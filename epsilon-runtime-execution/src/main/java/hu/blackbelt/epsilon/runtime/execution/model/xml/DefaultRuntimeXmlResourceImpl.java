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


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import java.util.HashMap;

public class DefaultRuntimeXmlResourceImpl extends XMLResourceImpl {


    protected boolean useXmiIds = super.useUUIDs();

    public DefaultRuntimeXmlResourceImpl() {
        super();
        setOptimizedOptions();
    }

    public DefaultRuntimeXmlResourceImpl(URI uri) {
        super(uri);
        setOptimizedOptions();
    }

    /*
     * TODO: Parameters have to delegate to maven
     */
    private void setOptimizedOptions() {
        super.setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_DEPRECATED_METHODS, false);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl(10000, true));
        this.getDefaultLoadOptions().put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<Object,Object>());
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DISABLE_NOTIFY, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
    }

    @Override
    protected boolean useUUIDs() {
        return useXmiIds;
    }

    public void setUseXmiIds(boolean useXmiIds) {
        this.useXmiIds = useXmiIds;
    }

    public boolean isUseXmiIds() {
        return useXmiIds;
    }

    public static class Factory implements Resource.Factory {

        @Override
        public Resource createResource(URI uri) {
            return new DefaultRuntimeXmlResourceImpl(uri);
        }

    }


}

