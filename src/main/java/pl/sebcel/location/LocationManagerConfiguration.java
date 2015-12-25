package pl.sebcel.location;

public class LocationManagerConfiguration {

    private int gpsLocationInterval;
    private int gpsLocationSingalLossTimeout;
    private int gpsHorizontalAccuracyForLocationProvider;
    private int gpsHorizontalAccuracyForTrackPointsFiltering;
    private int gpsLocationFindTimeout;
    private int gpsLocationFindRetryDelay;

    public int getGpsLocationInterval() {
        return gpsLocationInterval;
    }

    public void setGpsLocationInterval(int gpsLocationInterval) {
        this.gpsLocationInterval = gpsLocationInterval;
    }

    public int getGpsHorizontalAccuracyForLocationProvider() {
        return gpsHorizontalAccuracyForLocationProvider;
    }

    public void setGpsHorizontalAccuracyForLocationProvider(int gpsHorizontalAccuracyForLocationProvider) {
        this.gpsHorizontalAccuracyForLocationProvider = gpsHorizontalAccuracyForLocationProvider;
    }

    public int getGpsHorizontalAccuracyForTrackPointsFiltering() {
        return gpsHorizontalAccuracyForTrackPointsFiltering;
    }

    public void setGpsHorizontalAccuracyForTrackPointsFiltering(int gpsHorizontalAccuracyForTrackPointsFiltering) {
        this.gpsHorizontalAccuracyForTrackPointsFiltering = gpsHorizontalAccuracyForTrackPointsFiltering;
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
        return "Configuration [locationInterval=" + gpsLocationInterval + ", gpsHorizontalAccuracy=" + gpsHorizontalAccuracyForLocationProvider + ", gpsLocationFindTimeout=" + gpsLocationFindTimeout + ", gpsLocationFindRetryDelay=" + gpsLocationFindRetryDelay + "]";
    }

}