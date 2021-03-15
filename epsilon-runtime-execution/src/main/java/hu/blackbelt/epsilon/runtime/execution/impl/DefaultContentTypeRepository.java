package hu.blackbelt.epsilon.runtime.execution.impl;

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
