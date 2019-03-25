package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import org.eclipse.epsilon.egl.EgxModule;
import org.eclipse.epsilon.eol.IEolModule;

import java.util.List;
import java.util.Map;

public class EgxExecutionContext extends EglExecutionContext {

    @Builder(builderMethodName = "egxExecutionContextBuilder")
    public EgxExecutionContext(String source, List<ProgramParameter> parameters, String outputRoot) {
        super(source, parameters, outputRoot);
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        EgxModule module = new EgxModule(getTemplateFactory(context));
        return module;
    }

}
