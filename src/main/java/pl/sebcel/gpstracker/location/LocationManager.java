package pl.sebcel.gpstracker.location;

import java.util.Date;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import pl.sebcel.gpstracker.AppState;
import pl.sebcel.gpstracker.GpsStatus;
import pl.sebcel.gpstracker.utils.Logger;

public class LocationManager {

    private AppState appState;
    private LocationProvider locationProvider;
    private boolean alreadyStarted = false;
    private LocationListener locationListener;
    private Logger log = Logger.getLogger();
    private Display display;

    public LocationManager(AppState appState, Display display, LocationListener locationListener) {
        log.debug("[LocationManager] Initialization");
        this.appState = appState;
        this.locationListener = locationListener;
        this.display = display;
    }

    public void start() {
        log.debug("[LocationManager] Starting");
        if (alreadyStarted) {
            return;
        }
        alreadyStarted = true;
        final Criteria cr = new Criteria();
        cr.setHorizontalAccuracy(500);
        new Thread(new Runnable() {

            public void run() {

                boolean locationFound = false;
                Date startDate = new Date();
                while (!locationFound) {

                    try {
                        log.debug("[LocationManager] Trying to find location");
                        appState.setGpsStatus(GpsStatus.LOCATING);
                        locationProvider = LocationProvider.getInstance(cr);
                        locationProvider.setLocationListener(locationListener, -1, -1, -1);
                        Location location = locationProvider.getLocation(600);
                        Date endDate = new Date();
                        long duration = (endDate.getTime() - startDate.getTime()) / 1000;
                        log.debug("[LocationManager] Location found (" + duration + " seconds)");
                        appState.setGpsStatus(GpsStatus.OK);
                        locationListener.locationUpdated(locationProvider, location);
                        locationFound = true;
                        AlertType.INFO.playSound(display);
                        AlertType.INFO.playSound(display);
                        AlertType.INFO.playSound(display);
                        AlertType.INFO.playSound(display);
                    } catch (Exception ex) {
                        log.debug("[LocationManager] Failed to find location: " + ex.getMessage());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }).start();
    }
}
