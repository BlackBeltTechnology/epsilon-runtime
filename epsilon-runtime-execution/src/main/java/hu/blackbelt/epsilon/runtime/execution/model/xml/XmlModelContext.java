package hu.blackbelt.epsilon.runtime.execution.model.xml;

import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class XmlModelContext extends EmfModelContext implements ModelContext {

    private String xml;

    private String xsd;

    private XmlModelFactory xmlModelFactory;

    @Builder(builderMethodName = "xmlModelContextBuilder")
    public XmlModelContext(Log log, String xml, String xsd, String name, List<String> aliases,
                           String platformAlias, boolean readOnLoad, boolean storeOnDisposal, boolean cached,
                           boolean expand, boolean validateModel, XmlModelFactory xmlModelFactory) {
        super(log, null, name, aliases, platformAlias, readOnLoad, storeOnDisposal, cached, expand,
                validateModel, null);
        this.xml = xml;
        this.xsd = xsd;

        if (xmlModelFactory != null) {
            this.xmlModelFactory = xmlModelFactory;
        } else {
            this.xmlModelFactory = new DefaultRuntimeXmlModelFactory(log);
        }

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
                ", referenceUri='" + getReferenceUri() + '\'' +
                ", expand=" + getExpand() +
                ", validateModel='" + getValidateModel() + '\'' +
                ", xmlModelFactory='" + xmlModelFactory + '\'' +
                ", log='" + getLog().getClass().getName() + '\'' +
                '}';
    }


    @Override
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of("xml", xml, "xsd", xsd);
    }

    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap) throws EolModelLoadingException {
        return XmlModelFactory.loadXml(log, xmlModelFactory, resourceSet, repository, this, uriMap.get("xml"), uriMap.get("xsd"));
    }

}
