package hu.blackbelt.epsilon.runtime.execution.model.emf;

import hu.blackbelt.epsilon.runtime.execution.EmfUtils;
import lombok.SneakyThrows;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.emf.EmfPropertyGetter;
import org.eclipse.epsilon.emc.emf.EmfPropertySetter;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class ClonedEMFModel extends EmfModel {
    Logger log;
    Resource resource;
    Resource wrappedResource;
    ResourceSet wrappedResourceSet;
    Boolean copyBack;
    Map<URI, URI> uriMap;
    ResourceSet resourceSet;

    public ClonedEMFModel(ResourceSet resourceSet, ResourceSet wrappedResourceSet, Resource wrappedResource, Boolean parallel, Boolean expandReference, Boolean validateModel, Boolean copyBack, Logger log, Map<URI, URI> uriMap) {
//        this.wrappedEmfModelContext = wrappedEmfModelContext;
        this.uriMap = uriMap;
        this.wrappedResource = wrappedResource;
        this.wrappedResourceSet = wrappedResourceSet;
        this.resourceSet = resourceSet;
        this.resource = resourceSet.createResource(wrappedResource.getURI());
        this.log = log;
        this.copyBack = copyBack;
        super.setResource(this.resource);

//        if (uriMap != null) {
//            EmfUtils.setupResourceSet(log, wrappedResourceSet, resourceSet, uriMap);
//        }

        //super.propertySetter = new EmfPropertySetter();
        propertyGetter = new EmfPropertyGetter();
        propertySetter = new EmfPropertySetter();

        this.wrappedResourceSet = resourceSet;
        this.setReadOnLoad(false);
        this.setStoredOnDisposal(false);

//        this.setParallelAllOf(parallel);
//        this.setConcurrent(parallel);
//        this.setExpand(expandReference);
//        this.setValidate(validateModel);

        try {
            copyResource(this.wrappedResource, this.resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void loadModel() throws EolModelLoadingException {
        synchronized (resource) {
            super.loadModel();
//                modelImpl = wrappedResource;
        }
    }

    @SneakyThrows
    protected ResourceSet createResourceSet() {
        //ResourceSet resourceSet =  super.createResourceSet();
        //EmfUtils.setupResourceSet(log, wrappedResourceSet, resourceSet, uriMap);
        //return resourceSet;
        return resourceSet;
    }

//    @Override
//    public void loadModelFromUri() throws EolModelLoadingException {
//        throw new UnsupportedOperationException("Could not load model from uri in wrapped emf model");
//    }

    @Override
    public boolean store() {
        return false;
    }

    @Override
    public void dispose() {
        if (copyBack) {
            try {
                copyResource(this.resource, this.wrappedResource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.dispose();
    }

    public void copyResource(Resource from, Resource to) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        from.save(baos, defaultSaveOptions());
        baos.flush();
        baos.close();
        log.info(new String(baos.toByteArray(), "UTF-8"));
        to.load(new ByteArrayInputStream(baos.toByteArray()), defaultLoadOptions());
    }

    public Map<Object, Object> defaultSaveOptions() {
        Map<Object, Object> saveOptions = new HashMap<>();
        saveOptions.put(XMLResource.OPTION_DECLARE_XML, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        saveOptions.put(XMLResource.OPTION_URI_HANDLER, new URIHandlerImpl() {
            public URI deresolve(URI uri) {
                return uri.hasFragment()
                        && uri.hasOpaquePart()
                        && this.baseURI.hasOpaquePart()
                        && uri.opaquePart().equals(this.baseURI.opaquePart())
                        ? URI.createURI("#" + uri.fragment())
                        : super.deresolve(uri);
            }
        });
        saveOptions.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        saveOptions.put(XMLResource.OPTION_SKIP_ESCAPE_URI, Boolean.FALSE);
        saveOptions.put(XMLResource.OPTION_ENCODING, "UTF-8");
        return saveOptions;
    }

    public Map<Object, Object> defaultLoadOptions() {
        Map<Object, Object> loadOptions = new HashMap<>();
        //loadOptions.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);
        //loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_LAX_FEATURE_PROCESSING, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
        return loadOptions;
    }

}
