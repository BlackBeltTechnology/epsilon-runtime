package hu.blackbelt.epsilon.runtime.execution.api;

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

import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

public interface ModelContext {

    IModel load(Logger log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uris, Map<URI, URI> uriConverterMap) throws EolModelLoadingException, ModelValidationException;
    void addAliases(ModelRepository repository, ModelReference ref);
    List<String> getAliases();

    Map<String, String> getArtifacts();
    Map<String, String> getUriConverterMap();

    String getName();

}
