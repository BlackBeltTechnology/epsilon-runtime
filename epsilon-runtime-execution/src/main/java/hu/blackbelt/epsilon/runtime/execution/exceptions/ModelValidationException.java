package hu.blackbelt.epsilon.runtime.execution.exceptions;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

public class ModelValidationException extends Exception {

    List<String> validationErrors;
    Resource resource;

    public ModelValidationException(List<String> validationErrors, Resource resource) {
        super("Invalid model\n" +
                validationErrors.stream()
                        .collect(Collectors.joining("\n")) + "\n" + resourceToString(resource));
        this.resource = resource;
        this.validationErrors = validationErrors;
    }

    private static String resourceToString(Resource resource) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            resource.save(byteArrayOutputStream, ImmutableMap.of());
        } catch (IOException e) {
        }
        return new String(byteArrayOutputStream.toByteArray(), Charset.defaultCharset());
    }

}
