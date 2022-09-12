package hu.blackbelt.epsilon.runtime.execution.model.emf;

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

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.util.HashSet;
import java.util.Map;


@AllArgsConstructor
@Builder
public class DefaultRuntimeEmfModelFactory implements EmfModelFactory {

    @NonNull
    Log log;

    @Override
    public EmfModel create(ResourceSet resourceSet, Map<URI, URI> uriMapConverter) {
        return new EmfModel() {
            @Override
            @SneakyThrows
            protected ResourceSet createResourceSet() {
                ResourceSet emfReourceSet =  super.createResourceSet();

                for (URIHandler uriHandler : resourceSet.getURIConverter().getURIHandlers()) {
                    int idx = resourceSet.getURIConverter().getURIHandlers().indexOf(uriHandler);
                    if (!emfReourceSet.getURIConverter().getURIHandlers().contains(uriHandler)) {
                        log.info("    Adding uri handler: " + uriHandler.toString());
                        emfReourceSet.getURIConverter().getURIHandlers().add(idx, uriHandler);
                    }
                }

                for (URI key : resourceSet.getURIConverter().getURIMap().keySet()) {
                    if (!emfReourceSet.getURIConverter().getURIMap().containsKey(key)) {
                        URI value = resourceSet.getURIConverter().getURIMap().get(key);
                        log.info("    Adding reference URI converter: " + key + " -> " + value);
                        emfReourceSet.getURIConverter().getURIMap().put(key, value);
                    }
                }

                if (uriMapConverter != null) {
                    for (URI from : uriMapConverter.keySet()) {
                        URI to = uriMapConverter.get(from);
                        log.info(String.format("    Registering URI converter: %s -> %s", from.toString(), to.toString()));
                        emfReourceSet.getURIConverter().getURIMap().put(from, to);
                    }
                }


                for (String key : new HashSet<String>(resourceSet.getPackageRegistry().keySet())) {
                    EPackage ePackage = resourceSet.getPackageRegistry().getEPackage(key);
                    emfReourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
                }
                return emfReourceSet;
            }
        };
    }}
