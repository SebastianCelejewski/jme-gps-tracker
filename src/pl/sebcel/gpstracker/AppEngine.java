package pl.sebcel.gpstracker;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.repository.TrackRepository;

public class AppEngine implements UserActionListener, LocationListener {

    private AppState appState;
    private Track currentTrack;
    private TrackRepository trackRepository;
    private Thread autosaveThread;

    public AppEngine(AppState appState, TrackRepository trackRepository) {
        this.appState = appState;
        this.trackRepository = trackRepository;
    }

    public void init() {
        appState.setAppStatus(AppStatus.READY);
    }

    private void start() {
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
                        trackRepository.saveTrack(currentTrack);
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        System.err.println("Failed to save track: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        autosaveThread.start();
    }

    private void stop() {
        trackRepository.saveTrack(currentTrack);
        currentTrack = null;
        appState.setAppStatus(AppStatus.STOPPED);
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
            double latitude = location.getQualifiedCoordinates().getLatitude();
            double longitude = location.getQualifiedCoordinates().getLongitude();
            Date dateTime = new Date();
            TrackPoint point = new TrackPoint(dateTime, latitude, longitude);
            currentTrack.addPoint(point);
            appState.setInfo("" + currentTrack.getPoints().size());
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        // TODO Auto-generated method stub

    }
}