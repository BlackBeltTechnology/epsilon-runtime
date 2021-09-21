package hu.blackbelt.epsilon.runtime.execution;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModelResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class EmfUtils {


    public static void addUmlPackagesToResourceSet(ResourceSet resourceSet) {

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        if (resourceSet.getPackageRegistry().getEPackage(UMLPackage.eNS_URI) == null) {
            resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        }

        UMLResourcesUtil.init(resourceSet);
        UMLResourcesUtil.initLocalRegistries(resourceSet);
        ((ResourceSetImpl) resourceSet).setURIResourceMap(new HashMap<>());

    }

    public static void addEmfPackagesToResourceSet(ResourceSet resourceSet) {
        // resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        // EmfUtils.register(resourceSet, uri, true);
    }

    public static ResourceSet initDefaultCachedResourceSet() {
        ResourceSet rs = new ResourceSetImpl(); // new CachedResourceSet(); // new ResourceSetImpl(); // new EmfModelResourceSet();

        rs.setResourceFactoryRegistry(Resource.Factory.Registry.INSTANCE);
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(rs.getResourceFactoryRegistry().DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        return rs;
    }
    
    /**
     * Register all the packages in the metamodel specified by the uri in the registry.
     *
     * @param resourceSet The resourceSet metamodel registered for
     * @param uri The URI of the metamodel
     * @param useUriForResource If True, the URI of the resource created for the metamodel would be overwritten
     * 	with the URI of the [last] EPackage in the metamodel.
     * @return A list of the EPackages registered.
     * @throws Exception If there is an error accessing the resources.
     */
    public static List<EPackage> register(ResourceSet resourceSet, URI uri, boolean useUriForResource) throws Exception {

        List<EPackage> ePackages = new ArrayList<EPackage>();

        Resource metamodel = resourceSet.createResource(uri);
        metamodel.load(Collections.EMPTY_MAP);

        setDataTypesInstanceClasses(metamodel);

        Iterator<EObject> it = metamodel.getAllContents();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof EPackage) {
                EPackage p = (EPackage) next;

                if (p.getNsURI() == null || p.getNsURI().trim().length() == 0) {
                    if (p.getESuperPackage() == null) {
                        p.setNsURI(p.getName());
                    }
                    else {
                        p.setNsURI(p.getESuperPackage().getNsURI() + "/" + p.getName());
                    }
                }

                if (p.getNsPrefix() == null || p.getNsPrefix().trim().length() == 0) {
                    if (p.getESuperPackage() != null) {
                        if (p.getESuperPackage().getNsPrefix()!=null) {
                            p.setNsPrefix(p.getESuperPackage().getNsPrefix() + "." + p.getName());
                        }
                        else {
                            p.setNsPrefix(p.getName());
                        }
                    }
                }

                if (p.getNsPrefix() == null) {
                    p.setNsPrefix(p.getName());
                }

                // EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
                resourceSet.getPackageRegistry().put(p.getNsURI(), p);

                if (useUriForResource) {
                    metamodel.setURI(URI.createURI(p.getNsURI()));
                }
                ePackages.add(p);
            }
        }
        return ePackages;
    }


    protected static void setDataTypesInstanceClasses(Resource metamodel) {
        Iterator<EObject> it = metamodel.getAllContents();
        while (it.hasNext()) {
            EObject eObject = (EObject) it.next();
            if (eObject instanceof EEnum) {
                // ((EEnum) eObject).setInstanceClassName("java.lang.Integer");
            } else if (eObject instanceof EDataType) {
                EDataType eDataType = (EDataType) eObject;
                String instanceClass = "";
                if (eDataType.getName().equals("String")) {
                    instanceClass = "java.lang.String";
                } else if (eDataType.getName().equals("Boolean")) {
                    instanceClass = "java.lang.Boolean";
                } else if (eDataType.getName().equals("Integer")) {
                    instanceClass = "java.lang.Integer";
                } else if (eDataType.getName().equals("Float")) {
                    instanceClass = "java.lang.Float";
                } else if (eDataType.getName().equals("Double")) {
                    instanceClass = "java.lang.Double";
                }
                if (instanceClass.trim().length() > 0) {
                    eDataType.setInstanceClassName(instanceClass);
                }
            }
        }
    }

    /*
    public static Resource registerPackages(ResourceSet resourceSet, List<EPackage> packages) {
        for (EPackage ep : packages) {
            String nsUri = ep.getNsURI();
            if (nsUri == null || nsUri.trim().length() == 0) {
                nsUri = ep.getName();
            }
            resourceSet.getPackageRegistry().put(nsUri, ep);
        }
    } */


    public static Resource loadResourceToResourceSet(IModel model, ResourceSet resourceSet, List<EPackage> packages, URI modelUri, boolean expand, boolean readOnLoad) throws EolModelLoadingException {
        // Note that AbstractEmfModel#getPackageRegistry() is not usable yet, as modelImpl is not set
        for (EPackage ep : packages) {
            String nsUri = ep.getNsURI();
            if (nsUri == null || nsUri.trim().length() == 0) {
                nsUri = ep.getName();
            }
            resourceSet.getPackageRegistry().put(nsUri, ep);
        }
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        Resource resource = resourceSet.createResource(modelUri);
        if (resource instanceof XMLResource) {
            ((XMLResource) resource).getDefaultSaveOptions().put(XMLResource.OPTION_ENCODING, "UTF-8");
        }

        if (readOnLoad) {
            try {
                resource.load(null);
                if (expand) {
                    EcoreUtil.resolveAll(resource);
                }
            } catch (IOException e) {
                throw new EolModelLoadingException(e, model);
            }
        }
        return resource;

    }

}
