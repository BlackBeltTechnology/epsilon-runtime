package hu.blackbelt.epsilon.runtime.osgi;

/*-
 * #%L
 * epsilon-runtime-osgi
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

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.osgi.framework.Bundle;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BundleURIHandler extends URIHandlerImpl {
    @Builder.Default
    String urlSchema = "";

    @Builder.Default
    String rootPath = "";

    @NonNull
    Bundle bundle;

    public BundleURIHandler(String urlSchema, String rootPath, Bundle bundle) {
        super();
        this.bundle = bundle;
        this.urlSchema = urlSchema;
        this.rootPath = rootPath;
    }

    @Override
    public boolean canHandle(URI uri) {
        if (urlSchema == null || urlSchema.equals("")) {
            return true;
        } else if (uri == null) {
            return false;
        } else if (uri.scheme() == null || uri.scheme().equals("")) {
            return false;
        } else {
            return uri.scheme().equals(urlSchema);
        }
    }

    /**
     * Creates an output stream for the file path and returns it.
     * <p>
     * This implementation allocates a {@link OutputStream} and creates subdirectories as necessary.
     * </p>
     * @return an open output stream.
     * @exception IOException if there is a problem obtaining an open output stream.
     */
    @Override
    public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
        throw new IOException("Bundle URIHandler is readonly");
    }

    /**
     * Creates an input stream for the file path and returns it.
     * <p>
     * This implementation allocates a {@link FileInputStream}.
     * </p>
     * @return an open input stream.
     * @exception IOException if there is a problem obtaining an open input stream.
     */
    @Override
    public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
        InputStream inputStream = bundle.getEntry(getFullPath(uri)).openStream();

        Map<Object, Object> response = getResponse(options);
        if (response != null) {
            response.put(URIConverter.RESPONSE_TIME_STAMP_PROPERTY, bundle.getLastModified());
        }
        return inputStream;
    }

    @Override
    public void delete(URI uri, Map<?, ?> options) throws IOException {
        throw new IOException("Bundle URIHandler is readonly");
    }

    @Override
    public boolean exists(URI uri, Map<?, ?> options) {
        try {
            String parent = FilenameUtils.getFullPathNoEndSeparator(getFullPath(uri));
            String fileName = FilenameUtils.getName(getFullPath(uri));
            return bundle.findEntries(parent, fileName, false).hasMoreElements();
        } catch (NullPointerException | InvalidPathException e) {
            return false;
        }
    }

    @Override
    public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (exists(uri, options)) {
            Set<String> requestedAttributes = getRequestedAttributes(options);
            if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_TIME_STAMP)) {
                result.put(URIConverter.ATTRIBUTE_TIME_STAMP, bundle.getLastModified());
            }
            if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_LENGTH)) {
                result.put(URIConverter.ATTRIBUTE_LENGTH, -1);
            }
            if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_READ_ONLY)) {
                result.put(URIConverter.ATTRIBUTE_READ_ONLY, true);
            }
            if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_HIDDEN)) {
                result.put(URIConverter.ATTRIBUTE_HIDDEN, false);
            }
            if (requestedAttributes == null || requestedAttributes.contains(URIConverter.ATTRIBUTE_DIRECTORY)) {
                result.put(URIConverter.ATTRIBUTE_DIRECTORY, false);
            }
        }
        return result;
    }

    @Override
    public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
        throw new IOException("Bundle URIHandler is readonly");
    }

    private String getFullPath(URI uri) {
        String uriPath = "";
        if (uri.isFile()) {
            uriPath = uri.path();
        } else if (uri.hasOpaquePart()) {
            uriPath = uri.opaquePart();
        }

        if (rootPath == null || rootPath.equals("")) {
            return uriPath;
        } else {
            return rootPath + File.separator + uriPath;
        }
    }
}
