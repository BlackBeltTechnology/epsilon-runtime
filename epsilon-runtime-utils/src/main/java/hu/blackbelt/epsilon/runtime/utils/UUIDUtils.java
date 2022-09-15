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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class UUIDUtils {

    public static String uuid3(String namespace, String name) throws IOException  {
        UUID nameSpace_OID_uuid = UUID.fromString(namespace);

        long msb = nameSpace_OID_uuid.getMostSignificantBits();
        long lsb = nameSpace_OID_uuid.getLeastSignificantBits();

        ByteArrayOutputStream outputStreamString = new ByteArrayOutputStream();
        int i = 0;
        while (i < 8) {
            outputStreamString.write(Long.valueOf(Long.rotateRight(msb, 8 * (7 - i))).intValue());
            i++;
        }
        while (i < 16) {
            outputStreamString.write(Long.valueOf(Long.rotateRight(lsb, 8 * (7 - i))).intValue());
            i++;
        }

        outputStreamString.write(name.getBytes());

        return UUID.nameUUIDFromBytes(outputStreamString.toByteArray()).toString();
    }

    public static String randomUUID () {
        return UUID.randomUUID().toString();
    }

}
