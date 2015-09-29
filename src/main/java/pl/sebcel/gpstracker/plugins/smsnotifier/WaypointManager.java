package pl.sebcel.gpstracker.plugins.smsnotifier;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.location.Coordinates;

import pl.sebcel.gpstracker.utils.Logger;

public class WaypointManager {

    private final Logger log = Logger.getLogger();
    private Hashtable waypoints = new Hashtable();

    public WaypointManager() {
        log.debug("[WaypointManager] Initialization.");
        restart();
    }

    public void restart() {
        log.debug("[WaypointManager] Restarting route.");
        waypoints.clear();
        Waypoint p1 = new Waypoint(54.37473, 18.60453, "IHS_Global");
        Waypoint p2 = new Waypoint(54.37953, 18.58886, "Skrzyzowanie_Slowackiego_i_Partyzantow");
        Waypoint p3 = new Waypoint(54.37664, 18.58055, "Cmentarz_Srebrzysko");
        Waypoint p4 = new Waypoint(54.37417, 18.56735, "Rondo_de_la_Salle");
        Waypoint p5 = new Waypoint(54.36745, 18.57133, "Skrzyzowanie_Potokowej_i_Rakoczego");
        Waypoint p6 = new Waypoint(54.36158, 18.55471, "Nowiec");
        Waypoint p7 = new Waypoint(54.35552, 18.54020, "Poligon");
        Waypoint p8 = new Waypoint(54.35113, 18.53507, "Wjazd_na_poligon");
        Waypoint p9 = new Waypoint(54.34970, 18.53265, "Przytulna_26");
        Waypoint p10 = new Waypoint(54.352693, 18.519159, "Kladka_nad_obwodnica");
        Waypoint p11 = new Waypoint(54.352913, 18.512612, "Pozytywna_Szkola_Podstawowa");
        Waypoint p12 = new Waypoint(54.344235, 18.547326, "Rondo_Gronostajowe");
        Waypoint p13 = new Waypoint(54.353501, 18.549859, "PKM_Jasien");
        Waypoint p14 = new Waypoint(54.357762, 18.583543, "Morena");
        Waypoint p15 = new Waypoint(54.352346, 18.575258, "Migowo");
        Waypoint p16 = new Waypoint(54.349539, 18.552796, "Wrobla_Staw");

        waypoints.put(p1, Boolean.FALSE);
        waypoints.put(p2, Boolean.FALSE);
        waypoints.put(p3, Boolean.FALSE);
        waypoints.put(p4, Boolean.FALSE);
        waypoints.put(p5, Boolean.FALSE);
        waypoints.put(p6, Boolean.FALSE);
        waypoints.put(p7, Boolean.FALSE);
        waypoints.put(p8, Boolean.FALSE);
        waypoints.put(p9, Boolean.FALSE);
        waypoints.put(p10, Boolean.FALSE);
        waypoints.put(p11, Boolean.FALSE);
        waypoints.put(p12, Boolean.FALSE);
        waypoints.put(p13, Boolean.FALSE);
        waypoints.put(p14, Boolean.FALSE);
        waypoints.put(p15, Boolean.FALSE);
        waypoints.put(p16, Boolean.FALSE);

        log.debug("[WaypointManager] Loaded " + waypoints.size() + " waypoints.");
    }

    public String getWaypointInfo(Coordinates coordinates) {
        log.debug("[WaypointManager] Checking coordinates: " + coordinates.getLatitude() + " " + coordinates.getLongitude());
        Enumeration keys = waypoints.keys();

        String info = "";
        while (keys.hasMoreElements()) {
            Waypoint waypoint = (Waypoint) keys.nextElement();

            float distance = coordinates.distance(new CustomCoordinates(waypoint.getLatitude(), waypoint.getLongitude(), 0));

            if (distance < 50) {
                if (!((Boolean) waypoints.get(waypoint)).booleanValue()) {
                    log.debug("[WaypointManager] We are close to " + waypoint.getDescription());
                    info += waypoint.getDescription();
                    waypoints.put(waypoint, Boolean.TRUE);
                }
            }
        }

        log.debug("[WaypointManager] Result: " + info);

        return info;
    }
}