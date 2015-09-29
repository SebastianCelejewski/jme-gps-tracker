package pl.sebcel.gpstracker;

import java.io.InputStream;
import java.util.Vector;

public class TestUtils {

    public static String loadFile(String filePath) {
        try {
            InputStream in = TestUtils.class.getResourceAsStream(filePath);
            String data = "";

            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            do {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    data += new String(buffer, 0, bytesRead);
                }
            } while (bytesRead > 0);
            return data;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read from file " + filePath + ": " + ex.getMessage());
        }
    }

    public static String[] split(String text, String delimiter) {
        Vector lines = new Vector();
        int crlfPosition = -1;
        do {
            crlfPosition = text.indexOf(delimiter);
            if (crlfPosition != -1) {
                if (crlfPosition == 0) {
                    lines.addElement("");
                    text = text.substring(1);
                } else {
                    String line = text.substring(0, crlfPosition);
                    lines.addElement(line);
                    text = text.substring(crlfPosition + 1);
                }
            }

        } while (crlfPosition != -1);
        lines.addElement(text);

        String[] result = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            result[i] = (String) lines.elementAt(i);
        }
        return result;
    }
}
