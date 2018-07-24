package hu.blackbelt.epsilon.runtime.execution;

import org.eclipse.emf.common.util.URI;

public interface ArtifactResolver {
    URI getArtifactAsEclipseURI(String url);
}
