package pl.sebcel.gpstracker.plugins.smsnotifier;

import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.state.AppState;
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
            waypointManager.restart();
            sendMessage("Start");
        }
        if (statusTransition.equals(AppWorkflow.STOP)) {
            log.debug("[SMS Notifier] Stopped recording track.");
            sendMessage("Stop");
        }
    }

    public void appStateChanged(AppState appState) {
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        if (!location.isValid()) {
            return;
        }
        if (location.getQualifiedCoordinates() == null) {
            return;
        }
        QualifiedCoordinates coordinates = location.getQualifiedCoordinates();
        String message = waypointManager.getWaypointInfo(coordinates);
        if (message != null && message.trim().length() > 0) {
            log.debug("[SMS Notifier] Message: " + message);
            sendMessage(message);
        }
    }

    private void sendMessage(final String message) {
        synchronized (this) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        String url = "http://www.sebastian-celejewski.pl/Klodziomondo/update.php?message=" + message;
                        HttpConnection httpConnection = (HttpConnection) Connector.open(url);
                        httpConnection.setRequestMethod(HttpConnection.GET);
                        httpConnection.setRequestProperty("User-Agent", "Klodziomondo");

                        log.debug("[SMS Notifier] HttpConnection: " + httpConnection);

                        int responseCode = httpConnection.getResponseCode();
                        log.debug("[SMS Notifier] Response code: " + responseCode);

                        if (responseCode == HttpConnection.HTTP_OK) {
                            StringBuffer sb = new StringBuffer();
                            InputStream is = httpConnection.openDataInputStream();
                            int chr;
                            while ((chr = is.read()) != -1) {
                                sb.append((char) chr);
                            }

                            // Web Server just returns the birthday in mm/dd/yy format.
                            log.debug("[SMS Notifier] Response: " + sb.toString());
                        }

                        httpConnection.close();
                    }

                    catch (Exception ex) {
                        log.debug("[SMS Notifier] Error: " + ex);
                    }
                }
            }).start();
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
    }
}