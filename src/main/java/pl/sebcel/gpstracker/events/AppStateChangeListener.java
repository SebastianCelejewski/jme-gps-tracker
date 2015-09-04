package pl.sebcel.gpstracker.events;

import pl.sebcel.gpstracker.state.AppState;

public interface AppStateChangeListener {
    public void appStateChanged(AppState appState);
}
