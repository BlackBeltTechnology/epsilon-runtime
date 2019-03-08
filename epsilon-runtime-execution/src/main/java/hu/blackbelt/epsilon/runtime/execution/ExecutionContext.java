package hu.blackbelt.epsilon.runtime.execution;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EolExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.utils.AbbreviateUtils;
import hu.blackbelt.epsilon.runtime.utils.MD5Utils;
import hu.blackbelt.epsilon.runtime.utils.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.tools.EmfTool;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.eclipse.epsilon.profiling.Profiler;
import org.eclipse.epsilon.profiling.ProfilerTargetSummary;
import org.eclipse.epsilon.profiling.ProfilingExecutionListener;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.addUmlPackagesToResourceSet;

@Getter
@Builder
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
    private Log log = new Slf4jLog();

    private List<String> metaModels;

    private List<ModelContext> modelContexts;

    private ArtifactResolver artifactResolver;
    private File sourceDirectory;

    @Builder.Default
    private Boolean profile = false;


    @SneakyThrows
    public void load() {
        CachedResourceSet.getCache().clear();

        if (addUmlPackages) {
            addUmlPackagesToResourceSet(resourceSet);
        }
        addMetaModels();
        addModels();
    }


    public void rollback() {
        for (IModel model : projectModelRepository.getModels()) {
            model.setStoredOnDisposal(false);
        }
    }

    public void commit() {
        rollback = false;
    }

    public void disposeRepository() {
        if (projectModelRepository != null) {
            projectModelRepository.dispose();
        }
    }

    @SneakyThrows(Exception.class)
    public void executeProgram(EolExecutionContext eolProgram) {
        File sourceFile = new File(eolProgram.getSource());
        URI source = sourceFile.isAbsolute() ? sourceFile.toURI() : new File(sourceDirectory, eolProgram.getSource()).toURI();
        context.put(EglExecutionContext.ARTIFACT_ROOT, source);

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
                model.addAliases(repository, EmfUtils.createModelReference(modelContextMap.get(model)));
            }

        } else {
            eolModule.getContext().setModelRepository(projectModelRepository);
        }

        List<ProgramParameter> params = eolProgram.getParameters();
        if (params == null) {
            params = Lists.newArrayList();
        }

        log.info("Running program: " + source);

        executeModule(eolModule, source,
                Stream.concat(
                        params.stream().map(p -> Variable.createReadOnlyVariable(p.getName(), p.getValue())),
                        context.entrySet().stream().map(e -> Variable.createReadOnlyVariable(e.getKey().toString(), e.getValue())))
                        .collect(Collectors.toList()));


        eolProgram.post(context);

        if (!eolProgram.isOk()) {
            throw new ScriptExecutionException("Program aborted: " + eolProgram.toString());
        } else {
            log.info("Execution result: " + eolProgram.toString());
        }
    }

    @SneakyThrows
    private void executeModule(IEolModule eolModule, URI source, List<Variable> parameters) {
        for (IModel m : projectModelRepository.getModels()) {
            log.info("  Model: " + m.getName() + " Aliases: " + String.join(", ", m.getAliases()));
        }
        eolModule.parse(source);
        if (profile) {
            Profiler.INSTANCE.reset();
        }
        try {
            if (profile) {
                Profiler.INSTANCE.start(source.toString(), "", eolModule);
            }

            if (eolModule.getParseProblems().size() > 0) {
                log.error("Parse errors occured...");
                for (ParseProblem problem : eolModule.getParseProblems()) {
                    log.error(problem.toString());
                }
                throw new ScriptExecutionException("Parse error");
            }
            // Adding static utils
            eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable("UUIDUtils", new UUIDUtils()));
            eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable("MD5Utils", new MD5Utils()));
            eolModule.getContext().getFrameStack()
                    .put(Variable.createReadOnlyVariable("AbbreviateUtils", new AbbreviateUtils()));
            eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable("EMFTool", new EmfTool()));

            for (Variable parameter : parameters) {
                eolModule.getContext().getFrameStack().put(parameter);
            }
            eolModule.getContext().getFrameStack().put(Variable.createReadOnlyVariable("executionContext", this));

            if (profile) {
                eolModule.getContext().getExecutorFactory().addExecutionListener(new ProfilingExecutionListener());
            }

            StringBuffer sb = new StringBuffer();
            parameters.forEach(p -> sb.append("\t" + p.getName() + " - " + p.toString() + "\n"));
            log.info("Parameters: \n" + sb.toString());

            Object result = eolModule.execute();
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
    public void addMetaModel(String metaModel) {
        log.info("Registering ecore: " + metaModel);
        org.eclipse.emf.common.util.URI uri = artifactResolver.getArtifactAsEclipseURI(metaModel);
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
    public void addModel(ModelContext modelContext) {
        log.info("Model: " + modelContext.toString());
        Map<String, org.eclipse.emf.common.util.URI> uris = modelContext.getArtifacts().entrySet().stream()
                .filter(e -> !Strings.isNullOrEmpty(e.getValue()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> artifactResolver.getArtifactAsEclipseURI(entry.getValue())));
        uris.forEach((k,v) -> log.info("    Artifact " + k + " file: " + v.toString()));
        modelContextMap.put(modelContext, modelContext.load(log, resourceSet, projectModelRepository, uris));
    }


    @Override
    public void close() throws Exception {
        if (rollback) {
            rollback();
        }
        disposeRepository();
    }
}
