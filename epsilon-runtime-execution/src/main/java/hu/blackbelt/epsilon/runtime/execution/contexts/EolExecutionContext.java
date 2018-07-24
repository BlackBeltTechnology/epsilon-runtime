package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolExecutableModule;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class EolExecutionContext {
    @Getter
    @NonNull
    private String source;

    @Getter
    @NonNull
    private List<ProgramParameter> parameters;

    @Builder.Default
    private EolModule module = new EolModule();

    @Builder(builderMethodName = "eolExecutionContextBuilder")
    public EolExecutionContext(String source, List<ProgramParameter> parameters) {
        this.source = source;
        this.parameters = parameters;
    }

    public IEolExecutableModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        return module;
    };

    public boolean isOk() {
        return true;
    }

    public String toString() {
        return "";
    }
    
    public void post(Map<Object, Object> context) {}

}
