package pl.sebcel.gpstracker.model;

import java.util.Date;
import java.util.Vector;

public class Track {

    private String id;
    private Date startDate;
    private Vector points = new Vector();

    public Track(String id, Date startDate) {
        this.id = id;
        this.startDate = startDate;
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
}