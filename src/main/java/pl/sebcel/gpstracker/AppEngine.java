package pl.sebcel.gpstracker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.config.Configuration;
import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.plugins.smsnotifier.SMSNotifier;
import pl.sebcel.gpstracker.plugins.smsnotifier.WaypointManager;
import pl.sebcel.gpstracker.repository.TrackRepository;
import pl.sebcel.gpstracker.state.AppState;
import pl.sebcel.gpstracker.state.AppStatus;
import pl.sebcel.gpstracker.state.GpsStatus;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.AppWorkflow;
import pl.sebcel.gpstracker.workflow.StatusTransition;

/**
 * Tracker engine
 * 
 * <p>
 * Handles application lifecycle, controls location updates and autosave thread.
 * </p>
 * 
 * @author Sebastian Celejewski
 */
public class AppEngine implements UserActionListener, LocationListener {

    private final Logger log = Logger.getLogger();

    private AppState appState;
    private Track currentTrack;
    private TrackRepository trackRepository;
    private Thread autosaveThread;
    private Display display;
    private Vector currentTrackPoints;
    private Configuration config;
    private boolean alreadyInitialized = false;
    private Runtime runtime;
    private long lastGpsUpdate = 0;
    
    private SMSNotifier smsNotifier;

    public AppEngine(AppState appState, Configuration config, Display display, TrackRepository trackRepository) {
        this.appState = appState;
        this.trackRepository = trackRepository;
        this.display = display;
        this.config = config;
        this.runtime = Runtime.getRuntime();
        
        this.smsNotifier = new SMSNotifier(new WaypointManager());
    }

