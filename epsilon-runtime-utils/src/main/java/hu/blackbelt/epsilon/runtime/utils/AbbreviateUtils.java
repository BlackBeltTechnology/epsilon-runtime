package hu.blackbelt.epsilon.runtime.utils;

/*-
 * #%L
 * epsilon-runtime-utils
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

public class AbbreviateUtils {

    public static String abbreviate(String text, Integer maxLength) {
        if (text.length() > maxLength) {
            String cns = text.substring(0,2) + text.substring(2).replace("a", "").replace("e","").replace("i", "").replace("o","").replace("u","");
            int pos = cns.length() - 2;
            while (cns.length() > maxLength) {
                if (cns.substring(pos, pos + 1).matches("[a-z]")) {
                    cns = cns.substring(0, pos) + cns.substring(pos + 1);
                } else if (cns.substring(pos, pos + 1).matches("[0-9]")){
                    cns = cns.substring(0, pos-1) + cns.substring(pos, pos + 1).toLowerCase() + cns.substring(pos + 1);
                } else {
                    cns = cns.substring(0, pos) + cns.substring(pos, pos + 1).toLowerCase() + cns.substring(pos + 1);
                }
                pos = pos - 3;
                if (pos < 2) {
                    pos = cns.length() - 2;
                }
            }
            return cns.toLowerCase();
        } else {
            return text.toLowerCase();
        }
    }
}
