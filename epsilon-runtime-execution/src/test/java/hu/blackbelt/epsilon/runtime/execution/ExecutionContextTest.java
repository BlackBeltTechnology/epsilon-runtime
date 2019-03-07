package hu.blackbelt.epsilon.runtime.execution;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.epsilon.emc.emf.CachedResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelNotFoundException;
import org.junit.jupiter.api.Test;

import java.nio.file.FileSystems;

class ExecutionContextTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void testSimpleModelConversion() throws EolModelNotFoundException {
        URIHandler uriHandler = new NioFilesystemnURIHandlerImpl(FileSystems.getDefault(), "/Project/epsilon-runtime/epsilon-runtime-execution/src/test/resources");

        ResourceSet executionResourceSet = new CachedResourceSet();
        executionResourceSet.getURIConverter().getURIHandlers().add(0, uriHandler);

        ArtifactResolver passthroughArtifactResolver = new ArtifactResolver() {
            @Override
            public URI getArtifactAsEclipseURI(String url) {
                return URI.createURI(url);
            }
        };

        Slf4jLog log = new Slf4jLog();

        EmfModelContext emfModelContext = EmfModelContext.builder()
                .log(log)
                .name("TEST")
                .model("urn:epsilon-runtime-test1.model")
                .build();


        ExecutionContext executionContext = ExecutionContext.builder()
                .artifactResolver(passthroughArtifactResolver)
                .log(log)
                .resourceSet(executionResourceSet)
                .metaModels(ImmutableList.of("urn:epsilon-runtime-test.ecore"))
                .modelContexts(ImmutableList.of(emfModelContext))
                .build();

        executionContext.init();

        for (EObject o : ((EmfModel) executionContext.getProjectModelRepository().getModelByName("TEST")).getResource().getContents()) {
            log.info(o.toString());
        }

    }

}