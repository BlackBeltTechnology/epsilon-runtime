package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.concurrent.EclModuleParallel;
import org.eclipse.epsilon.ecl.concurrent.EclModuleParallelAnnotation;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eol.IEolModule;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class EclExecutionContext extends EolExecutionContext {

    @Getter
    @NonNull
    private String exportMatchTrace;

    @Getter
    @NonNull
    private String useMatchTrace;

    private final EclModule eclModule;

    @Builder(builderMethodName = "eclExecutionContextBuilder")
    public EclExecutionContext(URI source, List<ProgramParameter> parameters, String useMatchTrace, String exportMatchTrace, EclModule eclModule, Boolean parallel) {
        super(source, parameters, false, parallel);
        this.useMatchTrace = useMatchTrace;
        this.exportMatchTrace = exportMatchTrace;
        if (eclModule != null) {
            this.eclModule = eclModule;
        } else if (parallel != null && parallel) {
            // TODO: Not supported yet, newer version have to be released
            // this.eclModule = new EclModuleParallelAnnotation();
            this.eclModule = new EclModule();
        } else {
            this.eclModule = new EclModule();
        }
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
