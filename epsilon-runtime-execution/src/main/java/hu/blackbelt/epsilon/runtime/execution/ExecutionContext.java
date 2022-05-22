package hu.blackbelt.epsilon.runtime.execution;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EolExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.epsilon.runtime.execution.impl.LogLevel;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import lombok.*;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.eclipse.epsilon.eol.types.EolAnyType;
import org.eclipse.epsilon.profiling.Profiler;
import org.eclipse.epsilon.profiling.ProfilerTargetSummary;
import org.eclipse.epsilon.profiling.ProfilingExecutionListener;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.addEmfPackagesToResourceSet;
import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.addUmlPackagesToResourceSet;

@Getter
@Builder(builderMethodName = "executionContextBuilder")
@AllArgsConstructor
public class ExecutionContext implements AutoCloseable {

    @Builder.Default
    private Map<ModelContext, IModel> modelContextMap = Maps.newConcurrentMap();

    @Builder.Default
    private Map<Object, Object> context = new HashMap();

    @Builder.Default
    private ResourceSet resourceSet = EmfUtils.initDefaultCachedResourceSet();

    @Builder.Default
    private ModelRepository projectModelRepository = new ModelRepository();

    @Builder.Default
    private Boolean rollback = true;

    @Builder.Default
    private Boolean addUmlPackages = false;

    @Builder.Default
    private Boolean addEcorePackages = false;

    @Builder.Default
    private Log log = new StringBuilderLogger(LogLevel.DEBUG);

    @Builder.Default
    private List<String> metaModels = ImmutableList.of();

    private List<ModelContext> modelContexts;

    //private File sourceDirectory;

    @Builder.Default
    private Boolean profile = false;

    @Builder.Default
    private Map<String, Object> injectContexts = new HashMap();

    @SneakyThrows
    @Synchronized
    public void load() {
        if (addUmlPackages) {
            addUmlPackagesToResourceSet(resourceSet);
        }

        if (addEcorePackages) {
            addEmfPackagesToResourceSet(resourceSet);
        }

        addMetaModels();

        log.debug("Registered packages: ");
        for (String key : new HashSet<String>(resourceSet.getPackageRegistry().keySet())) {
            EPackage ePackage = resourceSet.getPackageRegistry().getEPackage(key);
            log.debug("      Name: " +  ePackage.getName() + " nsURI: " + ePackage.getNsURI() + " nsPrefix: " + ePackage.getNsPrefix());
        }

        addModels();
    }


    @Synchronized
    public void rollback() {
        for (IModel model : projectModelRepository.getModels()) {
            model.setStoredOnDisposal(false);
        }
    }

    @Synchronized
    public void commit() {
        rollback = false;
    }

    @Synchronized
    public void disposeRepository() {
        if (projectModelRepository != null) {
            projectModelRepository.dispose();
        }
    }

    @Synchronized
    public void executeProgram(EolExecutionContext eolProgram) throws ScriptExecutionException {
        //File sourceFile = new File(eolProgram.getSource());
        //URI source = sourceFile.isAbsolute() ? sourceFile.toURI() : new File(sourceDirectory, eolProgram.getSource()).toURI();
        context.put(EglExecutionContext.ARTIFACT_ROOT, eolProgram.getSource());

        IEolModule eolModule = eolProgram.getModule(context);

        // Determinate any mode have alias or not
        boolean isAliasExists = false;
        for (ModelContext model : modelContextMap.keySet()) {
            if (model.getAliases() != null) {
                isAliasExists = true;
            }
        }

        if (isAliasExists) {
            ModelRepository repository = eolModule.getContext().getModelRepository();

            for (ModelContext model : modelContextMap.keySet()) {
                model.addAliases(repository, EpsilonUtils.createModelReference(modelContextMap.get(model)));
            }

        } else {
            eolModule.getContext().setModelRepository(projectModelRepository);
        }

        List<ProgramParameter> params = eolProgram.getParameters();
        if (params == null) {
            params = Lists.newArrayList();
        }

        log.info("Running program: " + eolProgram.getSource().toString());

        try {
            executeModule(eolModule, eolProgram.getSource(),
                    Stream.concat(
                                    params.stream().map(p -> Variable.createReadOnlyVariable(p.getName(), p.getValue())),
                                    context.entrySet().stream().map(e -> Variable.createReadOnlyVariable(e.getKey().toString(), e.getValue())))
                            .collect(Collectors.toList()));

            eolProgram.post(context);
        } finally {
            if (eolModule != null && eolModule.getContext() != null) {
                eolModule.getContext().dispose();
            }
        }

        if (!eolProgram.isOk()) {
            throw new ScriptExecutionException("Program aborted: " + eolProgram.toString());
        } else {
            log.info("Execution result: " + eolProgram.toString());
        }
    }

