package pl.sebcel.gpstracker;

import java.util.Calendar;
import java.util.Date;
import java.util.Stack;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.WorkflowStatus;
import pl.sebcel.gpstracker.workflow.WorkflowTransition;
import pl.sebcel.location.GpsStatus;
import pl.sebcel.location.LocationManagerGpsListener;
import pl.sebcel.location.LocationManagerStatusListener;

/**
 * Tracker engine
 * 
 * <p>
 * Handles application lifecycle, controls location updates and autosave thread.
 * </p>
 * 
 * @author Sebastian Celejewski
 */
public class AppEngine implements UserActionListener, LocationManagerGpsListener, LocationManagerStatusListener {

    private final Logger log = Logger.getLogger();

    private AppState appState;
    private Track currentTrack;
    private PluginRegistry pluginRegistry;
    private Display display;
    private Vector currentTrackPoints;
    private GpsTrackerConfiguration gpsTrackerConfig;
    private boolean alreadyInitialized = false;

    private Thread autosaveThread;
    private Thread commandThread;
    private Stack commands = new Stack();

    public AppEngine(AppState appState, GpsTrackerConfiguration gpsTrackerConfig, Display display, PluginRegistry pluginRegistry) {
        this.appState = appState;
        this.pluginRegistry = pluginRegistry;
        this.display = display;
        this.gpsTrackerConfig = gpsTrackerConfig;
    }

