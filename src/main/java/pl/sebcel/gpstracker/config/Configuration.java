package pl.sebcel.gpstracker.config;

public class Configuration {

    private int saveInterval;
    private int gpsLocationInterval;
    private int gpsLocationSingalLossTimeout;
    private int gpsHorizontalAccuracy;
    private int gpsLocationFindTimeout;
    private int gpsLocationFindRetryDelay;

    public int getGpsLocationInterval() {
        return gpsLocationInterval;
    }

    public void setGpsLocationInterval(int gpsLocationInterval) {
        this.gpsLocationInterval = gpsLocationInterval;
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

    public int getGpsLocationSingalLossTimeout() {
        return gpsLocationSingalLossTimeout;
    }

    public void setGpsLocationSingalLossTimeout(int gpsLocationSingalLossTimeout) {
        this.gpsLocationSingalLossTimeout = gpsLocationSingalLossTimeout;
    }

    public String toString() {
        return "Configuration [locationInterval=" + gpsLocationInterval + ", saveInterval=" + saveInterval + ", gpsHorizontalAccuracy=" + gpsHorizontalAccuracy + ", gpsLocationFindTimeout=" + gpsLocationFindTimeout + ", gpsLocationFindRetryDelay=" + gpsLocationFindRetryDelay + "]";
    }

}