package pl.sebcel.gpstracker.model;

import java.util.Date;

public class TrackPoint {

    private Date dateTime;
    private double latitude;
    private double longitude;

    public TrackPoint(Date dateTime, double latitude, double longitude) {
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
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
}