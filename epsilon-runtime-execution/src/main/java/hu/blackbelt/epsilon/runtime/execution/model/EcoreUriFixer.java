package hu.blackbelt.epsilon.runtime.execution.model;

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
