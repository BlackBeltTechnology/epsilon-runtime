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
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.concurrent.EclModuleParallel;
import org.eclipse.epsilon.ecl.concurrent.EclModuleParallelAnnotation;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.concurrent.EvlModuleParallelElements;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
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
            // TODO: Remove when JNG-3096 Resolved
        } else if (Boolean.getBoolean("disableEpsilonParallel")) {
            this.eclModule = new EclModule();
        } else if (parallel == null || parallel) {
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
