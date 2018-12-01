package hu.blackbelt.epsilon.runtime.execution.contexts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eol.IEolExecutableModule;
import org.eclipse.epsilon.etl.EtlModule;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class EtlExecutionContext extends EolExecutionContext {

    @Getter
    @NonNull
    private String exportTransformationTrace;

    @Builder.Default
    EtlModule etlModule = new EtlModule();

    @Builder(builderMethodName = "etlExecutionContextBuilder")
    public EtlExecutionContext(String source, List<ProgramParameter> parameters, String exportTransformationTrace) {
        super(source, parameters);
        this.exportTransformationTrace = exportTransformationTrace;
    }

    @Override
    public IEolExecutableModule getModule(Map<Object, Object> context) {
        return etlModule;
    };

    @Override
    public void post(Map<Object, Object> context) {
        if (exportTransformationTrace != null) {
            context.put(
                    exportTransformationTrace,
                    etlModule.getContext().getTransformationTrace());
        }
    }


}
