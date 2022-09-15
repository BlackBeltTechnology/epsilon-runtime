package hu.blackbelt.epsilon.runtime.execution;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.model.test1.data.*;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
class ExecutionContextTest {

    URIHandler uriHandler;
    ResourceSet executionResourceSet;
    Log slf4jLogger;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Set our custom handler
        uriHandler = new NioFilesystemnRelativePathURIHandlerImpl("urn", FileSystems.getDefault(),
                new File(targetDir(), "test-classes").getAbsolutePath());

        // Setup resourcehandler used to load metamodels
        executionResourceSet = new CachedResourceSet();
        executionResourceSet.getURIConverter().getURIHandlers().add(0, uriHandler);

        // Default logger
        slf4jLogger = new Slf4jLog(log);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }


    @Test
    void testSimpleEolRunWithFileSystemURL() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(slf4jLogger)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        new File(targetDir(), "test-classes/epsilon-runtime-test.ecore").getAbsolutePath(),
                        new File(targetDir(), "test-classes/epsilon-runtime-test2.ecore").getAbsolutePath()))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST1")
                                .emf(new File(targetDir(), "test-classes/epsilon-runtime-test1.model").getAbsolutePath())
                                .build(),

                        emfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST2")
                                .emf(new File(targetDir(), "test-classes/epsilon-transformedfs.model").getAbsolutePath())
                                .readOnLoad(false)
                                .storeOnDisposal(true)
                                .build(),

                        excelModelContextBuilder()
                                .name("NAMEMAPPING")
                                .excel(new File(targetDir(), "test-classes/namemapping.xlsx").getAbsolutePath())
                                .excelConfiguration(new File(targetDir(), "test-classes/namemapping.xml").getAbsolutePath())
                                .build()))
                //.sourceDirectory(scriptDir())
                .build();

        // run the loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(new File(scriptDir(), "transformTest1ToTest2WithNameMapping.etl").toURI())
                        .build());

        executionContext.commit();
        executionContext.close();
    }

    @Test
    void testSimpleEolRunWithCustomURN() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(slf4jLogger)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test.ecore",
                        "urn:epsilon-runtime-test2.ecore"))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST1")
                                .emf("urn:epsilon-runtime-test1.model")
                                .build(),

                        emfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST2")
                                .emf("urn:epsilon-transformednio.model")
                                .readOnLoad(false)
                                .storeOnDisposal(true)
                                .build()))
                .build();

        // run the loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(new File(scriptDir(), "transformTest1ToTest2.etl").toURI())
                        .build());

        // The converted model resource
        Resource resource = ((EmfModel) executionContext
                .getProjectModelRepository()
                .getModelByName("TEST2")).getResource();

        for (Iterator<EObject> i = resource.getAllContents(); i.hasNext(); ) {
            slf4jLogger.info(i.next().toString());
        }

        executionContext.commit();
        executionContext.close();
    }

    @Test
    void testReflectiveCreated() throws EolModelNotFoundException, IOException, ScriptExecutionException {


        // Creatring resource from execution resource set
        // The lifecycle of resource is manged by us - creating the model and drop
        String createdSourceModelName = "urn:epsilon-runtime-test2.model";
        Resource createdSourceResource = executionResourceSet.createResource(
                URI.createURI(createdSourceModelName));

        // Adding packages from generated packages - the registration of source of the generated
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
                .log(slf4jLogger)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test2.ecore"))
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST1")
                                .resource(createdSourceResource)
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST2")
                                .resource(createdTargetResource)
                                .build()))
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(new File(scriptDir(), "transformTest1ToTest2.etl").toURI())
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
            slf4jLogger.info(i.next().toString());
        }
    }

    @Test
    void testSimpleXmlRunWithCustomURN() throws Exception {

        // Executrion context
        ExecutionContext executionContext = executionContextBuilder()
                .log(slf4jLogger)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of(
                        "urn:epsilon-runtime-test.ecore"))
                .modelContexts(ImmutableList.of(
                        emfModelContextBuilder()
                                .log(slf4jLogger)
                                .name("TEST1")
                                .emf("urn:epsilon-runtime-test1.model")
                                .build(),

                        xmlModelContextBuilder()
                                .log(slf4jLogger)
                                .name("LIQUIBASE")
                                // TODO: XSDEcoreBuilder creating separate ResourceSet, so URIHandlers are not
                                // working. Have to find a way to inject
                                // .xsd("urn:liquibase.xsd")
                                .xsd(new File(targetDir(), "test-classes/liquibase.xsd").getAbsolutePath())
                                .xml("urn:epsilon-transformedliquibase.xml")
                                .readOnLoad(false)
                                .storeOnDisposal(true)
                                .build()))
                .build();

        // run the loading
        executionContext.load();

        // Transformation script
        executionContext.executeProgram(
                etlExecutionContextBuilder()
                        .source(new File(scriptDir(), "transformTest1ToLiquibase.etl").toURI())
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
