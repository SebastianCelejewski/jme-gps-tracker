package pl.sebcel.gpstracker.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import pl.sebcel.gpstracker.AppState;
import pl.sebcel.gpstracker.GpsStatus;

public class LocationManager {

    private AppState appState;
    private LocationProvider locationProvider;
    private boolean alreadyStarted = false;
    private LocationListener locationListener; 
    
    public LocationManager(AppState appState, LocationListener locationListener) {
        this.appState = appState;
        this.locationListener = locationListener;
    }

    public void start() {
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
                        appState.setGpsStatus(GpsStatus.LOCATING);
                        locationProvider = LocationProvider.getInstance(cr);
                        locationProvider.setLocationListener(locationListener, -1, -1, -1);
                        Location location = locationProvider.getLocation(60);
                        appState.setGpsStatus(GpsStatus.OK);
                        locationListener.locationUpdated(locationProvider, location);
                        locationFound = true;
                    } catch (Exception ex) {
                        appState.setGpsStatus(GpsStatus.NOT_AVAILABLE);
                       try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }).start();
    }
}