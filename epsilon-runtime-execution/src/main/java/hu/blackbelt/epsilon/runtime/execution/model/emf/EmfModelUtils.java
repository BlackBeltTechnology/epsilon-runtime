package hu.blackbelt.epsilon.runtime.execution.model.emf;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.Map;

import static java.util.stream.Collectors.joining;

public class EmfModelUtils {
    public static EmfModel loadEmf(Log log,
                                   EmfModelFactory emfModelFactory, ResourceSet resourceSet,
                                   ModelRepository repository, EmfModelContext emfModel, URI uri, Map<URI, URI> uriMap) throws EolModelLoadingException {

        // Hack: to able to resolve supertypes
        Map<URI, URI> uriMapExtended = Maps.newHashMap(uriMap);
        uriMapExtended.put(URI.createURI(""), uri);

        EmfModel model = emfModelFactory.create(resourceSet, uriMapExtended);

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
            log.info(String.format("Registering MODEL_URI: %s Reference URI: %s", uri.toString(), emfModel.getReferenceUri()));
            URI referenceURI = URI.createURI(emfModel.getReferenceUri());
            properties.put(EmfModel.PROPERTY_MODEL_URI, uri);
            resourceSet.getURIConverter().getURIMap().put(referenceURI, uri);
        } else {
            log.info(String.format("Registering MODEL_URI: %s", uri.toString()));
            properties.put(EmfModel.PROPERTY_MODEL_URI, uri);
        }

        /*
        // Adding resource based reference URI's
        // model.getResource().getResourceSet().getURIConverter().getURIMap().putAll(uriMap);
        // resourceSet.getURIConverter().getURIMap().putAll(uriMap);
        if (uriMap != null) {
            for (URI from : uriMap.keySet()) {
                URI to = uriMap.get(from);
                log.info(String.format("Registering URI: %s --> %s", from.toString(), to.toString()));
                resourceSet.getURIConverter().getURIMap().put(from, to);
            }
        }
        */

        model.load(properties);
        model.setName(emfModel.getName());
        if (emfModel.validateModel && !ModelValidator.getValidationErrors(log, model).isEmpty()) {
            throw new IllegalStateException("Invalid model: " + model.getName());
        }

        repository.addModel(model);

        return model;
    }
}
