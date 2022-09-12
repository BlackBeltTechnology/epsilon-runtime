package hu.blackbelt.epsilon.runtime.execution.model.plainxml;

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

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
        model.load(properties);
        model.setName(plainXmlModelContext.getName());
        repository.addModel(model);
        return model;
    }


}
