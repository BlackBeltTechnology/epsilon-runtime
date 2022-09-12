package hu.blackbelt.epsilon.runtime.execution.exceptions;

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

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class ModelValidationException extends Exception {

    List<String> validationErrors;
    Resource resource;

    public ModelValidationException(List<String> validationErrors, Resource resource) {
        super("Invalid model\n" +
                validationErrors.stream()
                        .collect(Collectors.joining("\n")) + "\n" + resourceToString(resource));
        this.resource = resource;
        this.validationErrors = validationErrors;
    }

    private static String resourceToString(Resource resource) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            resource.save(byteArrayOutputStream, ImmutableMap.of());
        } catch (IOException e) {
        }
        return new String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset());
    }

}
