package pl.sebcel.gpstracker.model;

import java.util.Date;

public class TrackPoint {

    private Date dateTime;
    private double latitude;
    private double longitude;
    private double altitude;
    private double distance;
    private double horizontalAccuracy;
    private double verticalAccuracy;

    public TrackPoint(Date dateTime, double latitude, double longitude, double altitude, double distance, double horizontalAccuracy, double verticalAccuracy) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.altitude = altitude;
        this.horizontalAccuracy = horizontalAccuracy;
        this.verticalAccuracy = verticalAccuracy;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getDistance() {
        return distance;
    }

    public double getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public double getVerticalAccuracy() {
        return verticalAccuracy;
    }
}