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

import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.repository.TrackRepository;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;

public class AppEngine implements UserActionListener, LocationListener {

    private AppState appState;
    private Track currentTrack;
    private TrackRepository trackRepository;
    private Thread autosaveThread;
    private Display display;
    private static long idGenerator = 0;
    private long id = 0;
    private Vector currentTrackPoints;

    private static final Logger log = Logger.getLogger();

    public AppEngine(AppState appState, Display display, TrackRepository trackRepository) {
        this.appState = appState;
        this.trackRepository = trackRepository;
        this.display = display;
        id = idGenerator++;
    }

    public void init() {
        log.debug("[AppEngine " + id + "] Initialization");
        appState.setAppStatus(AppStatus.READY);

        autosaveThread = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    if (appState.getAppStatus().equals(AppStatus.STARTED) && currentTrack != null) {
                        log.debug("[AppEngine " + id + "] Triggering track auto saving");
                        save();
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (Exception ex) {
                    }
                }
            }
        });

        log.debug("[AppEngine " + id + "] Starting autosave thread");
        autosaveThread.start();
    }

    private void start() {
        log.debug("[AppEngine " + id + "] Starting recording of a new track");
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        String trackId = DateFormat.format(currentDate.getTime());
        currentTrackPoints = new Vector();
        currentTrack = new Track(trackId, new Date());
        trackRepository.createNewTrack(currentTrack);

        System.out.println("Started recording track " + currentTrack.getId());
        appState.setAppStatus(AppStatus.STARTED);
        log.debug("[AppEngine " + id + "] Track recording started");
    }

    private void stop() {
        log.debug("[AppEngine " + id + "] Stopping track recording");
        appState.setAppStatus(AppStatus.STOPPING);

        save();

        appState.setInfo("Exporting track to GPX");
        trackRepository.saveTrack(currentTrack);
        currentTrack = null;
        appState.setAppStatus(AppStatus.STOPPED);
        appState.setInfo("Track exported to GPX");
        log.debug("[AppEngine " + id + "] Track recording stopped");
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

    private void recordNew() {
        appState.setAppStatus(AppStatus.READY);
    }

    public void userSwitchedTo(StatusTransition statusTransition) {
        System.out.println("Switching to " + statusTransition.getTargetStatus().getDisplayName() + " upon " + statusTransition.getName());
        log.debug("[AppEngine " + id + "] User requested status transition " + statusTransition.getName());
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
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        if (currentTrack != null) {
            try {
                AlertType.INFO.playSound(display);
                QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
                double latitude = coordinates.getLatitude();
                double longitude = coordinates.getLongitude();
                double altitude = coordinates.getAltitude();
                double horizontalAccuracy = coordinates.getHorizontalAccuracy();
                double verticalAccuracy = coordinates.getVerticalAccuracy();

                log.debug("[AppEngine " + id + "] Location information arrived: " + latitude + ";" + longitude + ";" + altitude + ";" + horizontalAccuracy + ";" + verticalAccuracy);

                Date dateTime = new Date();
                TrackPoint point = new TrackPoint(dateTime, latitude, longitude, altitude, horizontalAccuracy, verticalAccuracy);
                synchronized (currentTrack) {
                    currentTrack.addPoint(point);
                    currentTrackPoints.addElement(point);
                }
                appState.setInfo("" + currentTrack.getPoints().size());
            } catch (Exception ex) {
                log.debug("[AppEngine " + id + "] Failed to handle location update: " + ex.getMessage());
            }
        } else {
            log.debug("[AppEngine " + id + "] Location information arrived but is ignored, because currentTrack is null. Current status: " + appState.getAppStatus().getDisplayName());
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        log.debug("[AppEngine " + id + "] location provider state changed to " + newState);
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
}