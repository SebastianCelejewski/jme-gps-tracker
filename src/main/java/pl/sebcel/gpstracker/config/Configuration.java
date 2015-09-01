package pl.sebcel.gpstracker.config;

public class Configuration {

    private int locationInterval;
    private int saveInterval;
    private int gpsHorizontalAccuracy;
    private int gpsLocationFindTimeout;
    private int gpsLocationFindRetryDelay;

    public int getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(int locationInterval) {
        this.locationInterval = locationInterval;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public int getGpsHorizontalAccuracy() {
        return gpsHorizontalAccuracy;
    }

    public void setGpsHorizontalAccuracy(int gpsHorizontalAccuracy) {
        this.gpsHorizontalAccuracy = gpsHorizontalAccuracy;
    }

    public int getGpsLocationFindTimeout() {
        return gpsLocationFindTimeout;
    }

    public void setGpsLocationFindTimeout(int gpsLocationFindTimeout) {
        this.gpsLocationFindTimeout = gpsLocationFindTimeout;
    }

    public int getGpsLocationFindRetryDelay() {
        return gpsLocationFindRetryDelay;
    }

    public void setGpsLocationFindRetryDelay(int gpsLocationFindRetryDelay) {
        this.gpsLocationFindRetryDelay = gpsLocationFindRetryDelay;
    }

    public String toString() {
        return "Configuration [locationInterval=" + locationInterval + ", saveInterval=" + saveInterval + ", gpsHorizontalAccuracy=" + gpsHorizontalAccuracy + ", gpsLocationFindTimeout=" + gpsLocationFindTimeout + ", gpsLocationFindRetryDelay=" + gpsLocationFindRetryDelay + "]";
    }

}