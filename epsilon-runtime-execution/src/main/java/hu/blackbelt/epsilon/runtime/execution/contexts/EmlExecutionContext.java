package hu.blackbelt.epsilon.runtime.execution.contexts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.IEolModule;

import java.util.List;
import java.util.Map;

public class EmlExecutionContext extends EtlExecutionContext {

    @Getter
    @NonNull
    private String useMatchTrace;

    private EmlModule emlModule = new EmlModule();

    @Builder(builderMethodName = "emlExecutionContextBuilder")
    public EmlExecutionContext(String source, List<ProgramParameter> parameters, String useMatchTrace, String exportTransformationTrace, EmlModule emlModule) {
        super(source, parameters, exportTransformationTrace, null);
        this.useMatchTrace = useMatchTrace;
        if (emlModule != null) {
        	this.emlModule = emlModule;
        }
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) {
        if (useMatchTrace != null) {
            emlModule.getContext().setMatchTrace((MatchTrace)context.get(useMatchTrace));
        }
        return emlModule;
    };
}
