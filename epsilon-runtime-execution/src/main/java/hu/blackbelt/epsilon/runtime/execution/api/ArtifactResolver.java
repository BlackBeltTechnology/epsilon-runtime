package hu.blackbelt.epsilon.runtime.execution.api;

import org.eclipse.emf.common.util.URI;

public interface ArtifactResolver {
    URI getArtifactAsEclipseURI(String url);
}
