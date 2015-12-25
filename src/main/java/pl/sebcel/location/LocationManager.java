package pl.sebcel.location;

import java.util.Date;
import java.util.Vector;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.utils.Logger;

/**
 * Proxy for JSR-179 LocationProvider that provides more granular information about GPS status and handles oddities in location updates (like isValid set to false, negative altitude, lack of qualified coordinates, and horizontal accuracy above limit)
 * 
 * @author Sebastian Celejewski
 */
public class LocationManager implements LocationListener {

    private final Logger log = Logger.getLogger();

    private LocationProvider locationProvider;

    private LocationManagerConfiguration config;

    private GpsStatus gpsStatus = GpsStatus.UNINITIALIZED;
    private boolean alreadyinitialized = false;
    private long lastGpsUpdate = 0;

    private Vector locationListeners = new Vector();
    private Vector statusListeners = new Vector();

    public LocationManager(LocationManagerConfiguration config) {
        log.debug("[LocationManager] Initialization");
        this.config = config;
    }

    public void addLocationListener(LocationManagerGpsListener locationListener) {
        this.locationListeners.addElement(locationListener);
    }

    public void addStatusListener(LocationManagerStatusListener statusListener) {
        this.statusListeners.addElement(statusListener);
    }

    public void initialize() {
        log.debug("[LocationManager] Initialization started");

        if (alreadyinitialized) {
            log.debug("[LocationManager] Already initialized");
            return;
        }
        alreadyinitialized = true;

        // It can take very long time (e.g. 10-20 minutes), so we are putting it into a separate thread
        new Thread(new Runnable() {
            public void run() {
                initializeLocationProvider();
            }
        }).start();

        // Watchdog thread - it checks if GPS signal has been lost
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    checkIfGpsSignalIsLost();
                    try {
                        Thread.sleep(10000);
                    } catch (Exception ex) {
                        // intentional
                    }
                }
            }
        }).start();

        log.debug("[LocationManager] Initialization complete");
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        if (location == null) {
            log.debug("[LocationManager] Ignoring locationUpdated event: location is null.");
            return;
        }

        if (location.getQualifiedCoordinates() == null) {
            log.debug("[LocationManager] Ignoring locationUpdated event: qualified coordinates are null.");
            return;
        }

        QualifiedCoordinates coordinates = location.getQualifiedCoordinates();

        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();
        double altitude = coordinates.getAltitude();
        double horizontalAccuracy = coordinates.getHorizontalAccuracy();
        double verticalAccuracy = coordinates.getVerticalAccuracy();

        if (!location.isValid()) {
            log.debug("[LocationManager] Ignoring locationUpdated event: isValid flag is set to negative. Latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", horizontalAccuracy=" + horizontalAccuracy + ", verticalAccuracy=" + verticalAccuracy);
            return;
        }

        if (location.getQualifiedCoordinates().getAltitude() < 0) {
            log.debug("[LocationManager] Ignoring locationUpdated event: altitude is negative. Latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", horizontalAccuracy=" + horizontalAccuracy + ", verticalAccuracy=" + verticalAccuracy);
            return;
        }

        if (horizontalAccuracy > config.getGpsHorizontalAccuracyForTrackPointsFiltering()) {
            log.debug("[LocationManager] Handling locationUpdated event: invalid reading. horizontal accuracy (" + horizontalAccuracy + " m) is too weak. Expected accuracy must be lower than " + config.getGpsHorizontalAccuracyForTrackPointsFiltering() + " m. Latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", horizontalAccuracy=" + horizontalAccuracy + ", verticalAccuracy=" + verticalAccuracy);
            setGpsStatus(GpsStatus.INVALID_READING);
            return;
        }

        if (gpsStatus.equals(GpsStatus.SIGNAL_LOST)) {
            log.debug("[LocationManager] GPS signal found again.");
        }

        log.debug("[LocationManager] Handling locationUpdated event: valid reading. Latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", horizontalAccuracy=" + horizontalAccuracy + ", verticalAccuracy=" + verticalAccuracy);

        setGpsStatus(GpsStatus.OK);
        fireLocationChanged(coordinates);
        lastGpsUpdate = new Date().getTime();
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        if (newState == LocationProvider.AVAILABLE) {
            log.debug("[LocationManager] Handling providerStateChangedEvent AVAILABLE");
            setGpsStatus(GpsStatus.OK);
        }
        if (newState == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            log.debug("[LocationManager] Handling providerStateChangedEvent TEMPORARILY_UNAVAILABLE");
            setGpsStatus(GpsStatus.LOCATING);
        }
        if (newState == LocationProvider.OUT_OF_SERVICE) {
            log.debug("[LocationManager] Handling providerStateChangedEvent OUT_OF_SERVICE");
            setGpsStatus(GpsStatus.NOT_AVAILABLE);
        }
    }

    private void initializeLocationProvider() {
        boolean locationFound = false;
        Date locationProviderInitializationStartTime = new Date();
        while (!locationFound) {

            try {
                log.debug("[LocationManager] Trying to find location");
                setGpsStatus(GpsStatus.LOCATING);

                Criteria cr = new Criteria();
                cr.setHorizontalAccuracy(config.getGpsHorizontalAccuracyForLocationProvider());
                locationProvider = LocationProvider.getInstance(cr);
                if (locationProvider == null) {
                    log.debug("[LocationManager] Location provider not found.");
                    setGpsStatus(GpsStatus.NOT_AVAILABLE);
                    break;
                }

                log.debug("[LocationManager] LocationProvider found. " + locationProvider.getClass());
                log.debug("[LocationManager] Config: " + config.toString());
                locationProvider.setLocationListener(LocationManager.this, config.getGpsLocationInterval(), config.getGpsLocationInterval(), config.getGpsLocationInterval());
                log.debug("[LocationManager] Location listener set");
                Location location = locationProvider.getLocation(config.getGpsLocationFindTimeout());
                Date locationManagerInitializationEndTime = new Date();
                long locationManagerInitializationDuration = (locationManagerInitializationEndTime.getTime() - locationProviderInitializationStartTime.getTime()) / 1000;
                log.debug("[LocationManager] Location found (" + locationManagerInitializationDuration + " seconds)");

                setGpsStatus(GpsStatus.OK);
                locationUpdated(locationProvider, location);
                locationFound = true;
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

    private void setGpsStatus(GpsStatus gpsStatus) {
        this.gpsStatus = gpsStatus;
        fireStatusChanged(gpsStatus);
    }

    private void fireLocationChanged(QualifiedCoordinates coordinates) {
        for (int i = 0; i < locationListeners.size(); i++) {
            LocationManagerGpsListener listener = (LocationManagerGpsListener) locationListeners.elementAt(i);
            listener.locationUpdated(coordinates);
        }
    }

    private void fireStatusChanged(GpsStatus gpsStatus) {
        for (int i = 0; i < statusListeners.size(); i++) {
            LocationManagerStatusListener listener = (LocationManagerStatusListener) statusListeners.elementAt(i);
            listener.stateChanged(gpsStatus);
        }
    }

    private void checkIfGpsSignalIsLost() {
        long now = new Date().getTime();
        long lastGpsUpdateInterval = now - lastGpsUpdate;

        log.debug("[LocationManager] Last GPS update interval: " + lastGpsUpdateInterval + ". Acceptable interval: " + config.getGpsLocationSingalLossTimeout() * 1000);

        boolean tooLate = lastGpsUpdateInterval > config.getGpsLocationSingalLossTimeout() * 1000;
        boolean statusIsAppropriateToReportLossOfSignal = gpsStatus.equals(GpsStatus.OK) || gpsStatus.equals(GpsStatus.INVALID_READING);

        if (tooLate && statusIsAppropriateToReportLossOfSignal) {
            log.debug("[LocationManager] Switching to Signal Lost");
            setGpsStatus(GpsStatus.SIGNAL_LOST);
        }
    }
}