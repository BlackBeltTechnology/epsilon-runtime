package hu.blackbelt.epsilon.runtime.utils;

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
