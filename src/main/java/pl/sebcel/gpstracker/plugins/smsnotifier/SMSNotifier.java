package pl.sebcel.gpstracker.plugins.smsnotifier;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.state.AppState;
import pl.sebcel.gpstracker.state.GpsStatus;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.AppWorkflow;
import pl.sebcel.gpstracker.workflow.StatusTransition;

public class SMSNotifier implements AppStateChangeListener, UserActionListener, LocationListener {

    private final Logger log = Logger.getLogger();

    private WaypointManager waypointManager;

    public SMSNotifier(WaypointManager waypointManager) {
        log.debug("[SMS Notifier] Initialization.");
        this.waypointManager = waypointManager;
    }

    public void userSwitchedTo(StatusTransition statusTransition) {
        if (statusTransition.equals(AppWorkflow.START)) {
            log.debug("[SMS Notifier] Starting recording new track.");
        }
        if (statusTransition.equals(AppWorkflow.STOP)) {
            log.debug("[SMS Notifier] Stopped recording track.");
        }

    }

    public void appStateChanged(AppState appState) {
        GpsStatus gpsStatus = appState.getGpsStatus();
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
        String message = waypointManager.getWaypointInfo(coordinates);
        if (message != null && message.trim().length() > 0) {
            log.debug("[SMS Notifier] Message: " + message);
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
    }

}
