package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.etl.EtlModule;

import java.util.List;
import java.util.Map;

public class EtlExecutionContext extends EolExecutionContext {

    @Getter
    @NonNull
    private String exportTransformationTrace;

    EtlModule etlModule = new EtlModule();

    @Builder(builderMethodName = "etlExecutionContextBuilder")
    public EtlExecutionContext(String source, List<ProgramParameter> parameters, String exportTransformationTrace, EtlModule etlModule) {
        super(source, parameters);
        this.exportTransformationTrace = exportTransformationTrace;
        if (etlModule != null) {
        	this.etlModule =  etlModule;
        }
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
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
