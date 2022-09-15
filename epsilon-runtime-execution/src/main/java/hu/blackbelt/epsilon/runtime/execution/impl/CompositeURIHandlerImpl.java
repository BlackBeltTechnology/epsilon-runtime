package hu.blackbelt.epsilon.runtime.execution.impl;

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

import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Builder
@ToString
public class CompositeURIHandlerImpl  extends URIHandlerImpl  {

    @NonNull
    List<URIHandler> uriHandlerList;

    private URIHandler chooseUriHandler(URI uri) throws IOException {
        Optional<URIHandler> uriHandler = uriHandlerList.stream().filter(u -> u.canHandle(uri)).findFirst();
        if (!uriHandler.isPresent()) {
            throw new IOException("URI cannot be resolved: " + uri.toString());
        } else {
            return uriHandler.get();
        }
    }

    @Override
    public boolean canHandle(URI uri) {
        return uriHandlerList.stream().filter(u -> u.canHandle(uri)).findFirst().isPresent();
    }

    @Override
    public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
        return chooseUriHandler(uri).createOutputStream(uri, options);
    }

    @Override
    public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
        return chooseUriHandler(uri).createInputStream(uri, options);
    }

    @Override
    public void delete(URI uri, Map<?, ?> options) throws IOException {
        chooseUriHandler(uri).delete(uri, options);
    }

    @Override
    public boolean exists(URI uri, Map<?, ?> options) {
        try {
            return chooseUriHandler(uri).exists(uri, options);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
        return chooseUriHandler(uri).getAttributes(uri, options);
    }

    @Override
    public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
        chooseUriHandler(uri).setAttributes(uri, attributes, options);
    }

}
