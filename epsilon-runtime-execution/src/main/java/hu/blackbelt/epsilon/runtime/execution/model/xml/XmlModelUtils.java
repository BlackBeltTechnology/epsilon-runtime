package hu.blackbelt.epsilon.runtime.execution.model.xml;

import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.model.ModelValidator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.xml.XmlModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.*;

import java.io.File;

import static hu.blackbelt.epsilon.runtime.execution.EmfUtils.convertFileToUri;
import static java.util.stream.Collectors.joining;

public final class XmlModelUtils {

    public static XmlModel loadXml(Log log, ResourceSet resourceSet, ModelRepository repository, XmlModelContext xmlModelContext, URI uri, URI xsd) throws EolModelLoadingException {

        final XmlModel model = createXmlModel(resourceSet);

        final StringProperties properties = new StringProperties();
        properties.put(XmlModel.PROPERTY_NAME, xmlModelContext.getName() + "");
        if (xmlModelContext.getAliases() != null) {
            properties.put(XmlModel.PROPERTY_ALIASES, xmlModelContext.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(XmlModel.PROPERTY_ALIASES, "");
        }
        properties.put(XmlModel.PROPERTY_READONLOAD, xmlModelContext.isReadOnLoad()+ "");
        properties.put(XmlModel.PROPERTY_STOREONDISPOSAL, xmlModelContext.isStoreOnDisposal() + "");
        properties.put(XmlModel.PROPERTY_EXPAND, xmlModelContext.isExpand() + "");
        properties.put(XmlModel.PROPERTY_CACHED, xmlModelContext.isCached() + "");
        properties.put(XmlModel.PROPERTY_REUSE_UNMODIFIED_FILE_BASED_METAMODELS, xmlModelContext.isReuseUnmodifiedFileBasedMetamodels() + "");
        properties.put(XmlModel.PROPERTY_XSD_FILE, xsd + "");

        String metamodelUri = xmlModelContext.getMetaModelUris().stream().collect(joining(","));
        //File modelFile = emfModel.getModelFile();
        String modelUri = xmlModelContext.getMetaModelUris().stream().collect(joining(","));
        File metamodelFile = xmlModelContext.getMetaModelFile();

        if (metamodelUri != null) {
            properties.put(XmlModel.PROPERTY_METAMODEL_URI, metamodelUri + "");
        }

        /*
        if (modelFile != null && modelUri != null) {
            throw new MojoExecutionException("Only one of modelFile or modelUri may be used");
        } else if (modelUri != null) {
            properties.put(XmlModel.PROPERTY_MODEL_URI, modelUri);
        } else {
            properties.put(XmlModel.PROPERTY_MODEL_URI, convertFileToUri(modelFile));
        }
        */
        properties.put(XmlModel.PROPERTY_MODEL_URI, uri);
        /*
        log.info("Registering MODEL_URI:" + uri.toString());
        */
        
        if (metamodelFile != null) {
            log.info("Using file base metamodel: " + metamodelFile);
            properties.put(XmlModel.PROPERTY_FILE_BASED_METAMODEL_URI, convertFileToUri(metamodelFile));
        }

        if (xmlModelContext.getPlatformAlias() != null && !xmlModelContext.getPlatformAlias().trim().equals("")) {
            properties.put(XmlModel.PROPERTY_MODEL_URI, xmlModelContext.getPlatformAlias());
            log.info(String.format("Registering MODEL_URI: %s Alias URI: %s" , uri.toString(), xmlModelContext.getPlatformAlias()));
            URIConverter.INSTANCE.URI_MAP.put(URI.createURI(xmlModelContext.getPlatformAlias()), uri);
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
        model.setName(xmlModelContext.getName());
        if (xmlModelContext.validateModel && !ModelValidator.getValidationErrors(log, model).isEmpty()) {
            throw new IllegalStateException("Invalid model: " + model.getName());
        }

        repository.addModel(model);
        return model;
    }

    public static XmlModel createXmlModel(ResourceSet resourceSet) {
        return new OptimizedXmlModel();
        //return new EmfModel();
    }

}
