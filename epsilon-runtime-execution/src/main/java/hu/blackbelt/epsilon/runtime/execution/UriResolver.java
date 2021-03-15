package hu.blackbelt.epsilon.runtime.execution;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URISyntaxException;

public class UriResolver {

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateURI(Class domainClass, String path) {
        URI classRoot = domainClass.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (classRoot.toString().endsWith(".jar")) {
            classRoot = new URI("jar:" + classRoot.toString() + "!/" + path);
        } else if (classRoot.toString().startsWith("jar:bundle:")) {
            classRoot = new URI(classRoot.toString().substring(4, classRoot.toString().indexOf("!")) + path);
        } else {
            if (classRoot.toString().endsWith("/")) {
                classRoot = new URI(classRoot.toString() + path);
            } else {
                classRoot = new URI(classRoot.toString() + "/" + path);
            }
        }
        return classRoot;
    }

}
