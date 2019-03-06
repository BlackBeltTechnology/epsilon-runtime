package hu.blackbelt.epsilon.runtime.execution.model.plainxml;

import hu.blackbelt.epsilon.runtime.execution.Log;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.plainxml.PlainXmlModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import static java.util.stream.Collectors.joining;

public class PlainXmlModelUtil {

    public static PlainXmlModel loadPlainXml(Log log, ResourceSet resourceSet, ModelRepository repository, PlainXmlModelContext plainXmlModelContext, URI uri) throws EolModelLoadingException {

        final PlainXmlModel model = new PlainXmlModel();

        final StringProperties properties = new StringProperties();
        properties.put(PlainXmlModel.PROPERTY_NAME, plainXmlModelContext.getName() + "");
        if (plainXmlModelContext.getAliases() != null) {
            properties.put(PlainXmlModel.PROPERTY_ALIASES, plainXmlModelContext.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(PlainXmlModel.PROPERTY_ALIASES, "");
        }
        properties.put(PlainXmlModel.PROPERTY_READONLOAD, plainXmlModelContext.isReadOnLoad()+ "");
        properties.put(PlainXmlModel.PROPERTY_STOREONDISPOSAL, plainXmlModelContext.isStoreOnDisposal() + "");
        properties.put(PlainXmlModel.PROPERTY_CACHED, plainXmlModelContext.isCached() + "");

        properties.put(PlainXmlModel.PROPERTY_URI, uri);
        // model.setFile(new File(uri.toFileString()));
        /*
        log.info("Registering MODEL_URI:" + uri.toString());
        */

        if (plainXmlModelContext.getPlatformAlias() != null && !plainXmlModelContext.getPlatformAlias().trim().equals("")) {
            properties.put(PlainXmlModel.PROPERTY_URI, plainXmlModelContext.getPlatformAlias());
            log.info(String.format("Registering MODEL_URI: %s Alias URI: %s" , uri.toString(), plainXmlModelContext.getPlatformAlias()));
            resourceSet.getURIConverter().getURIMap().put(URI.createURI(plainXmlModelContext.getPlatformAlias()), uri);
        } else {
            log.info(String.format("Registering MODEL_URI: %s", uri.toString()));
        }

        /* TODO: Find a way to handle relative pathes on ecoreModels */
        /*
        if (emfModel.getUrlAliases() != null) {
            for (String urlAlias : emfModel.getUrlAliases()) {
                if (!urlAlias.trim().equals("")) {
                    log.info(String.format("Adding URL alias: %s", urlAlias));
                    URIConverter.INSTANCE.URI_MAP.put(URI.createFileURI(urlAlias), uri);
                }
            }
        } */

        model.load(properties);
        model.setName(plainXmlModelContext.getName());
        repository.addModel(model);
        return model;
    }


}