    public void init() {
        log.debug("[AppEngine] Initialization");
        if (alreadyInitialized) {
            log.debug("[AppEngine] Already initialized");
            return;
        }
        alreadyInitialized = true;

        appState.setAppStatus(AppStatus.READY);

        autosaveThread = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    checkIfGpsSignalIsLost();
                    if (appState.getAppStatus().equals(AppStatus.STARTED) && currentTrack != null) {
                        log.debug("[AppEngine] Triggering track auto saving");
                        save();
                        log.debug("[AppEngine] Memory: " + getMemoryUtilization());
                    }
                    try {
                        Thread.sleep(config.getSaveInterval() * 1000);
                    } catch (Exception ex) {
                        // intentional
                    }
                }
            }
        });

        log.debug("[AppEngine] Starting autosave thread");
        autosaveThread.start();
    }

    private void start() {
        log.debug("[AppEngine] Starting recording of a new track");
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        String trackId = DateFormat.format(currentDate.getTime());
        currentTrackPoints = new Vector();
        currentTrack = new Track(trackId, new Date());
        trackRepository.createNewTrack(currentTrack);

        System.out.println("Started recording track " + currentTrack.getId());
        appState.setAppStatus(AppStatus.STARTED);
        log.debug("[AppEngine] Track recording started");
    }

    private void stop() {
        log.debug("[AppEngine] Stopping track recording");
        appState.setAppStatus(AppStatus.STOPPING);

        save();

        appState.setInfo("Exporting track to GPX");
        trackRepository.saveTrack(currentTrack);
        currentTrack = null;
        appState.setAppStatus(AppStatus.STOPPED);
        appState.setInfo("Track exported to GPX");
        log.debug("[AppEngine] Track recording stopped");
    }

    private void save() {
        appState.setInfo("Saving track");
        AlertType.WARNING.playSound(display);
        synchronized (currentTrack) {
            trackRepository.appendTrackPoints(currentTrack, currentTrackPoints);
            currentTrackPoints = new Vector();
        }
        AlertType.WARNING.playSound(display);
        AlertType.WARNING.playSound(display);
        appState.setInfo("Track saved");
    }

    private void pause() {
        log.debug("[AppEngine] Pausing");
        appState.setAppStatus(AppStatus.PAUSED);
        appState.setInfo("Paused");
        log.debug("[AppEngine] Paused");
    }

    private void resume() {
        log.debug("[AppEngine] Resuming");
        appState.setAppStatus(AppStatus.STARTED);
        appState.setInfo("Resumed");
        log.debug("[AppEngine] Resumed");
    }

    private void recordNew() {
        appState.setAppStatus(AppStatus.READY);
    }

    private void checkIfGpsSignalIsLost() {
        long now = new Date().getTime();
        long lastGpsUpdateInterval = now - lastGpsUpdate;

        log.debug("[AppEngine] Last GPS update interval: " + lastGpsUpdateInterval + ". Acceptable interval: " + config.getGpsLocationSingalLossTimeout() * 1000);

        if (lastGpsUpdateInterval > config.getGpsLocationSingalLossTimeout() * 1000 && appState.getGpsStatus().equals(GpsStatus.OK)) {
            log.debug("[AppEngine] Switching to Signal Lost");
            appState.setGpsStatus(GpsStatus.SIGNAL_LOST);
        }
    }

    public void userSwitchedTo(StatusTransition statusTransition) {
        smsNotifier.userSwitchedTo(statusTransition);
        
        System.out.println("Switching to " + statusTransition.getTargetStatus().getDisplayName() + " upon " + statusTransition.getName());
        log.debug("[AppEngine] User requested status transition " + statusTransition.getName());
        AlertType.CONFIRMATION.playSound(display);
        if (statusTransition.equals(AppWorkflow.START)) {
            start();
        }
        if (statusTransition.equals(AppWorkflow.STOP)) {
            stop();
        }
        if (statusTransition.equals(AppWorkflow.NEW)) {
            recordNew();
        }
        if (statusTransition.equals(AppWorkflow.PAUSE)) {
            pause();
        }
        if (statusTransition.equals(AppWorkflow.RESUME)) {
            resume();
        }
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        if (location == null) {
            log.debug("[AppEngine] LocationUpdated has been called, but location is null.");
            return;
        }

        if (location.getQualifiedCoordinates() == null) {
            log.debug("[AppEngine] LocationUpdated has been called, but qualified coordinates are null.");
            return;
        }

        QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();
        double altitude = coordinates.getAltitude();
        double horizontalAccuracy = coordinates.getHorizontalAccuracy();
        double verticalAccuracy = coordinates.getVerticalAccuracy();

        log.debug("[AppEngine] Location information arrived: " + latitude + ";" + longitude + ";" + altitude + ";" + horizontalAccuracy + ";" + verticalAccuracy);

        if (horizontalAccuracy > config.getGpsHorizontalAccuracyForTrackPointsFiltering()) {
            log.debug("[AppEngine] Location will be ignored, because horizontal accuracy (" + horizontalAccuracy + " m) is too weak. Expected accuracy must be lower than " + config.getGpsHorizontalAccuracyForTrackPointsFiltering() + " m.");
            appState.setGpsStatus(GpsStatus.INVALID_READING);
            return;
        }

        if (appState.getGpsStatus().equals(GpsStatus.SIGNAL_LOST)) {
            log.debug("[AppEngine] GPS signal found again.");
        }

        appState.setGpsStatus(GpsStatus.OK);
        lastGpsUpdate = new Date().getTime();
                
                smsNotifier.locationUpdated(provider, location);
                

        if (currentTrack == null || appState.getAppStatus().equals(AppStatus.STARTED)) {
            log.debug("[AppEngine] Location information arrived but is ignored. CurrentTrack: " + currentTrack + ", current status: " + appState.getAppStatus().getDisplayName());
        }

        try {
            AlertType.INFO.playSound(display);

            Date dateTime = new Date();
            TrackPoint point = new TrackPoint(dateTime, latitude, longitude, altitude, horizontalAccuracy, verticalAccuracy);
            synchronized (currentTrack) {
                currentTrack.addPoint(point);
                currentTrackPoints.addElement(point);
            }
            appState.setInfo("" + currentTrack.getPoints().size());
        } catch (Exception ex) {
            log.debug("[AppEngine] Failed to handle location update: " + ex.getMessage());
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        log.debug("[AppEngine] Location provider state changed to " + newState);
        if (newState == LocationProvider.AVAILABLE) {
            appState.setGpsStatus(GpsStatus.OK);
        }
        if (newState == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            appState.setGpsStatus(GpsStatus.LOCATING);
        }
        if (newState == LocationProvider.OUT_OF_SERVICE) {
            appState.setGpsStatus(GpsStatus.NOT_AVAILABLE);
        }
    }

    private String getMemoryUtilization() {
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUtilization = (int) (100 * (double) usedMemory / (double) totalMemory);
        return "Total: " + totalMemory + ", free: " + freeMemory + ", used: " + usedMemory + " (" + memoryUtilization + "%)";
    }
}