package pl.sebcel.gpstracker.location;

import pl.sebcel.gpstracker.TestUtils;
import pl.sebcel.gpstracker.location.NmeaInfo.SatelliteInfo;

public class NmeaDecoder {

    public NmeaInfo decode(String nmeaInfo) {
        System.out.println(nmeaInfo);
        NmeaInfo result = new NmeaInfo();
        String[] lines = TestUtils.split(nmeaInfo, "\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.startsWith("$GPGGA")) {
                parseGPGGA(result, line);
            }
            if (line.startsWith("$GPGSV")) {
                parseGPGSV(result, line);
            }
        }
        return result;
    }

    private void parseGPGGA(NmeaInfo result, String line) {
        String[] tokens = TestUtils.split(line, ",");
        int gpsQuality = Integer.parseInt(tokens[6]);
        int numberOfSatellitesInView = Integer.parseInt(tokens[7]);
        result.setGpsQuality(gpsQuality);
        result.setNumberOfSatellitesInView(numberOfSatellitesInView);
    }

    private void parseGPGSV(NmeaInfo result, String line) {
        String[] tokens = TestUtils.split(line.substring(0, line.length() - 4), ",");
        int numberOfEntries = (tokens.length - 4) / 4;
        for (int i = 0; i < numberOfEntries; i++) {
            int satelliteNumber = Integer.parseInt(tokens[i * 4 + 4]);
            int elevation = Integer.parseInt(tokens[i * 4 + 5]);
            int azimuth = Integer.parseInt(tokens[i * 4 + 6]);
            Integer snr = null;
            if (tokens[i * 4 + 7].length() > 0) {
                snr = new Integer(Integer.parseInt(tokens[i * 4 + 7]));
            }

            SatelliteInfo satelliteInfo = new SatelliteInfo();
            satelliteInfo.setNumber(satelliteNumber);
            satelliteInfo.setElevation(elevation);
            satelliteInfo.setAzimuth(azimuth);
            satelliteInfo.setSnr(snr);

            result.getSatellitesInfo().put(new Integer(satelliteNumber), satelliteInfo);
        }
    }
}