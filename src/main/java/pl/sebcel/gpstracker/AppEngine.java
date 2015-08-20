package pl.sebcel.gpstracker;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.repository.TrackRepository;
import pl.sebcel.gpstracker.utils.Logger;

public class AppEngine implements UserActionListener, LocationListener {

    private AppState appState;
    private Track currentTrack;
    private TrackRepository trackRepository;
    private Thread autosaveThread;

    private static final Logger log = Logger.getLogger();

    public AppEngine(AppState appState, TrackRepository trackRepository) {
        this.appState = appState;
        this.trackRepository = trackRepository;
    }

    public void init() {
        log.debug("[AppEngine] initialization");
        appState.setAppStatus(AppStatus.READY);
    }

    private void start() {
        log.debug("[AppEngine] starting");
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        int minute = currentDate.get(Calendar.MINUTE);
        int second = currentDate.get(Calendar.SECOND);
        String id = year + "-" + month + "-" + day + "_" + hour + ":" + minute + ":" + second;
        currentTrack = new Track(id, new Date());
        System.out.println("Started recording track " + currentTrack.getId());
        appState.setAppStatus(AppStatus.STARTED);

        autosaveThread = new Thread(new Runnable() {

            public void run() {
                while (appState.getAppStatus().equals(AppStatus.STARTED) && currentTrack != null) {
                    try {
                        appState.setInfo("Saving");
                        trackRepository.saveTrack(currentTrack);
                        appState.setInfo("Saved");
                        Thread.sleep(10000);
                    } catch (Exception ex) {
                        System.err.println("Failed to save track: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        autosaveThread.start();

        new Thread(new Runnable() {

            public void run() {
                try {
                    while (true) {
                        Thread.sleep(500);
                        Location location = new Location() {

                            public QualifiedCoordinates getQualifiedCoordinates() {
                                QualifiedCoordinates fakeCoordinates = new QualifiedCoordinates(1, 2, 3, 4, 5);
                                return fakeCoordinates;
                            }
                        };
                        locationUpdated(null, location);
                    }
                } catch (Exception ex) {

                }
            }
        });// .start();

        log.debug("[AppEngine] started");
    }

    private void stop() {
        log.debug("[AppEngine] stopping");
        trackRepository.saveTrack(currentTrack);
        currentTrack = null;
        appState.setAppStatus(AppStatus.STOPPED);
        log.debug("[AppEngine] stopped");
    }

    private void recordNew() {
        appState.setAppStatus(AppStatus.READY);
    }

    public void userSwitchedTo(StatusTransition statusTransition) {
        System.out.println("Switching to " + statusTransition.getTargetStatus().getDisplayName() + " upon " + statusTransition.getName());
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
            QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
            double latitude = coordinates.getLatitude();
            double longitude = coordinates.getLongitude();

            log.debug("[AppEngine] Location: " + latitude + ";" + longitude + ";" + coordinates.getAltitude() + ";" + coordinates.getHorizontalAccuracy() + ";" + coordinates.getVerticalAccuracy());

            Date dateTime = new Date();
            TrackPoint point = new TrackPoint(dateTime, latitude, longitude);
            currentTrack.addPoint(point);
            appState.setInfo("" + currentTrack.getPoints().size());
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        log.debug("[AppEngine] location provider state changed to " + newState);
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