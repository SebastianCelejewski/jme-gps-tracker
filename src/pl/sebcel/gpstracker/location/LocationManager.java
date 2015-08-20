package pl.sebcel.gpstracker.location;

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

    public LocationManager(AppState appState, LocationListener locationListener) {
        log.debug("[LocationManager] initialization");
        this.appState = appState;
        this.locationListener = locationListener;
    }

    public void start() {
        log.debug("[LocationManager] starting");
        if (alreadyStarted) {
            return;
        }
        alreadyStarted = true;
        final Criteria cr = new Criteria();
        cr.setHorizontalAccuracy(500);
        new Thread(new Runnable() {

            public void run() {

                boolean locationFound = false;
                while (!locationFound) {

                    try {
                        log.debug("[LocationManager] looking for location");
                        appState.setGpsStatus(GpsStatus.LOCATING);
                        locationProvider = LocationProvider.getInstance(cr);
                        locationProvider.setLocationListener(locationListener, -1, -1, -1);
                        Location location = locationProvider.getLocation(60);
                        log.debug("[LocationManager] location found");
                        appState.setGpsStatus(GpsStatus.OK);
                        locationListener.locationUpdated(locationProvider, location);
                        locationFound = true;
                    } catch (Exception ex) {
                        log.debug("[LocationManager] failed to obtain location: " + ex.getMessage());
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
