package hu.blackbelt.epsilon.runtime.execution.model.xml;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.DefaultRuntimeEmfModelFactory;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelFactory;
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

    private String xml;

    private String xsd;

    private XmlModelFactory xmlModelFactory = new DefaultRuntimeXmlModelFactory();

    public XmlModelContext() {
    }

    @Builder(builderMethodName = "buildXmlModelContext")
    public XmlModelContext(String xml, String xsd, String name, List<String> aliases, File metaModelFile,
                           String platformAlias, boolean readOnLoad, boolean storeOnDisposal, boolean cached,
                           boolean expand, List<String> metaModelUris, List<String> fileBasedMetamodelUris,
                           String modelUri, boolean reuseUnmodifiedFileBasedMetamodels, boolean validateModel, XmlModelFactory xmlModelFactory) {
        super(null, name, aliases, metaModelFile, platformAlias, readOnLoad, storeOnDisposal, cached, expand,
                metaModelUris, validateModel, null);
        if (xmlModelFactory != null) {
            this.xmlModelFactory = xmlModelFactory;
        }
        this.xml = xml;
        this.xsd = xsd;
    }


    @Override
    public String toString() {
        return "XmlModel{" +
                "artifact='" + getArtifacts() + '\'' +
                ", name='" + getName() + '\'' +
                ", aliases=" + getAliases() +
                ", readOnLoad=" + getReadOnLoad() +
                ", storeOnDisposal=" + getStoreOnDisposal() +
                ", cached=" + getCached() +
                ", metaModelFile=" + getMetaModelFile() +
                ", platformAlias='" + getPlatformAlias() + '\'' +
                ", expand=" + getExpand() +
                ", metaModelUris=" + getMetaModelUris() +
                ", validateModel='" + getValidateModel() + '\'' +
                ", xmlModelFactory='" + xmlModelFactory + '\'' +
                '}';
    }


    @Override
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of("xml", xml, "xsd", xsd);
    }

    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap) throws EolModelLoadingException {
        return XmlModelUtils.loadXml(log, resourceSet, repository, this, uriMap.get("xml"), uriMap.get("xsd"));
    }

}