    private void executeModule(IEolModule eolModule, URI source, List<Variable> parameters) throws ScriptExecutionException {
        for (IModel m : projectModelRepository.getModels()) {
            log.info("  Model: " + m.getName() + " Aliases: " + String.join(", ", m.getAliases()));
        }

        try {
            eolModule.parse(source);
        } catch (Exception e) {
            throw new ScriptExecutionException("Error on parsing: " + source.toString(), e);
        }
        if (profile) {
            Profiler.INSTANCE.reset();
        }
        try {
            if (profile) {
                Profiler.INSTANCE.start(source.toString(), "", eolModule);
            }

            eolModule.getContext().getFrameStack().put(new org.eclipse.epsilon.eol.execute.context.Variable("log", log, new EolAnyType()));
            injectContexts.forEach((n, v) -> eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable(n, v)));
            parameters.forEach(p -> eolModule.getContext().getFrameStack().put(p));
            eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable("executionContext", this));

            if (profile) {
                eolModule.getContext().getExecutorFactory().addExecutionListener(new ProfilingExecutionListener());
            }

            if (eolModule.getParseProblems().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (ParseProblem problem : eolModule.getParseProblems()) {
                    sb.append("\t" + problem.toString() + "\n");
                }
                throw new ScriptExecutionException("Parse error: " + sb.toString());
            }

            StringBuffer sb = new StringBuffer();
            parameters.forEach(p -> sb.append("\t" + p.getName() + " - " + p.toString() + "\n"));
            log.info("Parameters: \n" + sb.toString());

            try {
                Object result = eolModule.execute();
            } catch (EolRuntimeException e) {
                throw new ScriptExecutionException("Program execute: " + e.getMessage() + "\tat " + e.getAst().getUri() + " (" + e.getLine() + ", " + e.getColumn() + ")", e);
            }
            // getLog().info("EolExecutionContext executeAll result: " + result.toString());
        } finally {
            if (profile) {
                Profiler.INSTANCE.stop(source.toString());
                for (ProfilerTargetSummary p : Profiler.INSTANCE.getTargetSummaries()) {
                    log.info(String.format("Index: %d Name: %s: Count: %d Individual Time: %d Aggregate time: %d",
                            p.getIndex(), p.getName(), p.getExecutionCount(), p.getExecutionTime().getIndividual(),
                            p.getExecutionTime().getAggregate()));
                }
            }
        }
    }

    @SneakyThrows
    private void addMetaModels() {
        for (String metaModel : metaModels) {
            addMetaModel(metaModel);
        }
    }

    @SneakyThrows
    private void addMetaModel(String metaModel) {
        log.info("Registering ecore: " + metaModel);
        org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createURI(metaModel);
        log.info("    Meta model: " + uri);
        List<EPackage> ePackages = EmfUtils.register(resourceSet, uri, true);
        log.info("    EPackages: " + ePackages.stream().map(e -> e.getNsURI()).collect(Collectors.joining(", ")));
    }


    @SneakyThrows
    private void addModels() {
        for (ModelContext modelContext : modelContexts) {
            addModel(modelContext);
        }
    }

    @SneakyThrows
    private void addModel(ModelContext modelContext) {
        log.info("Model: " + modelContext.toString());

        Map<String, org.eclipse.emf.common.util.URI> uris = modelContext.getArtifacts().entrySet().stream()
                .filter(e -> !Strings.isNullOrEmpty(e.getValue()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> org.eclipse.emf.common.util.URI.createURI(entry.getValue())));


        Map<org.eclipse.emf.common.util.URI, org.eclipse.emf.common.util.URI> uriConverters =
                modelContext.getUriConverterMap().entrySet().stream()
                .filter(e -> !Strings.isNullOrEmpty(e.getValue()))
                .collect(Collectors.toMap(
                        entry -> org.eclipse.emf.common.util.URI.createURI(entry.getKey()),
                        entry -> org.eclipse.emf.common.util.URI.createURI(entry.getValue())));

        uris.forEach((k,v) -> log.info("    Artifact " + k + " file: " + v.toString()));
        modelContextMap.put(modelContext, modelContext.load(log, resourceSet, projectModelRepository, uris, uriConverters));
    }


    @Override
    public void close() throws Exception {
        if (rollback) {
            rollback();
        }
        disposeRepository();
    }
}
