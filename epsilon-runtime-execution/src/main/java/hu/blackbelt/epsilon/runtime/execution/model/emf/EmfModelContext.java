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
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import hu.blackbelt.epsilon.runtime.execution.impl.LogLevel;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.List;
import java.util.Map;

@Data
@Builder(builderMethodName = "emfModelContextBuilder")
@EqualsAndHashCode
public class EmfModelContext implements ModelContext {

    public static final String MODEL = "model";
    @Builder.Default
    Log log = new StringBuilderLogger(LogLevel.DEBUG);

    @NonNull
    String emf;

    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    String referenceUri;

    @Builder.Default
    Boolean readOnLoad = true;

    @Builder.Default
    Boolean storeOnDisposal = false;

    @Builder.Default
    Boolean cached = true;

    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * When paired with "true", external references will be resolved during loading.
     * Otherwise, external references are not resolved.
     *
     * Paired with "true" by default.
     */
    @Builder.Default
    Boolean expand = true;

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    @Builder.Default
    Boolean validateModel = true;

    EmfModelFactory emfModelFactory;

    @Builder.Default
    Map<String, String> uriConverterMap = ImmutableMap.of();

    @java.beans.ConstructorProperties({"log", "emf", "name", "aliases", "referenceUri", "readOnLoad", "storeOnDisposal", "cached", "expand", "metaModelUris", "validateModel", "emfModelFactory", "uriConverterMap"})
    public EmfModelContext(Log log, String emf, String name, List<String> aliases, String referenceUri, boolean readOnLoad, boolean storeOnDisposal, boolean cached, boolean expand, boolean validateModel, EmfModelFactory emfModelFactory, Map<String, String> uriConverterMap) {
        this.log = log;
        this.emf = emf;
        this.name = name;
        this.aliases = aliases;
        this.referenceUri = referenceUri;
        this.readOnLoad = readOnLoad;
        this.storeOnDisposal = storeOnDisposal;
        this.cached = cached;
        this.expand = expand;
        this.validateModel = validateModel;
        if (emfModelFactory != null) {
            this.emfModelFactory = emfModelFactory;
        } else {
            this.emfModelFactory = new DefaultRuntimeEmfModelFactory(log);
        }
        if (uriConverterMap != null) {
            this.uriConverterMap = uriConverterMap;
        } else {
            this.uriConverterMap = ImmutableMap.of();
        }

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
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap, Map<URI, URI> uriConverterMap) throws EolModelLoadingException, ModelValidationException {
        return EmfModelUtils.loadEmf(log, emfModelFactory, resourceSet, repository, this, uriMap.get(MODEL), uriConverterMap);
    }

}
