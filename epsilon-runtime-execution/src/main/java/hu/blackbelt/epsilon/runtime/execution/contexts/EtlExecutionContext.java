package hu.blackbelt.epsilon.runtime.execution.contexts;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
