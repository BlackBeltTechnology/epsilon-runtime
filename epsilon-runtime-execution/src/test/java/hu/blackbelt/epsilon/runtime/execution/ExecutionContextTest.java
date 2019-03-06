package hu.blackbelt.epsilon.runtime.execution;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import hu.blackbelt.epsilon.runtime.execution.model.emf.DefaultRuntimeEmfModelFactory;
import hu.blackbelt.epsilon.runtime.execution.model.emf.DefaultRuntimeXmiResourceImpl;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelFactory;
import hu.blackbelt.epsilon.runtime.execution.model.xml.DefaultRuntimeXmlResourceImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ExecutionContextTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void testSimpleModelConversion() {

        List<ModelContext> modelContexts = Lists.newArrayList();

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(resourceSet.getResourceFactoryRegistry().DEFAULT_EXTENSION,
                        new DefaultRuntimeXmlResourceImpl.Factory());


        /*
        XMIResourceFactoryImpl resourceFactory = new XMIResourceFactoryImpl() {
            @Override
            public Resource createResource(URI uri) {
                return super.createResource(uri);
            }
        };

        Resource.Factory.Registry resourceFactoryRegistry = new Resource.Factory.Registry() {
            @Override
            public Resource.Factory getFactory(URI uri) {
                return null;
            }

            @Override
            public Resource.Factory getFactory(URI uri, String contentType) {
                if (uri.scheme().equals("model")) {
                    return resourceFactory;
                }
                return null;
            }

            @Override
            public Map<String, Object> getProtocolToFactoryMap() {
                return null;
            }

            @Override
            public Map<String, Object> getExtensionToFactoryMap() {
                return null;
            }

            @Override
            public Map<String, Object> getContentTypeToFactoryMap() {
                return null;
            }
        };
        resourceSet.setResourceFactoryRegistry(resourceFactoryRegistry);

        */


        if (resourceSet.getPackageRegistry().getEPackage(EcorePackage.eNS_URI) == null) {
            resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        }

        // Add virtual filesystem
        resourceSet.getURIConverter().getURIHandlers().add(0, new NioFilesystemnURIHandlerImpl( Jimfs.newFileSystem(Configuration.unix())));


        //new ResourceFactoryRegistryImpl();

        EmfModelFactory emfModelFactory = new DefaultRuntimeEmfModelFactory();

        /*
        {
            @Override
            public EmfModel create(final ResourceSet resourceSet) {
                return new EmfModel() {
                    public void loadModelFromUri() throws EolModelLoadingException {
                        //ResourceSet resourceSet = createResourceSet();                        /*
                        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                                .put(resourceSet.getResourceFactoryRegistry().DEFAULT_EXTENSION, new DefaultRuntimeXmiResourceImpl.Factory());

                        super.determinePackagesFrom(resourceSet);

                        modelImpl = loadResourceToResourceSet(this, resourceSet, packages, modelUri, expand, readOnLoad);
                    }
                };
            }
        }; */


        ExecutionContext executionContext = ExecutionContext.builder()
                .artifactResolver(new ArtifactResolver() {
                    @Override
                    public URI getArtifactAsEclipseURI(String url) {
                        return URI.createGenericURI("niofs", url, null);
                    }
                })
                .resourceSet(resourceSet)
                .log(new Slf4jLog())
                .metaModels(ImmutableList.of("epsilon-runtime-test.ecore"))
                .build();

        EmfModelContext emfModelContext = EmfModelContext.builder()
                .emfModelFactory(emfModelFactory)
                .name("TEST")
                .model("epsilon-runtime-test1.model")
                .build();

        executionContext.addModel(emfModelContext);

    }



    public static ResourceSet initResourceSet() {
        ResourceSet rs = new ResourceSetImpl();
        rs.setResourceFactoryRegistry(Resource.Factory.Registry.INSTANCE);
        // rs.setPackageRegistry(EPackage.Registry.INSTANCE);
        /*rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("library", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xml", new XMLResourceFactoryImpl()); */
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(rs.getResourceFactoryRegistry().DEFAULT_EXTENSION, new DefaultRuntimeXmiResourceImpl.Factory());
        //rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put(rs.getResourceFactoryRegistry().DEFAULT_EXTENSION, new DefaultXMIResource.Factory());


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

/*
    class CustomXmiResourceImpl extends XMIResourceImpl {
        public CustomXmiResourceImpl() {
            super();
            super.defaultLoadOptions.put(XMLResource.OPTION_URI_HANDLER, new NioFilesystemnURIHandlerImpl());
        }
    } */

}