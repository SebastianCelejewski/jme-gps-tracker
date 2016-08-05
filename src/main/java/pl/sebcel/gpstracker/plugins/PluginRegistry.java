package pl.sebcel.gpstracker.plugins;

import java.util.Vector;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.utils.Logger;

public class PluginRegistry {

    private final Logger log = Logger.getLogger();

    private Vector trackListeners = new Vector();
    private Vector configListeners = new Vector();

    public void addTrackListener(TrackListener trackListener) {
        log.debug("[PluginRegistry] Adding track listener: " + trackListener);
        trackListeners.addElement(trackListener);
    }

    public void addConfigListener(ConfigListener configListener) {
        log.debug("[PluginRegistry] Adding config listener: " + configListener);
        configListeners.addElement(configListener);
    }

    public void fireTrackCreated(Track track) {
        log.debug("[PluginRegistry] Firing event: onTrackCreated");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((TrackListener) trackListeners.elementAt(i)).onTrackCreated(track);
        }
    }

    public void fireTrackUpdated(Track track, Vector trackPoints) {
        log.debug("[PluginRegistry] Firing event: onTrackUpdated");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((TrackListener) trackListeners.elementAt(i)).onTrackUpdated(track, trackPoints);
        }
    }

    public void fireTrackCompleted(Track track) {
        log.debug("[PluginRegistry] Firing event: onTrackCompleted");
        for (int i = 0; i < trackListeners.size(); i++) {
            ((TrackListener) trackListeners.elementAt(i)).onTrackCompleted(track);
        }
    }

    public PluginConfig[] initializePluginConfigs() {
        log.debug("[PluginRegistry] Initializing plugins configuration");
        PluginConfig[] pluginConfigs = new PluginConfig[configListeners.size()];
        for (int i = 0; i < configListeners.size(); i++) {
            ConfigListener configListener = (ConfigListener) configListeners.elementAt(i);
            PluginConfig pluginConfig = configListener.getPluginConfig();
            log.debug("[PluginRegistry] Plugin name: " + pluginConfig.getPluginName());
            log.debug("[PluginRegistry] This plugin requires " + pluginConfig.getConfigurationKeys().length + " keys");
            pluginConfigs[i] = pluginConfig;
        }
        return pluginConfigs;
    }
}