package hu.blackbelt.epsilon.runtime.execution.model.emf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
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

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder(builderMethodName = "emfModelContextBuilder")
public class EmfModelContext implements ModelContext {

    @Builder.Default
    Log log = new Slf4jLog();


    @NonNull
    String model;

    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    File metaModelFile;

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


    EmfModelFactory emfModelFactory;

    @java.beans.ConstructorProperties({"log", "model", "name", "aliases", "metaModelFile", "referenceUri", "readOnLoad", "storeOnDisposal", "cached", "expand", "metaModelUris", "validateModel", "emfModelFactory"})
    public EmfModelContext(Log log, String model, String name, List<String> aliases, File metaModelFile, String referenceUri, boolean readOnLoad, boolean storeOnDisposal, boolean cached, boolean expand, List<String> metaModelUris, boolean validateModel, EmfModelFactory emfModelFactory) {
        this.log = log;
        this.model = model;
        this.name = name;
        this.aliases = aliases;
        this.metaModelFile = metaModelFile;
        this.referenceUri = referenceUri;
        this.readOnLoad = readOnLoad;
        this.storeOnDisposal = storeOnDisposal;
        this.cached = cached;
        this.expand = expand;
        this.metaModelUris = metaModelUris;
        this.validateModel = validateModel;
        if (emfModelFactory != null) {
            this.emfModelFactory = emfModelFactory;
        } else {
            this.emfModelFactory = new DefaultRuntimeEmfModelFactory(log);
        }

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
                ", referenceUri='" + referenceUri + '\'' +
                ", expand=" + expand +
                ", metaModelUris=" + metaModelUris +
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
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap) throws EolModelLoadingException {
        URI uri =  uriMap.get("model");
        IModel model = EmfModelFactory.loadEmf(log, emfModelFactory, resourceSet, repository, this, uri);
        return model;
    }


}