    public void init() {
        log.debug("[AppEngine] Initialization");
        if (alreadyInitialized) {
            log.debug("[AppEngine] Already initialized");
            return;
        }
        alreadyInitialized = true;

        appState.setAppStatus(WorkflowStatus.READY);

        autosaveThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (appState.getAppStatus().equals(WorkflowStatus.STARTED) && currentTrack != null) {
                        log.debug("[AppEngine] Triggering track auto saving");
                        save();
                        log.debug("[AppEngine] Memory: " + getMemoryUtilization());
                    }
                    try {
                        Thread.sleep(gpsTrackerConfig.getSaveInterval() * 1000);
                    } catch (Exception ex) {
                        // intentional
                    }
                }
            }
        });

        log.debug("[AppEngine] Starting autosave thread");
        autosaveThread.start();

        // All user commands are handled in a separate thread, so that the user command handler
        // in the main application thread completed almost immediately allowing GUI thread to refresh the screen
        // before the actual command is executed.
        commandThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    synchronized (appState) {
                        if (!commands.empty()) {
                            Runnable command = (Runnable) commands.pop();
                            command.run();
                        }

                        try {
                            appState.wait();
                        } catch (Exception ex) {
                            // intentional
                        }
                    }
                }
            }
        });

        log.debug("[AppEngine] Starting commands thread");
        commandThread.start();

        log.debug("[AppEngine] Initialization complete");
    }

    private void start() {
        log.debug("[AppEngine] Start - started");
        appState.setAppStatus(WorkflowStatus.STARTING);

        Runnable command = new Runnable() {
            public void run() {
                log.debug("[AppEngine] Start - command thread started");
                Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                String trackId = DateFormat.format(currentDate.getTime());
                currentTrackPoints = new Vector();
                currentTrack = new Track(trackId, new Date());
                pluginRegistry.fireTrackCreated(currentTrack);

                appState.setAppStatus(WorkflowStatus.STARTED);
                log.debug("[AppEngine] Start - command thread completed");
            }
        };

        commands.push(command);
    }

    private void stop() {
        log.debug("[AppEngine] Stop - started");
        appState.setAppStatus(WorkflowStatus.STOPPING);

        Runnable command = new Runnable() {
            public void run() {
                log.debug("[AppEngine] Stop - command thread started");
                save();

                pluginRegistry.fireTrackCompleted(currentTrack);
                currentTrack = null;
                appState.setAppStatus(WorkflowStatus.STOPPED);
                log.debug("[AppEngine] Track recording stopped");
                log.debug("[AppEngine] Stop - command thread completed");
            }
        };

        commands.push(command);
    }

    private void save() {
        log.debug("[AppEngine] Save - started");
        AlertType.WARNING.playSound(display);
        synchronized (currentTrack) {
            pluginRegistry.fireTrackUpdated(currentTrack, currentTrackPoints);
            currentTrackPoints = new Vector();
        }
        AlertType.WARNING.playSound(display);
        AlertType.WARNING.playSound(display);
        log.debug("[AppEngine] Save - completed");
    }

    private void pause() {
        log.debug("[AppEngine] Pausing");
        appState.setAppStatus(WorkflowStatus.PAUSED);
        log.debug("[AppEngine] Paused");
    }

    private void resume() {
        log.debug("[AppEngine] Resuming");
        appState.setAppStatus(WorkflowStatus.STARTED);
        log.debug("[AppEngine] Resumed");
    }

    private void recordNew() {
        appState.setAppStatus(WorkflowStatus.READY);
    }

    public void userSwitchedTo(WorkflowTransition statusTransition) {
        log.debug("[AppEngine] User requested status transition " + statusTransition.getName());
        AlertType.CONFIRMATION.playSound(display);
        if (statusTransition.equals(WorkflowTransition.START)) {
            start();
        }
        if (statusTransition.equals(WorkflowTransition.STOP)) {
            stop();
        }
        if (statusTransition.equals(WorkflowTransition.NEW)) {
            recordNew();
        }
        if (statusTransition.equals(WorkflowTransition.PAUSE)) {
            pause();
        }
        if (statusTransition.equals(WorkflowTransition.RESUME)) {
            resume();
        }
    }

    private String getMemoryUtilization() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUtilization = (int) (100 * (double) usedMemory / (double) totalMemory);
        return "Total: " + totalMemory + ", free: " + freeMemory + ", used: " + usedMemory + " (" + memoryUtilization + "%)";
    }

    public void locationUpdated(QualifiedCoordinates currentCoordinates, QualifiedCoordinates previousCoordinates) {
        double latitude = currentCoordinates.getLatitude();
        double longitude = currentCoordinates.getLongitude();
        double altitude = currentCoordinates.getAltitude();
        double horizontalAccuracy = currentCoordinates.getHorizontalAccuracy();
        double verticalAccuracy = currentCoordinates.getVerticalAccuracy();

        if (currentTrack == null || !appState.getAppStatus().equals(WorkflowStatus.STARTED)) {
            log.debug("[AppEngine] Location information arrived but is ignored, because application is not in status STARTED. CurrentTrack: " + currentTrack + ", current status: " + appState.getAppStatus().getDisplayName());
            return;
        }

        try {
            AlertType.INFO.playSound(display);

            double distanceDelta = currentCoordinates.distance(previousCoordinates) / 1000;
            currentTrack.addDistance(distanceDelta);
            log.debug("[AppEngine] Distance delta: " + distanceDelta + " km, total distance: " + currentTrack.getDistance());

            Date dateTime = new Date();
            TrackPoint point = new TrackPoint(dateTime, latitude, longitude, altitude, currentTrack.getDistance(), horizontalAccuracy, verticalAccuracy);
            synchronized (currentTrack) {
                currentTrack.addPoint(point);
                currentTrackPoints.addElement(point);
            }

            long duration = dateTime.getTime() - currentTrack.getStartDate().getTime();
            long totalDurationInMinutes = duration / 1000 / 60;
            long trackedDurationInMinutes = (dateTime.getTime() - ((TrackPoint) currentTrack.getPoints().elementAt(0)).getDateTime().getTime()) / 1000 / 60;
            long numberOfPoints = currentTrack.getPoints().size();
            double distance = ((double) ((int) (currentTrack.getDistance() * 100))) / 100;

            appState.setInfo(totalDurationInMinutes + " min (" + trackedDurationInMinutes + " min)", numberOfPoints + " pts, " + distance + " km");
        } catch (Exception ex) {
            log.error("[AppEngine] Failed to handle location update: " + ex.getMessage());
        }
    }

    public void stateChanged(GpsStatus gpsStatus) {
        appState.setGpsStatus(gpsStatus);
    }
}