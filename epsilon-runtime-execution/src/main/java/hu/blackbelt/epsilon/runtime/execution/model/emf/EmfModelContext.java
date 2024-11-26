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
import lombok.*;
import org.slf4j.Logger;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import hu.blackbelt.epsilon.runtime.execution.impl.LogLevel;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@EqualsAndHashCode
public class EmfModelContext implements ModelContext {

    public static final String MODEL = "model";

    Logger log;

    @NonNull
    String emf;

    @NonNull
    String name;

    List<String> aliases;

    String referenceUri;

    Boolean readOnLoad;

    Boolean storeOnDisposal;

    Boolean cached;

    Boolean parallel;


    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * When paired with "true", external references will be resolved during loading.
     * Otherwise, external references are not resolved.
     *
     * Paired with "true" by default.
     */
    Boolean expand;

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    Boolean validateModel;

    EmfModelFactory emfModelFactory;

    Map<String, String> uriConverterMap;

    Boolean addExecutionMetModels;

    @java.beans.ConstructorProperties({"log", "emf", "name", "aliases", "referenceUri", "readOnLoad", "storeOnDisposal", "cached", "parallel", "expand", "metaModelUris", "validateModel", "addExecutionMetModels", "emfModelFactory", "uriConverterMap"})
    @Builder(builderMethodName = "emfModelContextBuilder")
    public EmfModelContext(Logger log,
                           String emf,
                           @NonNull
                           String name,
                           List<String> aliases,
                           String referenceUri,
                           Boolean readOnLoad,
                           Boolean storeOnDisposal,
                           Boolean cached,
                           Boolean parallel,
                           Boolean expand,
                           Boolean validateModel,
                           Boolean addExecutionMetModels,
                           EmfModelFactory emfModelFactory,
                           Map<String, String> uriConverterMap) {
        this.log = Objects.requireNonNullElseGet(log, () -> new StringBuilderLogger(LogLevel.DEBUG));
        this.emf = emf;
        this.name = name;
        this.aliases = Objects.requireNonNullElseGet(aliases, () -> ImmutableList.of());
        this.referenceUri = referenceUri;
        this.readOnLoad = Objects.requireNonNullElse(readOnLoad, true);
        this.storeOnDisposal = Objects.requireNonNullElse(storeOnDisposal, false);
        this.cached = Objects.requireNonNullElse(cached, true);
        this.parallel = Objects.requireNonNullElse(parallel, false);
        this.expand = Objects.requireNonNullElse(expand, true);
        this.validateModel = Objects.requireNonNullElse(validateModel, true);
        this.emfModelFactory = Objects.requireNonNullElseGet(emfModelFactory, () -> new DefaultRuntimeEmfModelFactory(this.log));
        this.uriConverterMap = Objects.requireNonNullElseGet(uriConverterMap, () -> ImmutableMap.of());
        this.addExecutionMetModels = Objects.requireNonNullElse(addExecutionMetModels, true);
    }

    public EmfModelContext() {
    }

    @Override
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of(MODEL, emf);
    }

    @Override
    public String toString() {
        return "EmfModel{" +
                "artifacts='" + getArtifacts() + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", uriConverterMap='" + getUriConverterMap() + '\'' +
                ", readOnLoad=" + readOnLoad +
                ", storeOnDisposal=" + storeOnDisposal +
                ", cached=" + cached +
                ", parallel=" + parallel +
                ", referenceUri='" + referenceUri + '\'' +
                ", expand=" + expand +
                ", validateModel='" + validateModel + '\'' +
                ", emfModelFactory='" + emfModelFactory.getClass().getName() + '\'' +
                ", log='" + log.getClass().getName() + '\'' +
                '}';
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
    public IModel load(Logger log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap, Map<URI, URI> uriConverterMap) throws EolModelLoadingException, ModelValidationException {
        return EmfModelUtils.loadEmf(log, emfModelFactory, resourceSet, repository, this, uriMap.get(MODEL), uriConverterMap);
    }

}
