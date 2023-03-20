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
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.concurrent.EolModuleParallel;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
public class EolExecutionContext {
    @Getter
    @NonNull
    private URI source;

    @Getter
    @NonNull
    private List<ProgramParameter> parameters;

    private final EolModule module;

    @Builder(builderMethodName = "eolExecutionContextBuilder")
    public EolExecutionContext(URI source, List<ProgramParameter> parameters, Boolean createModule, Boolean parallel) {
        this.source = source;
        this.parameters = parameters;
        if (createModule == null || createModule) {
            // TODO: Remove when JNG-3096 Resolved
            if (Boolean.getBoolean("disableEpsilonParallel")) {
                module = new EolModule();
            } else if (parallel == null || parallel) {
                module = new EolModuleParallel();
            } else {
                module = new EolModule();
            }
        } else {
            module = null;
        }
    }

    public IEolModule getModule(Map<Object, Object> context) throws ScriptExecutionException {
        return module;
    };

    public boolean isOk() {
        return true;
    }

    public String toString() {
        return "";
    }

    public void post(Map<Object, Object> context) throws ScriptExecutionException {}

}
