package hu.blackbelt.epsilon.runtime.execution.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@AllArgsConstructor
public class NameMappedURIHandlerImpl extends URIHandlerImpl {

    @NonNull
    List<URIHandler> uriHandlerList;

    @NonNull
    Map<URI, URI> nameMapping;

    private URI getMappedURI(URI uri) {
        // return uri.hasFragment() && uri.hasOpaquePart() && this.baseURI.hasOpaquePart() && uri.opaquePart().equals(this.baseURI.opaquePart()) ? URI.createURI("#" + uri.fragment()) : super.deresolve(uri);

        if (uri.hasOpaquePart()) {
            for (URI u : nameMapping.keySet()) {
                if (u.hasOpaquePart() && u.scheme().equals(uri.scheme()) && u.opaquePart().equals(uri.opaquePart())) {
                    URI rep = nameMapping.get(u);
                    if (uri.hasFragment()) {
                        // Get resolved URI
                        return URI.createGenericURI(rep.scheme(), uri.opaquePart(), uri.fragment());
                    } else {
                        return rep;
                    }
                }
            }
            /*
            if (nameMapping.containsKey(uri.opaquePart())) {
                return URI.createURI(nameMapping.get(uri.opaquePart()) + (uri.hasFragment() ? "#" + uri.fragment() : ""));
            } */
        }
        return uri;
    }


    private URIHandler chooseUriHandler(URI uri) throws IOException {
        Optional<URIHandler> uriHandler = uriHandlerList.stream().filter(u -> u.canHandle(getMappedURI(uri))).findFirst();
        if (!uriHandler.isPresent()) {
            throw new IOException("URI cannot be resolved: " + uri.toString());
        } else {
            return uriHandler.get();
        }
    }

    @Override
    public boolean canHandle(URI uri) {
        return uriHandlerList.stream().filter(u -> u.canHandle(getMappedURI(uri))).findFirst().isPresent();
    }

    @Override
    public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
        return chooseUriHandler(getMappedURI(uri)).createOutputStream(getMappedURI(uri), options);
    }

    @Override
    public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
        return chooseUriHandler(getMappedURI(uri)).createInputStream(getMappedURI(uri), options);
    }

    @Override
    public void delete(URI uri, Map<?, ?> options) throws IOException {
        chooseUriHandler(getMappedURI(uri)).delete(uri, options);
    }

    @Override
    public boolean exists(URI uri, Map<?, ?> options) {
        try {
            return chooseUriHandler(getMappedURI(uri)).exists(getMappedURI(uri), options);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
        return chooseUriHandler(getMappedURI(uri)).getAttributes(getMappedURI(uri), options);
    }

    @Override
    public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
        chooseUriHandler(getMappedURI(uri)).setAttributes(getMappedURI(uri), attributes, options);
    }

}
