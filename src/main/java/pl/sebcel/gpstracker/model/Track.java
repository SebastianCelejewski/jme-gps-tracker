package pl.sebcel.gpstracker.model;

import java.util.Date;
import java.util.Vector;

public class Track {

    private String id;
    private Date startDate;
    private Vector points = new Vector();
    private double distance;

    public Track(String id, Date startDate) {
        this.id = id;
        this.startDate = startDate;
        this.distance = 0;
    }

    public void addPoint(TrackPoint point) {
        points.addElement(point);
    }

    public String getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Vector getPoints() {
        return points;
    }

    public double getDistance() {
        return distance;
    }

    public void addDistance(double distanceDelta) {
        this.distance = distance + distanceDelta;
    }
}