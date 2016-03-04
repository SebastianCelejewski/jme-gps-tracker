package pl.sebcel.gpstracker.plugins;

import java.util.Vector;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.utils.Logger;

public class PluginRegistry {

    private final Logger log = Logger.getLogger();

    private Vector trackListeners = new Vector();

    public void addPlugin(GpsTrackerPlugin trackListener) {
        log.debug("[PluginRegistry] Adding new plugin: " + trackListener);
        trackListeners.addElement(trackListener);
    }

    public void newTrackCreated(Track track) {
        log.debug("[PluginRegistry] Firing event: onTrackCreated");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((GpsTrackerPlugin) trackListeners.elementAt(i)).onNewTrackCreated(track);
        }
    }

    public void trackUpdated(Track track, Vector trackPoints) {
        log.debug("[PluginRegistry] Firing event: onTrackUpdated");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((GpsTrackerPlugin) trackListeners.elementAt(i)).onTrackUpdated(track, trackPoints);
        }
    }

    public void trackCompleted(Track track) {
        log.debug("[PluginRegistry] Firing event: onTrackCompleted");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((GpsTrackerPlugin) trackListeners.elementAt(i)).onTrackCompleted(track);
        }
    }
}