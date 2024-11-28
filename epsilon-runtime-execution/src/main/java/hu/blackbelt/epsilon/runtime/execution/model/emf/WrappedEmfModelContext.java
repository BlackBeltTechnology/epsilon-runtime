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
import hu.blackbelt.epsilon.runtime.execution.EmfUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.slf4j.Logger;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import hu.blackbelt.epsilon.runtime.execution.impl.LogLevel;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import lombok.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.joining;

@EqualsAndHashCode
@Getter
public class WrappedEmfModelContext implements ModelContext {

    Logger log;

    Resource resource;

    String name;

    List<String> aliases;

    String referenceUri;

    Map<String, String> uriConverterMap;

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    Boolean validateModel;

    Boolean useCache;

    Boolean parallel;

    Boolean expandReference;

    ResourceSet wrappedResourceSet;

    ResourceSet resourceSet;

    @Getter
    private EmfModel emfModel;

    @Builder(builderMethodName = "wrappedEmfModelContextBuilder")
    public WrappedEmfModelContext(
            Logger log,
            @NonNull Resource resource,
            @NonNull String name,
            List<String> aliases,
            String referenceUri,
            Map<String, String> uriConverterMap,
            Boolean validateModel,
            Boolean useCache,
            Boolean parallel,
            Boolean expandReference,
            ResourceSet wrappedResourceSet
    ) {
        this.log = Objects.requireNonNullElseGet(log, () -> new StringBuilderLogger(LogLevel.DEBUG));
        this.resource = resource;
        this.name = name;
        this.aliases = Objects.requireNonNullElseGet(aliases, () -> ImmutableList.of());
        this.referenceUri = referenceUri;
        this.uriConverterMap = Objects.requireNonNullElseGet(uriConverterMap, () -> ImmutableMap.of());
        this.validateModel = Objects.requireNonNullElse(validateModel, true);
        this.useCache = Objects.requireNonNullElse(useCache, false);
        this.parallel = Objects.requireNonNullElse(parallel, true);
        this.expandReference = Objects.requireNonNullElse(expandReference, false);
        this.wrappedResourceSet = Objects.requireNonNullElseGet(wrappedResourceSet, () -> resource.getResourceSet());
    }

    public WrappedEmfModelContext() {
    }
    @Override
    public IModel load(Logger log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uris, Map<URI, URI> uriConverterMap) throws EolModelLoadingException, ModelValidationException {
        emfModel = new InMemoryEmfModel(name, resource, resource.getResourceSet().getPackageRegistry().values().stream().map(o -> (EPackage) o).toList()) {
            @Override
            public Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
                try {
                    return super.getCacheKeyForType(type);
                } catch (EolModelElementTypeNotFoundException ex) {
                }
                return type;
            }

            @Override
            synchronized public void setupContainmentChangeListeners() {
                synchronized (resource) {
                    int cnt = 0;
                    boolean success = false;
                    ConcurrentModificationException exception = null;
                    while (cnt < 10 && !success) {
                        try {
                            super.setupContainmentChangeListeners();
                            success = true;
                        } catch (ConcurrentModificationException e) {
                            exception = e;
                        }
                    }
                    if (!success && exception != null) {
                        throw exception;
                    }
                }
            }
        };
        emfModel.setName(name);
        this.resourceSet = emfModel.getResource().getResourceSet();

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
            log.debug(String.format("Registering MODEL_URI: %s Alias URI: %s", resource.getURI().toString(), getReferenceUri().toString()));
            resourceSet.getURIConverter().getURIMap().put(URI.createURI(getReferenceUri()), resource.getURI());
        } else {
            log.debug(String.format("Registering MODEL_URI: %s", resource.getURI().toString()));
        }
        if (parallel) {
            properties.put(EmfModel.PROPERTY_CONCURRENT, true);
            emfModel.setParallelAllOf(true);
            emfModel.setConcurrent(true);
        }
        if (useCache) {
            properties.put(EmfModel.PROPERTY_CACHED, true);
            emfModel.setCachingEnabled(true);
        }

        synchronized ($LOCK) {
            emfModel.load(properties);

            if (validateModel) {
                ModelValidator.validate(emfModel);
            }
            repository.addModel(emfModel);
        }

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
                ", referenceUri='" + referenceUri + '\'' +
                ", useCache=" + useCache + "" +
                ", parallel=" + parallel + "" +
                '}';
    }

}
