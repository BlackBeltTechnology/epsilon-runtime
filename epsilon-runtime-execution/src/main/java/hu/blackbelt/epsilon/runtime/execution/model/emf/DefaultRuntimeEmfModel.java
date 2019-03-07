package hu.blackbelt.epsilon.runtime.execution.model.emf;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

import java.util.HashMap;

import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.loadResourceToResourceSet;

@Slf4j
public class DefaultRuntimeEmfModel extends EmfModel {

    ResourceSet resourceSet;

    public DefaultRuntimeEmfModel(ResourceSet resourceSet) {
        this.resourceSet = resourceSet;
    }

    public DefaultRuntimeEmfModel(Resource resource) throws EolModelLoadingException {
        super.determinePackagesFrom(resource.getResourceSet());
        modelImpl = resource;
    }


    public DefaultRuntimeEmfModel() {
        this.resourceSet = createResourceSet();
        this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(resourceSet.getResourceFactoryRegistry().DEFAULT_EXTENSION,
                        new DefaultRuntimeXmiResourceImpl.Factory());
    }

    protected ResourceSet createResourceSet() {
        CachedResourceSet ret =  new CachedResourceSet();
        ret.getLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        ret.setURIResourceMap(new HashMap<>());
        return ret;
    }


    public void loadModelFromUri() throws EolModelLoadingException {

        super.determinePackagesFrom(resourceSet);

        modelImpl = loadResourceToResourceSet(this, resourceSet, packages, modelUri, expand, readOnLoad);
    }

}
