package pl.sebcel.gpstracker;

import java.util.Vector;

import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.workflow.WorkflowStatus;
import pl.sebcel.location.GpsStatus;

public class AppState {

    private WorkflowStatus appStatus;
    private GpsStatus gpsStatus;
    private String infoLine1;
    private String infoLine2;
    private Vector listeners = new Vector();

    public AppState(WorkflowStatus initialAppStatus, GpsStatus initialGpsStatus) {
        this.appStatus = initialAppStatus;
        this.gpsStatus = initialGpsStatus;
        this.infoLine1 = "";
        this.infoLine2 = "";
    }

    public void addListener(AppStateChangeListener listener) {
        listeners.addElement(listener);
    }

    public WorkflowStatus getAppStatus() {
        return appStatus;
    }

    public GpsStatus getGpsStatus() {
        return gpsStatus;
    }

    public String getInfoLine1() {
        return infoLine1;
    }

    public String getInfoLine2() {
        return infoLine2;
    }

    public void setAppStatus(WorkflowStatus appStatus) {
        this.appStatus = appStatus;
        notifyListeners();
    }

    public void setGpsStatus(GpsStatus gpsStatus) {
        this.gpsStatus = gpsStatus;
        notifyListeners();
    }

    public void setInfo(String infoLine1, String infoLine2) {
        this.infoLine1 = infoLine1;
        this.infoLine2 = infoLine2;
        notifyListeners();
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            AppStateChangeListener listener = (AppStateChangeListener) listeners.elementAt(i);
            listener.appStateChanged();
        }
    }
}