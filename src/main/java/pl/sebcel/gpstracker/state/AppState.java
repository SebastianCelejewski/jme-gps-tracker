package pl.sebcel.gpstracker.state;

import java.util.Vector;

import pl.sebcel.gpstracker.events.AppStateChangeListener;

public class AppState {

    private AppStatus appStatus;
    private GpsStatus gpsStatus;
    private String info;
    private Vector listeners = new Vector();

    public AppState(AppStatus initialAppStatus, GpsStatus initialGpsStatus) {
        this.appStatus = initialAppStatus;
        this.gpsStatus = initialGpsStatus;
        this.info = "";
    }

    public void addListener(AppStateChangeListener listener) {
        listeners.addElement(listener);
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public GpsStatus getGpsStatus() {
        return gpsStatus;
    }

    public String getInfo() {
        return info;
    }

    public void setAppStatus(AppStatus appStatus) {
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
        System.out.println(">>>> Notifying listener");
        for (int i = 0; i < listeners.size(); i++) {
            AppStateChangeListener listener = (AppStateChangeListener) listeners.elementAt(i);
            listener.appStateChanged();
        }
    }
}