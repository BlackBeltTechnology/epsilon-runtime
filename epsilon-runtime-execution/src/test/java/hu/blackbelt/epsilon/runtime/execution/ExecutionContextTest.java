package hu.blackbelt.epsilon.runtime.execution;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.model.test1.data.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext.emfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

class ExecutionContextTest {

    URIHandler uriHandler;
    ResourceSet executionResourceSet;
    ArtifactResolver passthroughArtifactResolver;
    Log log;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl(FileSystems.getDefault(),
                new File(targetDir(), "test-classes").getAbsolutePath());

        // Setup resourcehandler used to load metamodels
        executionResourceSet = new CachedResourceSet();
        executionResourceSet.getURIConverter().getURIHandlers().add(0, uriHandler);

        // Artifact resolver - @Deprecated
        passthroughArtifactResolver = new ArtifactResolver() {
            @Override
            public URI getArtifactAsEclipseURI(String url) {
                return URI.createURI(url);
            }
        };

        // Default logger
        log = new Slf4jLog();

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void testSimpleEolRun() throws Exception {

        // Executrion context
        ExecutionContext executionContext = ExecutionContext.builder()
                .artifactResolver(passthroughArtifactResolver)
                .log(log)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test.ecore",
                        "urn:epsilon-runtime-test2.ecore"))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(log)
                                .name("TEST1")
                                .model("urn:epsilon-runtime-test1.model")
                                .build(),

                        emfModelContextBuilder()
                                .log(log)
                                .name("TEST2")
                                .model("urn:epsilon-transformed.model")
                                .readOnLoad(false)
                                .storeOnDisposal(true)
                                .build()))
                .sourceDirectory(scriptDir())
                .build();

        // run the loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("transformTest1ToTest2.etl")
                        .build());

        // The converted model resource
        Resource resource = ((EmfModel) executionContext
                .getProjectModelRepository()
                .getModelByName("TEST2")).getResource();

        for (Iterator<EObject> i = resource.getAllContents(); i.hasNext(); ) {
            log.info(i.next().toString());
        }

        executionContext.commit();
        executionContext.close();


    }

    @Test
    void testReflectiveCreated() throws EolModelNotFoundException, IOException {


        // Creatring resource from execution resource set
        // The lifecycle of resource is manged by us - creating the model and drop
        String createdSourceModelName = "urn:epsilon-runtime-test2.model";
        Resource createdSourceResource = executionResourceSet.createResource(
                URI.createURI(createdSourceModelName));

        // Adding ppackage from generated packages - the registration of source of the generated
        // ecore id not required
        executionResourceSet.getPackageRegistry().put(DataPackage.eNS_URI, DataPackage.eINSTANCE);

        // Creating our sample model
        DataModel dataModel = DataFactory.eINSTANCE.createDataModel();
        dataModel.setName("TEST1");
        createdSourceResource.getContents().add(dataModel);

        Entity entity1 = DataFactory.eINSTANCE.createEntity();
        entity1.setName("TestEntity1");

        Entity entity2 = DataFactory.eINSTANCE.createEntity();
        entity2.setName("TestEntity2");

        dataModel.getEntity().add(entity1);
        dataModel.getEntity().add(entity2);

        Attribute entity1Attr1 = DataFactory.eINSTANCE.createAttribute();
        entity1Attr1.setName("attr1");
        entity1Attr1.setName("String");
        entity1.getAttribute().add(entity1Attr1);

        Attribute entity2Attr2 = DataFactory.eINSTANCE.createAttribute();
        entity2Attr2.setName("attr2");
        entity2Attr2.setName("String");
        entity2.getAttribute().add(entity2Attr2);

        EntityReference entity2Reference = DataFactory.eINSTANCE.createEntityReference();
        entity2Reference.setTarget(entity1);
        entity2.getReference().add(entity2Reference);


        // Creating target resource from execution resource set.
        // The lifecycle of resource is manged by us
        Resource createdTargetResource = executionResourceSet.createResource(URI.createURI(
                "urn:epsilon-transformed-reflective.model"));

        // Executrion context
        ExecutionContext executionContext = ExecutionContext.builder()
                .artifactResolver(passthroughArtifactResolver)
                .log(log)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test2.ecore"))
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("TEST1")
                                .resource(createdSourceResource)
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("TEST2")
                                .resource(createdTargetResource)
                                .build()))
                .sourceDirectory(scriptDir())
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("transformTest1ToTest2.etl")
                        .build());


        // Saving the transformed model
        Map options = new HashMap<>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        createdTargetResource.save(options);

        // The converted model resource
        Resource resource = ((EmfModel) executionContext
                .getProjectModelRepository()
                .getModelByName("TEST1")).getResource();

        for (Iterator<EObject> i = resource.getAllContents(); i.hasNext(); ) {
            log.info(i.next().toString());
        }
    }


    public File targetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../target");
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

    public File scriptDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath+"../../src/test/epsilon");
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }

}