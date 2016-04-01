package pl.sebcel.gpstracker.utils;

import java.util.Vector;

public class StringUtils {

    public static String[] split(String input, char separator) {
        Vector pieces = new Vector();
        int currentIdx = 0;
        int nextIdx = -1;
        do {
            nextIdx = input.indexOf(separator, currentIdx);
            if (nextIdx != -1) {
                String token = input.substring(currentIdx, nextIdx);
                if (token.length() > 0) {
                    pieces.addElement(token);
                }
                currentIdx = nextIdx + 1;
            }
        } while (nextIdx != -1);
        if (currentIdx != -1) {
            String token = input.substring(currentIdx);
            if (token.length() > 0) {
                pieces.addElement(token);
            }
        }

        String[] result = new String[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
            result[i] = (String) pieces.elementAt(i);
        }
        return result;
    }
}