package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.concurrent.EolModuleParallel;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class EolExecutionContext {
    @Getter
    @NonNull
    private URI source;

    @Getter
    @NonNull
    private List<ProgramParameter> parameters;

    private final EolModule module;

    @Builder(builderMethodName = "eolExecutionContextBuilder")
    public EolExecutionContext(URI source, List<ProgramParameter> parameters, Boolean createModule, Boolean parallel) {
        this.source = source;
        this.parameters = parameters;
        if (createModule == null || createModule) {
            if (parallel != null && parallel) {
                module = new EolModuleParallel();
            } else {
                module = new EolModule();
            }
        } else {
            module = null;
        }
    }

    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        return module;
    };

    public boolean isOk() {
        return true;
    }

    public String toString() {
        return "";
    }
    
    public void post(Map<Object, Object> context) throws ScriptExecutionException {}

}
