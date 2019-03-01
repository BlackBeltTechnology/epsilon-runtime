package hu.blackbelt.epsilon.runtime.execution.model.emf;

import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import lombok.Builder;
import lombok.Data;
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

    Map<String, String> artifacts;

    String name;

    List<String> aliases;

    File metaModelFile;

    String platformAlias;

    @Builder.Default
    boolean readOnLoad = true;

    @Builder.Default
    boolean storeOnDisposal = false;

    @Builder.Default
    boolean cached = true;


    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * When paired with "true", external references will be resolved during loading.
     * Otherwise, external references are not resolved.
     *
     * Paired with "true" by default.
     */
    boolean expand;

    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * This key is a comma-separated list of zero or more namespaces URI of some of the metamodels to which
     * this model conforms. Users may combine this key with  to loadEmf "fileBasedMetamodelUris"
     * both file-based and URI-based metamodels at the same time.
     */
    List<String> metaModelUris;

    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * This key is a comma-separated list of zero or more {@link URI}s that can be used to locate some of the
     * metamodels to which this model conforms. Users may combine this key with "metaModelUris"
     * to loadEmf both file-based and URI-based metamodels at the same time.
     */
    List<String> fileBasedMetamodelUris;


    /**
     * One of the keys used to construct the first argument to {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * This key is paired with a {@link URI} that can be used to locate this model.
     * This key must always be paired with a value.
     */
    // public static final String PROPERTY_MODEL_URI = "modelUri";

    String modelUri;

    /**
     * One of the keys used to construct the first argument to
     * {@link org.eclipse.epsilon.emc.emf.EmfModel#load(StringProperties, String)}.
     *
     * This key is a Boolean value that if set to <code>true</code> (the
     * default), tries to reuse previously registered file-based EPackages that
     * have not been modified since the last time they were registered.
     */
    boolean reuseUnmodifiedFileBasedMetamodels;


    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    boolean validateModel;

    @java.beans.ConstructorProperties({"artifacts", "name", "aliases", "metaModelFile", "platformAlias", "readOnLoad", "storeOnDisposal", "cached", "expand", "metaModelUris", "fileBasedMetamodelUris", "modelUri", "reuseUnmodifiedFileBasedMetamodels", "validateModel"})
    public EmfModelContext(Map<String, String> artifacts, String name, List<String> aliases, File metaModelFile, String platformAlias, boolean readOnLoad, boolean storeOnDisposal, boolean cached, boolean expand, List<String> metaModelUris, List<String> fileBasedMetamodelUris, String modelUri, boolean reuseUnmodifiedFileBasedMetamodels, boolean validateModel) {
        this.artifacts = artifacts;
        this.name = name;
        this.aliases = aliases;
        this.metaModelFile = metaModelFile;
        this.platformAlias = platformAlias;
        this.readOnLoad = readOnLoad;
        this.storeOnDisposal = storeOnDisposal;
        this.cached = cached;
        this.expand = expand;
        this.metaModelUris = metaModelUris;
        this.fileBasedMetamodelUris = fileBasedMetamodelUris;
        this.modelUri = modelUri;
        this.reuseUnmodifiedFileBasedMetamodels = reuseUnmodifiedFileBasedMetamodels;
        this.validateModel = validateModel;
    }

    public EmfModelContext() {
    }


    @Override
    public String toString() {
        return "EmfModel{" +
                "artifacts='" + artifacts + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", readOnLoad=" + readOnLoad +
                ", storeOnDisposal=" + storeOnDisposal +
                ", cached=" + cached +
                ", metaModelFile=" + metaModelFile +
                ", platformAlias='" + platformAlias + '\'' +
                ", expand=" + expand +
                ", metaModelUris=" + metaModelUris +
                ", fileBasedMetamodelUris=" + fileBasedMetamodelUris +
                ", modelUri='" + modelUri + '\'' +
                ", reuseUnmodifiedFileBasedMetamodels=" + reuseUnmodifiedFileBasedMetamodels +
                ", validateModel='" + validateModel + '\'' +
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
        return EmfModelUtils.loadEmf(log, resourceSet, repository, this, uriMap.get("model"));
    }


}
