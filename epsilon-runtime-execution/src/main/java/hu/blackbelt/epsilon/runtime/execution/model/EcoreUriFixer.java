package hu.blackbelt.epsilon.runtime.execution.model;

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

import org.eclipse.emf.common.util.DelegatingResourceLocator;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import java.lang.reflect.Field;
import java.net.URL;

public class EcoreUriFixer {

    // Ugly hack: The EcorePlugin baseUri has an extra trailing slash
    // on OSGi, so we try to fix it
    public static void fixEcoreUri() {
        try {
            URL baseUrl = EcorePlugin.INSTANCE.getBaseURL();
            if (baseUrl.toString().startsWith("bundle:") && baseUrl.toString().endsWith("//")) {
                URL fixedUrl = new URL(baseUrl.toString().substring(0, baseUrl.toString().length() - 1));
                Field myField = getField(DelegatingResourceLocator.class, "baseURL");
                myField.setAccessible(true);
                myField.set(EcorePlugin.INSTANCE, fixedUrl);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }

    }

    private static Field getField(Class clazz, String fieldName)
            throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

}
