package hu.blackbelt.epsilon.runtime.execution.model.xml;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
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

    public static final String XML = "xml";
    public static final String XSD = "xsd";
    private String xml;

    private String xsd;

    private XmlModelFactory xmlModelFactory;


    @Builder(builderMethodName = "xmlModelContextBuilder")
    public XmlModelContext(Logger log, String xml, String xsd, String name, List<String> aliases,
                           String referenceUri, boolean readOnLoad, boolean storeOnDisposal, boolean cached,
                           boolean expand, boolean validateModel, XmlModelFactory xmlModelFactory, Map<String, String> uriConverterMap) {
        super(log, null, name, aliases, referenceUri, readOnLoad, storeOnDisposal, cached, expand,
                validateModel, null, uriConverterMap);
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
                "artifacts='" + getArtifacts() + '\'' +
                ", name='" + getName() + '\'' +
                ", aliases=" + getAliases() +
                ", uriConverterMap='" + getUriConverterMap() + '\'' +
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
        return ImmutableMap.of(XML, xml, XSD, xsd);
    }

    @Override
    public IModel load(Logger log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uriMap, Map<URI, URI> uriConverterMap) throws EolModelLoadingException {
        return XmlModelFactory.loadXml(log, xmlModelFactory, resourceSet, repository, this, uriMap.get(XML), uriMap.get(XSD));
    }

}
