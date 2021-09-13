package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.egl.concurrent.EgxModuleParallelAnnotation;
import org.eclipse.epsilon.eol.IEolModule;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class EgxExecutionContext extends EglExecutionContext {
    Boolean parallel = false;

    @Builder(builderMethodName = "egxExecutionContextBuilder")
    public EgxExecutionContext(URI source, List<ProgramParameter> parameters, String outputRoot, Boolean parallel) {
        super(source, parameters, outputRoot, parallel);
        this.parallel = parallel;
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        return new EgxModule(getTemplateFactory(context));
    }

}
