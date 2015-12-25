package pl.sebcel.gpstracker.state;

import java.util.Vector;

import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.workflow.WorkflowStatus;
import pl.sebcel.location.GpsStatus;

public class AppState {

    private WorkflowStatus appStatus;
    private GpsStatus gpsStatus;
    private String info;
    private Vector listeners = new Vector();

    public AppState(WorkflowStatus initialAppStatus, GpsStatus initialGpsStatus) {
        this.appStatus = initialAppStatus;
        this.gpsStatus = initialGpsStatus;
        this.info = "";
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

    public String getInfo() {
        return info;
    }

    public void setAppStatus(WorkflowStatus appStatus) {
        this.appStatus = appStatus;
        notifyListeners();
    }

    public void setGpsStatus(GpsStatus gpsStatus) {
        this.gpsStatus = gpsStatus;
        notifyListeners();
    }

    public void setInfo(String info) {
        this.info = info;
        notifyListeners();
    }

    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            AppStateChangeListener listener = (AppStateChangeListener) listeners.elementAt(i);
            listener.appStateChanged();
        }
    }
}