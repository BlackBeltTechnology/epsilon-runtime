package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.epsilon.egl.EglFileGeneratingTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactory;
import org.eclipse.epsilon.egl.EglTemplateFactoryModuleAdapter;
import org.eclipse.epsilon.egl.exceptions.EglRuntimeException;
import org.eclipse.epsilon.eol.IEolModule;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class EglExecutionContext extends EolExecutionContext {

    public static final String ARTIFACT_ROOT = "ARTIFACT_ROOT";

    @Getter
    @NonNull
    private String outputRoot;

    @Builder(builderMethodName = "eglExecutionContextBuilder")
    public EglExecutionContext(URI source, List<ProgramParameter> parameters, String outputRoot) {
        super(source, parameters);
        this.outputRoot = outputRoot;
    }

    protected EglTemplateFactory getTemplateFactory(Map<Object, Object> context) throws ScriptExecutionException {
        EglTemplateFactory templateFactory;
        try {
            templateFactory = EglFileGeneratingTemplateFactory.class.newInstance();
        } catch (InstantiationException | IllegalAccessException e1) {
            // TODO Auto-generated catch block
            throw new ScriptExecutionException("Could not instantiate templalte factory", e1);
        }

        File outputRootDir = null;
        if (outputRoot != null) {
            outputRootDir = new File(outputRoot);
            if (!outputRootDir.exists()) {
                outputRootDir.mkdirs();
            }
        }

        if (templateFactory instanceof EglFileGeneratingTemplateFactory && outputRoot != null) {
            try {
                if (outputRootDir != null) {
                    ((EglFileGeneratingTemplateFactory) templateFactory).setOutputRoot(outputRootDir.getAbsolutePath());
                }
                if (context.get(ARTIFACT_ROOT)!= null) {
                    URI main = (URI)context.get(ARTIFACT_ROOT);
                    ((EglFileGeneratingTemplateFactory) templateFactory).setRoot(main);
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
