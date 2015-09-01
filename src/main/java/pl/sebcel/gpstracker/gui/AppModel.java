package pl.sebcel.gpstracker.gui;

import pl.sebcel.gpstracker.state.AppState;

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