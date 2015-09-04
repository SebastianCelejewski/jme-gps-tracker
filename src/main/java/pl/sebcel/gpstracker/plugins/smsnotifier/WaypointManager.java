package pl.sebcel.gpstracker.plugins.smsnotifier;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.location.Coordinates;

public class WaypointManager {

    private Hashtable waypoints = new Hashtable();

    public WaypointManager() {
        restart();
    }

    private void restart() {
        waypoints.clear();
        Waypoint p1 = new Waypoint(54.37473, 18.60453, "IHS Global");
        Waypoint p2 = new Waypoint(54.37953, 18.58886, "Skrzy¿owanie S³owackiego i Partyzantów");
        Waypoint p3 = new Waypoint(54.37664, 18.58055, "Cmentarz Srebrzysko");
        Waypoint p4 = new Waypoint(54.37417, 18.56735, "Rondo de la Salle");
        Waypoint p5 = new Waypoint(54.36745, 18.57133, "Skrzy¿owanie Potokowej i Rakoczego");
        Waypoint p6 = new Waypoint(54.36158, 18.55471, "Nowiec");
        Waypoint p7 = new Waypoint(54.35552, 18.54020, "Poligon");
        Waypoint p8 = new Waypoint(54.35113, 18.53507, "Wjazd na Poligon");
        Waypoint p9 = new Waypoint(54.34970, 18.53265, "Pod blokiem");

        waypoints.put(p1, Boolean.FALSE);
        waypoints.put(p2, Boolean.FALSE);
        waypoints.put(p3, Boolean.FALSE);
        waypoints.put(p4, Boolean.FALSE);
        waypoints.put(p5, Boolean.FALSE);
        waypoints.put(p6, Boolean.FALSE);
        waypoints.put(p7, Boolean.FALSE);
        waypoints.put(p8, Boolean.FALSE);
        waypoints.put(p9, Boolean.FALSE);
    }

    public String getWaypointInfo(Coordinates coordinates) {
        Enumeration keys = waypoints.keys();

        String info = "";
        while (keys.hasMoreElements()) {
            Waypoint waypoint = (Waypoint) keys.nextElement();

            float distance = coordinates.distance(new CustomCoordinates(waypoint.getLatitude(), waypoint.getLongitude(), 0));

            if (distance < 50) {
                if (!((Boolean) waypoints.get(waypoint)).booleanValue()) {
                    info += waypoint.getDescription();
                    waypoints.put(waypoint, Boolean.TRUE);
                }
            }
        }

        return info;
    }
}