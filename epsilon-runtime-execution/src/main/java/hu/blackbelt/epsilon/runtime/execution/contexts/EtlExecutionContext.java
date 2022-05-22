package hu.blackbelt.epsilon.runtime.execution.contexts;

import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.etl.concurrent.EtlModuleParallel;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
public class EtlExecutionContext extends EolExecutionContext {

    @Getter
    @NonNull
    private String exportTransformationTrace;

    final EtlModule etlModule;

    @Builder(builderMethodName = "etlExecutionContextBuilder")
    public EtlExecutionContext(URI source, List<ProgramParameter> parameters, String exportTransformationTrace, EtlModule etlModule, Boolean createModule, Boolean parallel) {
        super(source, parameters, false, parallel);
        this.exportTransformationTrace = exportTransformationTrace;
        if (createModule == null || createModule) {
            if (etlModule != null) {
                this.etlModule = etlModule;
            // TODO: Remove when JNG-3096 Resolved
            } else if (Boolean.getBoolean("disableEpsilonParallel")) {
                this.etlModule = new EtlModule();
            } else if (parallel == null || parallel) {
                this.etlModule = new EtlModuleParallel();
            } else {
                this.etlModule = new EtlModule();
            }
        } else {
            this.etlModule = null;
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
