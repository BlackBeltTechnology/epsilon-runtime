package hu.blackbelt.epsilon.runtime.utils;

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
