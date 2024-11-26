package hu.blackbelt.epsilon.runtime.execution.model.xml;

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

import hu.blackbelt.epsilon.runtime.execution.EmfUtils;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.emc.emf.xml.XmlModel;

import java.util.Map;

@AllArgsConstructor
@Builder
public class DefaultRuntimeXmlModelFactory implements XmlModelFactory {

    @NonNull
    Logger log;

    @Override
    public XmlModel create(ResourceSet resourceSet, Map<URI, URI> uriMapConverter) {
        return new XmlModel() {
            @Override
            @SneakyThrows
            protected ResourceSet createResourceSet() {
                ResourceSet emfReourceSet =  super.createResourceSet();
                EmfUtils.setupResourceSet(log, resourceSet, emfReourceSet, uriMapConverter);
                return emfReourceSet;
            }
        };
    }

}
