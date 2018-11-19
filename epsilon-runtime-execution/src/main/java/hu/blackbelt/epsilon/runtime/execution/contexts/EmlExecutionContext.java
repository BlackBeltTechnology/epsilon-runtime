package hu.blackbelt.epsilon.runtime.execution.contexts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.IEolExecutableModule;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class EmlExecutionContext extends EtlExecutionContext {

    @Getter
    @NonNull
    private String useMatchTrace;

    @Builder.Default
    private EmlModule emlModule = new EmlModule();

    @Builder(builderMethodName = "emlExecutionContextBuilder")
    public EmlExecutionContext(String source, List<ProgramParameter> parameters, String useMatchTrace) {
        super(source, parameters);
        this.useMatchTrace = useMatchTrace;
    }

    @Override
    public IEolExecutableModule getModule(Map<Object, Object> context) {
        if (useMatchTrace != null) {
            emlModule.getContext().setMatchTrace((MatchTrace)context.get(useMatchTrace));
        }
        return emlModule;
    };
}
