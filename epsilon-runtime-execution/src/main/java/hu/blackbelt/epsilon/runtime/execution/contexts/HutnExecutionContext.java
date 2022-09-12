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

import hu.blackbelt.epsilon.runtime.execution.api.LoggingOutputStream;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.parse.problem.ParseProblem;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.eclipse.epsilon.etl.EtlModule;
import org.eclipse.epsilon.hutn.HutnContext;
import org.eclipse.epsilon.hutn.HutnModule;

import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class HutnExecutionContext {
    @Getter
    @NonNull
    private URI source;

    @Getter
    @NonNull
    private URI target;

    @Builder.Default
    HutnModule module = new HutnModule() {
        @Override
        public Object execute() throws EolRuntimeException {
            return super.execute();
        }
    };

    @Builder(builderMethodName = "hutnExecutionContextBuilder")
    public HutnExecutionContext(URI source, URI target, ModelRepository  modelRepository) {
        this.source = source;
        this.target = target;
        setHutnContext(module, modelRepository);
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
    
    public void post(Map<Object, Object> context) {}

    private void setHutnContext(HutnModule module, ModelRepository modelRepository) {
        HutnContext context = new HutnContext(module);
        context.setErrorStream(new PrintStream(new LoggingOutputStream(log, LoggingOutputStream.LogLevel.ERROR)));
        context.setWarningStream(new PrintStream(new LoggingOutputStream(log, LoggingOutputStream.LogLevel.WARN)));
        context.setOutputStream(new PrintStream(new LoggingOutputStream(log, LoggingOutputStream.LogLevel.INFO)));
        context.setModelRepository(modelRepository);
        module.setContext(context);
    }

/*
    private void parseHutnAndStoreModel(HutnModule module)
            throws Exception {
        log.info("Start parsing HUTN file");

        if (module.parse(source)) {
            log.info("Parsing successfull, storing transformed emf model");
            List<File> files = module.storeEmfModel(target.getParentFile(), target.getName(), "any");
            for (File file : files) {
                log.info("Transformed: " + file);
            }
        } else {
            StringBuffer sb = new StringBuffer();
            for (ParseProblem p : module.getParseProblems()) {
                sb.append("\n\t" + p.toString());
            }

            throw new Exception(sb.toString());
        }
    }
*/

}
