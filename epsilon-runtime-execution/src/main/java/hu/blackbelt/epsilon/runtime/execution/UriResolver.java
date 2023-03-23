package hu.blackbelt.epsilon.runtime.execution;

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
