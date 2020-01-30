package hu.blackbelt.epsilon.runtime.execution.model.xml;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.xml.XmlModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.Optional;

import static java.util.stream.Collectors.joining;

public interface XmlModelFactory {

    XmlModel create(ResourceSet resourceSet);

    static XmlModel loadXml(Log log, XmlModelFactory xmlModelFactory,
                            ResourceSet resourceSet, ModelRepository repository, XmlModelContext xmlModelContext,
                            URI uri, URI xsd) throws EolModelLoadingException {

        final XmlModel model = xmlModelFactory.create(resourceSet);

        final StringProperties properties = new StringProperties();
        properties.put(XmlModel.PROPERTY_NAME, xmlModelContext.getName() + "");
        if (xmlModelContext.getAliases() != null) {
            properties.put(XmlModel.PROPERTY_ALIASES, xmlModelContext.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(XmlModel.PROPERTY_ALIASES, "");
        }
        properties.put(XmlModel.PROPERTY_READONLOAD, xmlModelContext.getReadOnLoad() + "");
        properties.put(XmlModel.PROPERTY_STOREONDISPOSAL, xmlModelContext.getStoreOnDisposal() + "");
        properties.put(XmlModel.PROPERTY_EXPAND, xmlModelContext.getExpand() + "");
        properties.put(XmlModel.PROPERTY_CACHED, xmlModelContext.getCached() + "");
        properties.put(XmlModel.PROPERTY_XSD_FILE, xsd + "");

        properties.put(XmlModel.PROPERTY_MODEL_URI, uri);

        model.load(properties);
        model.setName(xmlModelContext.getName());
        if (xmlModelContext.getValidateModel() && !ModelValidator.getValidationErrors(Optional.of(log), model).isEmpty()) {
            throw new IllegalStateException("Invalid model: " + model.getName());
        }

        repository.addModel(model);
        return model;
    }
}