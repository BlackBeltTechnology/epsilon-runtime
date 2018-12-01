package hu.blackbelt.epsilon.runtime.execution;

import hu.blackbelt.epsilon.runtime.execution.model.emf.OptimizedEmfModel;
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
import org.eclipse.epsilon.emc.emf.DefaultXMIResource;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IReflectiveModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ReflectiveModelReference;
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


    public static ModelReference createModelReference(IModel model) {
        if (model instanceof IReflectiveModel) {
            return new ReflectiveModelReference((IReflectiveModel)model);

        } else {
            return new ModelReference(model);
        }
    }

    public static URI convertFileToUri(File file) {
        return file == null ? null : URI.createFileURI(file.getAbsolutePath());
    }

    /*
    public static List<EPackage> registerMetamodel(ResourceSet resourceSet, String fileName) throws Exception {
        // List<EPackage> ePackages = EmfUtil.register(URI.createPlatformResourceURI(fileName, true), EPackage.Registry.INSTANCE);
        EmfUtil.register(URI.createFileURI(fileName), EPackage.Registry.INSTANCE);
        List<EPackage> ePackages = EmfUtil.register(URI.createFileURI(fileName), resourceSet.getPackageRegistry());
        return ePackages;
    } */

    public static ResourceSet initResourceSet() {
        ResourceSet rs = new ResourceSetImpl(); // new EmfModelResourceSet();
        rs.setResourceFactoryRegistry(Resource.Factory.Registry.INSTANCE);
        // rs.setPackageRegistry(EPackage.Registry.INSTANCE);
        /*rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("library", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl()); */
        // rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(rs.getResourceFactoryRegistry().DEFAULT_EXTENSION, new OptimizedXmiResourceImpl.Factory());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(rs.getResourceFactoryRegistry().DEFAULT_EXTENSION, new DefaultXMIResource.Factory());


        if (rs.getPackageRegistry().getEPackage(EcorePackage.eNS_URI) == null) {
            rs.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        }


        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        if (rs.getPackageRegistry().getEPackage(UMLPackage.eNS_URI) == null) {
            rs.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        }

        UMLResourcesUtil.init(rs);
        UMLResourcesUtil.initLocalRegistries(rs);
        ((ResourceSetImpl) rs).setURIResourceMap(new HashMap<>());

        return rs;
    }
    
    public static EmfModel createEmfModel(ResourceSet resourceSet) {
        return new OptimizedEmfModel();
        //return new EmfModel();
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

                EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
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
