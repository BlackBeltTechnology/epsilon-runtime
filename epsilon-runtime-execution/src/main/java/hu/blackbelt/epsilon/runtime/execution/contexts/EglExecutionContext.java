package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.UriResolver;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.epsilon.runtime.execution.impl.DefaultContentTypeRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.egl.config.ContentTypeRepository;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.eol.IEolModule;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class EglExecutionContext extends EolExecutionContext {

    public static final String ARTIFACT_ROOT = "ARTIFACT_ROOT";
    public static final String CONTENTTYPE_REPOSITORY = "CONTENTTYPE_REPOSITORY";
    public static final String DEFAULT_CONFIG_XML = "org/eclipse/epsilon/egl/config/DefaultConfig.xml";

    @Getter
    @NonNull
    private String outputRoot;

    private ContentTypeRepository defaultContentTypeRepository;

    @Builder(builderMethodName = "eglExecutionContextBuilder")
    public EglExecutionContext(URI source, List<ProgramParameter> parameters, String outputRoot, Boolean parallel) {
        super(source, parameters, false, parallel);
        try {
            defaultContentTypeRepository = new DefaultContentTypeRepository(
                    UriResolver.calculateURI(this.getClass(), DEFAULT_CONFIG_XML).toURL().openStream());
        } catch (Exception e) {
            throw new RuntimeException("Could not load content type repositoty", e);
        }
        this.outputRoot = outputRoot;
    }

    protected EglTemplateFactory getTemplateFactory(Map<Object, Object> context) throws ScriptExecutionException {
        EglFileGeneratingTemplateFactory templateFactory = new EglFileGeneratingTemplateFactory();
        templateFactory.getContext().setContentTypeRepository(defaultContentTypeRepository);

        ContentTypeRepository contentTypeRepository = (ContentTypeRepository) context.get(CONTENTTYPE_REPOSITORY);
        if (contentTypeRepository != null) {
            templateFactory.getContext().setContentTypeRepository(contentTypeRepository);
        }

        File outputRootDir = null;
        if (outputRoot != null) {
            outputRootDir = new File(outputRoot);
            if (!outputRootDir.exists()) {
                outputRootDir.mkdirs();
            }
            try {
                templateFactory.setOutputRoot(outputRootDir.getAbsolutePath());
                if (context.get(ARTIFACT_ROOT)!= null) {
                    URI main = (URI)context.get(ARTIFACT_ROOT);
                    templateFactory.setRoot(main);
                } else {
                    throw new ScriptExecutionException("Artifact must be set!");
                }
            } catch (EglRuntimeException e) {
                throw new ScriptExecutionException("Could not create template factory", e);
            }
        }
        return templateFactory;
    }


    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        EglTemplateFactoryModuleAdapter module = new EglTemplateFactoryModuleAdapter(getTemplateFactory(context));
        return module;
    }
}
