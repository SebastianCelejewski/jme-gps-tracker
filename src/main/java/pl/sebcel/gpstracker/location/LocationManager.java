package pl.sebcel.gpstracker.location;

import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import pl.sebcel.gpstracker.config.Configuration;
import pl.sebcel.gpstracker.state.AppState;
import pl.sebcel.gpstracker.state.GpsStatus;
import pl.sebcel.gpstracker.utils.Logger;

public class LocationManager implements LocationListener {

    private final Logger log = Logger.getLogger();

    private AppState appState;
    private LocationProvider locationProvider;
    private boolean alreadyStarted = false;
    private Vector locationListeners = new Vector();
    private Display display;
    private Configuration config;

    public LocationManager(AppState appState, Configuration config, Display display) {
        log.debug("[LocationManager] Initialization");
        this.appState = appState;
        this.display = display;
        this.config = config;
    }
    
    public void addLocationListener(LocationListener locationListener) {
        this.locationListeners.addElement(locationListener);
    }

    public void start() {
        log.debug("[LocationManager] Starting");
        if (alreadyStarted) {
            return;
        }
        alreadyStarted = true;
        final Criteria cr = new Criteria();
        cr.setHorizontalAccuracy(config.getGpsHorizontalAccuracyForLocationProvider());
        new Thread(new Runnable() {

            public void run() {

                boolean locationFound = false;
                Date startDate = new Date();
                while (!locationFound) {

                    try {
                        log.debug("[LocationManager] Trying to find location");
                        appState.setGpsStatus(GpsStatus.LOCATING);
                        locationProvider = LocationProvider.getInstance(cr);
                        if (locationProvider == null) {
                            log.debug("[LocationManager] Location provider not found.");
                            appState.setGpsStatus(GpsStatus.NOT_AVAILABLE);
                            break;
                        }

                        log.debug("[LocationManager] LocationProvider found. " + locationProvider.getClass());
                        log.debug("[LocationManager] Config: " + config.toString());
                        locationProvider.setLocationListener(LocationManager.this, config.getGpsLocationInterval(), config.getGpsLocationInterval(), config.getGpsLocationInterval());
                        log.debug("[LocationManager] Location listener set");
                        Location location = locationProvider.getLocation(config.getGpsLocationFindTimeout());
                        Date endDate = new Date();
                        long duration = (endDate.getTime() - startDate.getTime()) / 1000;
                        log.debug("[LocationManager] Location found (" + duration + " seconds)");
                        appState.setGpsStatus(GpsStatus.OK);
                        locationUpdated(locationProvider, location);
                        locationFound = true;
                        AlertType.INFO.playSound(display);
                    } catch (Exception ex) {
                        log.debug("[LocationManager] Failed to find location: " + ex.getClass() + ", " + ex.getMessage());
                        try {
                            Thread.sleep(config.getGpsLocationFindRetryDelay() * 1000);
                        } catch (InterruptedException e) {
                            // intentional
                        }
                    }
                }
            }
        }).start();
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        for (int i = 0; i < locationListeners.size(); i++) {
            LocationListener listener = (LocationListener) locationListeners.elementAt(i);
            listener.locationUpdated(provider, location);
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        for (int i = 0; i < locationListeners.size(); i++) {
            LocationListener listener = (LocationListener) locationListeners.elementAt(i);
            listener.providerStateChanged(provider, newState);
        }
    }
}