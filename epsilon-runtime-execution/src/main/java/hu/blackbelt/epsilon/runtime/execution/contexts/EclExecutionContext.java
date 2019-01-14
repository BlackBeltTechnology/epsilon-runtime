package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eol.IEolModule;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class EclExecutionContext extends EolExecutionContext {

    @Getter
    @NonNull
    private String exportMatchTrace;

    @Getter
    @NonNull
    private String useMatchTrace;

    @Builder.Default
    private EclModule eclModule = new EclModule();

    @Builder(builderMethodName = "eclExecutionContextBuilder")
    public EclExecutionContext(String source, List<ProgramParameter> parameters, String useMatchTrace, String exportMatchTrace) {
        super(source, parameters);
        this.useMatchTrace = useMatchTrace;
        this.exportMatchTrace = exportMatchTrace;
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        if (useMatchTrace != null) {
            if (context.get(useMatchTrace) != null) {
                eclModule.getContext().setMatchTrace((MatchTrace)context.get(useMatchTrace));
            } else {
                eclModule.getContext().setMatchTrace(new MatchTrace());
            }
        }
        return eclModule;
    };
    
    @Override
    public void post(Map<Object, Object> context) {
         if (exportMatchTrace != null) {
            context.put(
                exportMatchTrace,
                eclModule.getContext().getMatchTrace().getReduced());
        }
    }
}
