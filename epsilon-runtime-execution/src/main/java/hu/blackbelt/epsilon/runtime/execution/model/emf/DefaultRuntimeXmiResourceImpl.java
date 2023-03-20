package hu.blackbelt.epsilon.runtime.execution.model.emf;

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


import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;

import java.util.HashMap;

@Slf4j
public class DefaultRuntimeXmiResourceImpl extends XMIResourceImpl {


    protected boolean useXmiIds = super.useUUIDs();

    public DefaultRuntimeXmiResourceImpl() {
        super();
        setOptimizedOptions();
    }

    public DefaultRuntimeXmiResourceImpl(URI uri) {
        super(uri);
        log.debug("Load optimized model - " + uri);
        setOptimizedOptions();
    }

    private void setOptimizedOptions() {
        super.setIntrinsicIDToEObjectMap(new HashMap<String, EObject>());
        this.getDefaultLoadOptions().put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMIResource.OPTION_USE_DEPRECATED_METHODS, false);
        this.getDefaultLoadOptions().put(XMIResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl(10000, true));
        this.getDefaultLoadOptions().put(XMIResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap<Object,Object>());
        this.getDefaultLoadOptions().put(XMIResource.OPTION_DISABLE_NOTIFY, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMIResource.OPTION_CONFIGURATION_CACHE, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_LAX_FEATURE_PROCESSING, Boolean.TRUE);
        this.getDefaultLoadOptions().put(XMLResource.OPTION_PROCESS_DANGLING_HREF, XMLResource.OPTION_PROCESS_DANGLING_HREF_DISCARD);
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
            return new DefaultRuntimeXmiResourceImpl(uri);
        }

    }


}

