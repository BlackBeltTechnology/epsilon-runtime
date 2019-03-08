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

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext.emfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.xml.XmlModelContext.xmlModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.model.test1.data.util.builder.DataBuilders.*;

class ExecutionContextTest {

    URIHandler uriHandler;
    ResourceSet executionResourceSet;
    ArtifactResolver passthroughArtifactResolver;
    Log log;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
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
    void testSimpleEolRunWithFileSystemURL() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .artifactResolver(passthroughArtifactResolver)
                .log(log)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        new File(targetDir(), "test-classes/epsilon-runtime-test.ecore").getAbsolutePath(),
                        new File(targetDir(), "test-classes/epsilon-runtime-test2.ecore").getAbsolutePath()))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(log)
                                .name("TEST1")
                                .model(new File(targetDir(), "test-classes/epsilon-runtime-test1.model").getAbsolutePath())
                                .build(),

                        emfModelContextBuilder()
                                .log(log)
                                .name("TEST2")
                                .model(new File(targetDir(), "test-classes/epsilon-transformedfs.model").getAbsolutePath())
                                .readOnLoad(false)
                                .storeOnDisposal(true)
                                .build(),

                        excelModelContextBuilder()
                                .name("NAMEMAPPING")
                                .excelSheet(new File(targetDir(), "test-classes/namemapping.xlsx").getAbsolutePath())
                                .excelConfiguration(new File(targetDir(), "test-classes/namemapping.xml").getAbsolutePath())
                                .build()))
                .sourceDirectory(scriptDir())
                .build();

        // run the loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source("transformTest1ToTest2WithNameMapping.etl")
                        .build());

        executionContext.commit();
        executionContext.close();
    }

    @Test
    void testSimpleEolRunWithCustomURN() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
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
                                .model("urn:epsilon-transformednio.model")
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
        // To generate it from genmodel: https://github.com/BlackBeltTechnology/emfbuildergenerator
        Entity entity1 = newEntityBuilder()
                .withName("TestEntity1")
                .withAttribute(newAttributeBuilder()
                        .withName("attr1")
                        .withType("String"))
                .build();

        Entity entity2 = newEntityBuilder()
                .withName("TestEntity2")
                .withAttribute(newAttributeBuilder()
                        .withName("attr2")
                        .withType("String"))
                .withReference(newEntityReferenceBuilder()
                        .withName("entity1ref")
                        .withTarget(entity1))
                .build();


        DataModel dataModel = newDataModelBuilder().withEntity(entity1).withEntity(entity2).build();
        createdSourceResource.getContents().add(dataModel);


        // Creating target resource from execution resource set.
        // The lifecycle of resource is manged by us
        Resource createdTargetResource = executionResourceSet.createResource(URI.createURI(
                "urn:epsilon-transformed-reflective.model"));

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
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

    @Test
    void testSimpleXmlRunWithCustomURN() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .artifactResolver(passthroughArtifactResolver)
                .log(log)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test.ecore"))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(log)
                                .name("TEST1")
                                .model("urn:epsilon-runtime-test1.model")
                                .build(),

                        xmlModelContextBuilder()
                                .log(log)
                                .name("LIQUIBASE")
                                // TODO: XSDEcoreBuilder creating separate ResourceSet, so URIHandlers are not
                                // working. Have to find a way to inject
                                // .xsd("urn:liquibase.xsd")
                                .xsd(new File(targetDir(), "test-classes/liquibase.xsd").getAbsolutePath())
                                .xml("urn:epsilon-transformedliquibase.xml")
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
                        .source("transformTest1ToLiquibase.etl")
                        .build());

        executionContext.commit();
        executionContext.close();
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