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
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eml.EmlModule;
import org.eclipse.epsilon.eol.IEolModule;

import java.net.URI;
import java.util.List;
import java.util.Map;

public class EmlExecutionContext extends EtlExecutionContext {

    @Getter
    @NonNull
    private String useMatchTrace;

    private final EmlModule emlModule;

    @Builder(builderMethodName = "emlExecutionContextBuilder")
    public EmlExecutionContext(URI source, List<ProgramParameter> parameters, String useMatchTrace, String exportTransformationTrace, EmlModule emlModule, Boolean parallel) {
        super(source, parameters, exportTransformationTrace, null, false, parallel);
        this.useMatchTrace = useMatchTrace;
        if (emlModule != null) {
        	this.emlModule = emlModule;
        } else {
            this.emlModule = new EmlModule();
        }
    }

    @Override
    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        if (useMatchTrace != null) {
            emlModule.getContext().setMatchTrace((MatchTrace)context.get(useMatchTrace));
        }
        return emlModule;
    };
}
