package hu.blackbelt.epsilon.runtime.execution.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

/**
 * A URI handler that will avoid creating relative references between platform:/resource and platform:/plugin.
 */
public class IgnoreOwnBaseUriURIHandler extends URIHandlerImpl {
    @Override
    public URI deresolve(URI uri)
    {
        if (uri.hasFragment() && uri.hasOpaquePart() && baseURI.hasOpaquePart()) {
            if (uri.opaquePart().equals(baseURI.opaquePart())) {
                return URI.createURI(uri.fragment());
            }
        }
        return super.deresolve(uri);
    }
}
