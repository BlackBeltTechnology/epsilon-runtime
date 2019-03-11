package hu.blackbelt.epsilon.runtime.execution.model.emf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import lombok.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
    Log log = new Slf4jLog();

    @NonNull
    Resource resource;

    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    String referenceUri;

    @Builder.Default
    Map<String, String> uriConverterMap = ImmutableMap.of();

    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uris, Map<URI, URI> uriConverterMap) throws EolModelLoadingException {
        IModel emfModel =  new ResourceWrappedEMFModel(resourceSet,  resource);

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
                ", resource=" + resource +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", uriConverterMap='" + getUriConverterMap() + '\'' +
                ", referenceUri='" + referenceUri + '\'' +
                '}';
    }

    class ResourceWrappedEMFModel extends EmfModel {

        Resource wrappedResource;
        ResourceSet wrappedResourceSet;

        public ResourceWrappedEMFModel(ResourceSet resourceSet, Resource resource) {
            this.wrappedResource = resource;
            this.wrappedResourceSet = resourceSet;
            readOnLoad = false;
            storeOnDisposal =  false;
            cachingEnabled = true;
        }

        @Override
        @SneakyThrows
        protected ResourceSet createResourceSet() {
            ResourceSet resourceSet =  super.createResourceSet();
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
