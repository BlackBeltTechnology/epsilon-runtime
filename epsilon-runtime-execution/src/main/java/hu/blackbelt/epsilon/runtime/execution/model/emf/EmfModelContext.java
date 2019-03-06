package hu.blackbelt.epsilon.runtime.execution.model.emf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class EmfModelContext implements ModelContext {

    @NonNull
    String model;

    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    File metaModelFile;

    String platformAlias;

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
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * This key is a comma-separated list of zero or more namespaces URI of some of the metamodels to which
     * this model conforms. Users may combine this key with  to loadEmf "fileBasedMetamodelUris"
     * both file-based and URI-based metamodels at the same time.
     */
    List<String> metaModelUris;

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    @Builder.Default
    Boolean validateModel = true;

    @Builder.Default
    EmfModelFactory emfModelFactory = new DefaultRuntimeEmfModelFactory();

    @java.beans.ConstructorProperties({"model", "name", "aliases", "metaModelFile", "platformAlias", "readOnLoad", "storeOnDisposal", "cached", "expand", "metaModelUris", "validateModel", "emfModelFactory"})
    public EmfModelContext(String model, String name, List<String> aliases, File metaModelFile, String platformAlias, boolean readOnLoad, boolean storeOnDisposal, boolean cached, boolean expand, List<String> metaModelUris, boolean validateModel, EmfModelFactory emfModelFactory) {
        this.model = model;
        this.name = name;
        this.aliases = aliases;
        this.metaModelFile = metaModelFile;
        this.platformAlias = platformAlias;
        this.readOnLoad = readOnLoad;
        this.storeOnDisposal = storeOnDisposal;
        this.cached = cached;
        this.expand = expand;
        this.metaModelUris = metaModelUris;
        this.validateModel = validateModel;
        this.emfModelFactory = emfModelFactory;
    }

    public EmfModelContext() {
    }


    @Override
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of("model", model);
    }

    @Override
    public String toString() {
        return "EmfModel{" +
                "artifacts='" + getArtifacts() + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", readOnLoad=" + readOnLoad +
                ", storeOnDisposal=" + storeOnDisposal +
                ", cached=" + cached +
                ", metaModelFile=" + metaModelFile +
                ", platformAlias='" + platformAlias + '\'' +
                ", expand=" + expand +
                ", metaModelUris=" + metaModelUris +
                ", validateModel='" + validateModel + '\'' +
                ", emfModelFactory='" + emfModelFactory.toString() + '\'' +
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
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap) throws EolModelLoadingException {
        return EmfModelUtils.loadEmf(log, emfModelFactory, resourceSet, repository, this, uriMap.get("model"));
    }


}
