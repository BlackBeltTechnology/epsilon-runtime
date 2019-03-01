package hu.blackbelt.epsilon.runtime.execution.model.xml;

import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import lombok.Builder;
import lombok.Data;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
public class XmlModelContext extends EmfModelContext implements ModelContext {

    /**
     * Validate model against Ecore metamodel and fail on validation errors.
     */
    boolean validateModel;

    public XmlModelContext() {
    }

    @Builder(builderMethodName = "buildXmlModelContext")
    public XmlModelContext(Map<String, String> artifacts, String name, List<String> aliases, File metaModelFile,
                           String platformAlias, boolean readOnLoad, boolean storeOnDisposal, boolean cached,
                           boolean expand, List<String> metaModelUris, List<String> fileBasedMetamodelUris,
                           String modelUri, boolean reuseUnmodifiedFileBasedMetamodels, boolean validateModel) {
        super(artifacts, name, aliases, metaModelFile, platformAlias, readOnLoad, storeOnDisposal, cached, expand,
                metaModelUris, fileBasedMetamodelUris, modelUri, reuseUnmodifiedFileBasedMetamodels, validateModel);
        this.validateModel = validateModel;
    }


    @Override
    public String toString() {
        return "XmlModel{" +
                "artifact='" + getArtifacts() + '\'' +
                ", name='" + getName() + '\'' +
                ", aliases=" + getAliases() +
                ", readOnLoad=" + isReadOnLoad() +
                ", storeOnDisposal=" + isStoreOnDisposal() +
                ", cached=" + isCached() +
                ", metaModelFile=" + getMetaModelFile() +
                ", platformAlias='" + getPlatformAlias() + '\'' +
                ", expand=" + isExpand() +
                ", metaModelUris=" + getMetaModelUris() +
                ", fileBasedMetamodelUris=" + getFileBasedMetamodelUris() +
                ", modelUri='" + getModelUri() + '\'' +
                ", reuseUnmodifiedFileBasedMetamodels=" + isReuseUnmodifiedFileBasedMetamodels() +
                ", validateModel='" + validateModel + '\'' +
                '}';
    }




    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap) throws EolModelLoadingException {
        return XmlModelUtils.loadXml(log, resourceSet, repository, this, uriMap.get("xml"), uriMap.get("xsd"));
    }

}
