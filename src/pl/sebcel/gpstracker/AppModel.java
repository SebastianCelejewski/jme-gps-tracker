package pl.sebcel.gpstracker;

public class AppModel {

    private AppState appState;

    public AppModel(AppState initialAppState) {
        this.appState = initialAppState;
    }

    public AppState getAppState() {
        return appState;
    }

    public void setAppState(AppState appState) {
        this.appState = appState;
    }
}