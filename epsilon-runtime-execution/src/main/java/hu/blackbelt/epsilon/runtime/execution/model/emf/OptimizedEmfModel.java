package hu.blackbelt.epsilon.runtime.execution.model.emf;


import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

import java.util.HashMap;

import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.loadResourceToResourceSet;

public class OptimizedEmfModel extends EmfModel {

    protected ResourceSet createResourceSet() {
        CachedResourceSet ret =  new CachedResourceSet();
        ret.getLoadOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        ret.setURIResourceMap(new HashMap<>());
        return ret;
    }


    public void loadModelFromUri() throws EolModelLoadingException {
        ResourceSet resourceSet = createResourceSet();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model", new OptimizedXmiResourceImpl.Factory());

        super.determinePackagesFrom(resourceSet);

        modelImpl = loadResourceToResourceSet(this, resourceSet, packages, modelUri, expand, readOnLoad);
    }

}
