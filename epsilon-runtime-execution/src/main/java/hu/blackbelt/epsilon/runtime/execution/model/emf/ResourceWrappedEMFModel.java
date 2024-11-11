package hu.blackbelt.epsilon.runtime.execution.model.emf;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.EmfUtils;
import lombok.SneakyThrows;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfPropertyGetter;
import org.eclipse.epsilon.emc.emf.EmfPropertySetter;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;

class ResourceWrappedEMFModel extends EmfModel {

    Resource resource;
    Resource wrappedResource;
    Map<URI, URI> uriConverterMap;
    Logger log;
    ResourceSet wrappedResourceSet;

    public ResourceWrappedEMFModel(Logger log, ResourceSet wrappedResourceSet, Resource resource, Boolean parallel, Boolean expandReference, Boolean validateModel, Map<URI, URI> uriConverterMap) {
        super.setResource(resource);
        this.log = log;
        this.wrappedResourceSet = wrappedResourceSet;
        this.uriConverterMap = uriConverterMap;
        this.resource = resource;
        propertyGetter = new EmfPropertyGetter();
        propertySetter = new EmfPropertySetter();
        this.wrappedResource = resource;
        this.setReadOnLoad(false);
        this.setStoredOnDisposal(false);
        this.setParallelAllOf(parallel);
        this.setConcurrent(parallel);
        this.setExpand(false);
        this.setValidate(false);

        this.setExpand(expandReference);
        this.setValidate(validateModel);
    }

    @Override
    protected void loadModel() throws EolModelLoadingException {
        synchronized (resource) {
            super.loadModel();
            modelImpl = wrappedResource;
        }
    }

    @Override
    public boolean store() {
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    @SneakyThrows
    protected ResourceSet createResourceSet() {
        ResourceSet resourceSet =  super.createResourceSet();
        EmfUtils.setupResourceSet(log, wrappedResourceSet, resourceSet, uriConverterMap);
        return resourceSet;
    }

    /*
    @Override
    protected ResourceSet createResourceSet() {
        throw new UnsupportedOperationException("Could not create resourceset in wrapped emf model");
    }

    @Override
    public void loadModelFromUri() throws EolModelLoadingException {
        throw new UnsupportedOperationException("Could not load model from uri in wrapped emf model");
    }
     */

}
