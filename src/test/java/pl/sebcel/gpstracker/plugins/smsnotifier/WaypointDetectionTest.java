package pl.sebcel.gpstracker.plugins.smsnotifier;

import java.io.InputStream;
import java.util.Vector;

import javax.microedition.location.Coordinates;

import junit.framework.TestCase;

public class WaypointDetectionTest extends TestCase {

    public void testHopsa() throws Exception {

        WaypointManager waypointManager = new WaypointManager();

        String data = loadFile("/2015-09-03_14-38-26.dta");
        String[] lines = split(data, "\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].length() > 0) {
                String[] tokens = split(lines[i], ";");
                double lat = Double.parseDouble(tokens[1]);
                double lon = Double.parseDouble(tokens[2]);

                String info = waypointManager.getWaypointInfo(new CustomCoordinates(lat, lon, 0));
                if (info != null && info.length() > 0) {
                    System.out.println(info);
                }
            }
        }
    }

    private String loadFile(String filePath) {
        try {
            InputStream in = this.getClass().getResourceAsStream(filePath);
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

    private String[] split(String input, String delimiter) {
        Vector lines = new Vector();
        int lineBreakIndex = -1;
        do {
            lineBreakIndex = input.indexOf(delimiter);
            if (lineBreakIndex > -1) {
                String line = input.substring(0, lineBreakIndex);
                lines.addElement(line);
                input = input.substring(lineBreakIndex + 1);
            } else {
                lines.addElement(input);
            }
        } while (lineBreakIndex > -1);

        String[] result = new String[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            result[i] = (String) lines.elementAt(i);
        }
        return result;

    }
}