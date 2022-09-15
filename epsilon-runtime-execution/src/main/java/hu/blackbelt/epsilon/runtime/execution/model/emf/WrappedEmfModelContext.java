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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import hu.blackbelt.epsilon.runtime.execution.impl.LogLevel;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import lombok.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Data
@Builder(builderMethodName = "wrappedEmfModelContextBuilder")
@AllArgsConstructor
@EqualsAndHashCode
public class WrappedEmfModelContext implements ModelContext {

    @Builder.Default
    Log log = new StringBuilderLogger(LogLevel.DEBUG);

    @NonNull
    Resource resource;

    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    String referenceUri;

    @Builder.Default
    Map<String, String> uriConverterMap = ImmutableMap.of();

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    @Builder.Default
    Boolean validateModel = true;

    @Builder.Default
    Boolean useCache = false;

    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uris, Map<URI, URI> uriConverterMap) throws EolModelLoadingException, ModelValidationException {
        // Hack: to able to resolve supertypes
        Map<URI, URI> uriMapExtended = Maps.newHashMap(uriConverterMap);
        uriMapExtended.put(URI.createURI(""), resource.getURI());

        ResourceWrappedEMFModel emfModel =  new ResourceWrappedEMFModel(resourceSet,  resource, uriMapExtended, useCache);

        final StringProperties properties = new StringProperties();
        properties.put(EmfModel.PROPERTY_NAME, emfModel.getName() + "");
        if (emfModel.getAliases() != null && emfModel.getAliases().size() > 0) {
            properties.put(EmfModel.PROPERTY_ALIASES, emfModel.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(EmfModel.PROPERTY_ALIASES, "");
        }
        properties.put(EmfModel.PROPERTY_MODEL_URI, resource.getURI());

        if (getReferenceUri() != null && !getReferenceUri().trim().equals("")) {
            properties.put(EmfModel.PROPERTY_MODEL_URI, getReferenceUri());
            log.info(String.format("Registering MODEL_URI: %s Alias URI: %s", resource.getURI().toString(), getReferenceUri().toString()));
            resourceSet.getURIConverter().getURIMap().put(URI.createURI(getReferenceUri()), resource.getURI());
        } else {
            log.info(String.format("Registering MODEL_URI: %s", resource.getURI().toString()));
        }

        emfModel.load(properties);
        emfModel.setName(getName());

        if (validateModel) {
            ModelValidator.validate(emfModel);
        }
        repository.addModel(emfModel);

        return emfModel;
    }

    @Override
    public void addAliases(ModelRepository repository, ModelReference ref) {
        ref.setName(this.getName());
        if (this.getAliases() != null) {
            for (String alias : this.getAliases()) {
                ref.getAliases().add(alias);
            }
        }
        repository.addModel(ref);
    }

    @Override
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of();
    }

    @Override
    public String toString() {
        return "WrappedEmfModelContext{" +
                "log=" + log +
                ", resource={class: " + resource.getClass() + " uri: " + resource.getURI() + "}" +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", uriConverterMap='" + getUriConverterMap() + '\'' +
                ", referenceUri='" + referenceUri + '\'' +
                '}';
    }

    class ResourceWrappedEMFModel extends EmfModel {

        Resource wrappedResource;
        ResourceSet wrappedResourceSet;
        Map<URI, URI> uriConverterMap;

        public ResourceWrappedEMFModel(ResourceSet resourceSet, Resource resource, Map<URI, URI> uriConverterMap, boolean useCache) {
            this.wrappedResource = resource;
            this.wrappedResourceSet = resourceSet;
            this.uriConverterMap = uriConverterMap;
            setCachingEnabled(useCache);
            setReadOnLoad(false);
            setStoredOnDisposal(false);
        }

        @Override
        @SneakyThrows
        protected ResourceSet createResourceSet() {
            ResourceSet resourceSet =  super.createResourceSet();

            for (URIHandler uriHandler : resourceSet.getURIConverter().getURIHandlers()) {
                int idx = resourceSet.getURIConverter().getURIHandlers().indexOf(uriHandler);
                if (!wrappedResourceSet.getURIConverter().getURIHandlers().contains(uriHandler)) {
                    log.info("    Adding uri handler: " + uriHandler.toString());
                    wrappedResourceSet.getURIConverter().getURIHandlers().add(idx, uriHandler);
                }
            }

            for (URI key : resourceSet.getURIConverter().getURIMap().keySet()) {
                if (!wrappedResourceSet.getURIConverter().getURIMap().containsKey(key)) {
                    URI value = resourceSet.getURIConverter().getURIMap().get(key);
                    log.info("    Adding reference URI converter: " + key + " -> " + value);
                    wrappedResourceSet.getURIConverter().getURIMap().put(key, value);
                }
            }

            if (uriConverterMap != null) {
                for (URI from : uriConverterMap.keySet()) {
                    URI to = uriConverterMap.get(from);
                    log.info(String.format("    Registering URI converter: %s -> %s", from.toString(), to.toString()));
                    wrappedResourceSet.getURIConverter().getURIMap().put(from, to);
                }
            }

            for (String key : new HashSet<String>(wrappedResourceSet.getPackageRegistry().keySet())) {
                EPackage ePackage = wrappedResourceSet.getPackageRegistry().getEPackage(key);
                resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
            }
            return resourceSet;
        }


        @Override
        protected void loadModel() throws EolModelLoadingException {
            super.loadModel();
            modelImpl = wrappedResource;
        }

        @Override
        public boolean store() {
            return false;
        }
    }

}
