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

import lombok.SneakyThrows;
import org.eclipse.epsilon.egl.config.ContentTypeRepository;
import org.eclipse.epsilon.egl.merge.partition.CompositePartitioner;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class DefaultContentTypeRepository implements ContentTypeRepository {

    private final DefaultXMLConfigReader reader = new DefaultXMLConfigReader();

    private Map<String, CompositePartitioner> partitioners = Collections.emptyMap();


    public DefaultContentTypeRepository(InputStream config) {
        load(config);
    }

    @SneakyThrows
    @Override
    public void load(InputStream stream) {
        partitioners = reader.read(stream);
    }

    @Override
    public CompositePartitioner partitionerFor(String contentType) {
        return partitioners.get(contentType);
    }
}
