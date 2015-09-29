package pl.sebcel.gpstracker.location;

import java.util.Hashtable;

public class NmeaInfo {

    private int gpsQuality;
    private int numberOfSatellitesInView;
    private Hashtable satellitesInfo = new Hashtable();

    public int getGpsQuality() {
        return gpsQuality;
    }

    public void setGpsQuality(int gpsQuality) {
        this.gpsQuality = gpsQuality;
    }

    public int getNumberOfSatellitesInView() {
        return numberOfSatellitesInView;
    }

    public void setNumberOfSatellitesInView(int numberOfSatellitesInView) {
        this.numberOfSatellitesInView = numberOfSatellitesInView;
    }

    public Hashtable getSatellitesInfo() {
        return satellitesInfo;
    }

    public void setSatellitesInfo(Hashtable satellitesInfo) {
        this.satellitesInfo = satellitesInfo;
    }

    public static class SatelliteInfo {
        private int number;
        private int elevation;
        private int azimuth;
        private Integer snr;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getElevation() {
            return elevation;
        }

        public void setElevation(int elevation) {
            this.elevation = elevation;
        }

        public int getAzimuth() {
            return azimuth;
        }

        public void setAzimuth(int azimuth) {
            this.azimuth = azimuth;
        }

        public Integer getSnr() {
            return snr;
        }

        public void setSnr(Integer snr) {
            this.snr = snr;
        }
    }
}