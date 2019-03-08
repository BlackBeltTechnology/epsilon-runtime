package hu.blackbelt.epsilon.runtime.execution.model.emf;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import static java.util.stream.Collectors.joining;

public interface EmfModelFactory {

    EmfModel create(ResourceSet resourceSet);

    static EmfModel loadEmf(Log log,
                     EmfModelFactory emfModelFactory, ResourceSet resourceSet,
                     ModelRepository repository, EmfModelContext emfModel, URI uri) throws EolModelLoadingException {

        EmfModel model = emfModelFactory.create(resourceSet);

        final StringProperties properties = new StringProperties();
        properties.put(EmfModel.PROPERTY_NAME, emfModel.getName() + "");
        if (emfModel.getAliases() != null && emfModel.getAliases().size() > 0) {
            properties.put(EmfModel.PROPERTY_ALIASES, emfModel.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(EmfModel.PROPERTY_ALIASES, "");
        }
        properties.put(EmfModel.PROPERTY_READONLOAD, emfModel.getReadOnLoad() + "");
        properties.put(EmfModel.PROPERTY_STOREONDISPOSAL, emfModel.getStoreOnDisposal() + "");
        properties.put(EmfModel.PROPERTY_EXPAND, emfModel.getExpand() + "");
        properties.put(EmfModel.PROPERTY_CACHED, emfModel.getCached() + "");

        if (emfModel.getReferenceUri() != null && !emfModel.getReferenceUri().trim().equals("")) {
            properties.put(EmfModel.PROPERTY_MODEL_URI, emfModel.getReferenceUri());
            log.info(String.format("Registering MODEL_URI: %s Alias URI: %s", uri.toString(), emfModel.getReferenceUri()));
            resourceSet.getURIConverter().getURIMap().put(URI.createURI(emfModel.getReferenceUri()), uri);
        } else {
            log.info(String.format("Registering MODEL_URI: %s", uri.toString()));
        }
        properties.put(EmfModel.PROPERTY_MODEL_URI, uri);
        model.load(properties);
        model.setName(emfModel.getName());
        if (emfModel.validateModel && !ModelValidator.getValidationErrors(log, model).isEmpty()) {
            throw new IllegalStateException("Invalid model: " + model.getName());
        }

        repository.addModel(model);

        return model;
    }
}
