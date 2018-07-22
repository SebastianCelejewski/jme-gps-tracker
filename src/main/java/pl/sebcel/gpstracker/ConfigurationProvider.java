package pl.sebcel.gpstracker;

import pl.sebcel.gpstracker.gui.ConfigurationView;
import pl.sebcel.gpstracker.plugins.PluginConfig;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.storage.Storage;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.location.LocationManagerConfiguration;

public class ConfigurationProvider {

    private final Logger log = Logger.getLogger();
    private ConfigurationView configurationView;
    private Storage storage;
    private PluginRegistry pluginRegistry;
    private PluginConfig[] pluginConfigs;

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setConfigurationView(ConfigurationView configurationView) {
        this.configurationView = configurationView;
    }

    public void setPluginRegistry(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    public void init() {
        log.debug("[ConfigurationProvider] Initialization started");

        log.debug("[ConfigurationProvider] Initializing storage");
        this.storage.load();

        log.debug("[ConfigurationProvider] Initializing plugin configs");
        pluginConfigs = pluginRegistry.initializePluginConfigs();

        log.debug("[ConfigurationProvider] Loading plugin configs");
        for (int i = 0; i < pluginConfigs.length; i++) {
            PluginConfig pluginConfig = pluginConfigs[i];
            String pluginName = pluginConfig.getPluginName();
            String[] configurationKeys = pluginConfig.getConfigurationKeys();
            for (int j = 0; j < configurationKeys.length; j++) {
                String configurationKey = configurationKeys[j];
                String storageKey = pluginName + "." + configurationKey;
                String value = storage.getValue(storageKey);
                pluginConfig.setValue(configurationKey, value);
            }
        }

        log.debug("[ConfigurationProvider] Initializing configuration view");
        configurationView.setPluginConfigs(pluginConfigs);

        log.debug("[ConfigurationProvider] Initialization complete");
    }

    public void updateViewAndStorage() {
        log.debug("[ConfigurationProvider] Updating configuration view");
        configurationView.setPluginConfigs(pluginConfigs);

        log.debug("[ConfigurationProvider] Updating storage");
        updateStorage();
    }

    public LocationManagerConfiguration getConfiguration() {
        LocationManagerConfiguration config = new LocationManagerConfiguration();
        config.setGpsLocationInterval(5);
        config.setGpsHorizontalAccuracyForLocationProvider(500);
        config.setGpsHorizontalAccuracyForTrackPointsFiltering(60);
        config.setGpsLocationFindTimeout(600);
        config.setGpsLocationFindRetryDelay(10);
        config.setGpsLocationSingalLossTimeout(15);

        log.debug("[ConfigurationProvider] Current GPS Tracker configuration: ");
        log.debug(config.toString());

        return config;
    }

    public GpsTrackerConfiguration getGpsTrackerConfiguration() {
        GpsTrackerConfiguration config = new GpsTrackerConfiguration();
        config.setSaveInterval(60);

        log.debug("[ConfigurationProvider] Current Location Manager configuration: ");
        log.debug(config.toString());

        return config;
    }

    public void updateStorage() {
        log.debug("[ConfigurationProvider] Updating plugin configs");
        for (int i = 0; i < pluginConfigs.length; i++) {
            PluginConfig pluginConfig = pluginConfigs[i];
            String pluginName = pluginConfig.getPluginName();
            String[] configurationKeys = pluginConfig.getConfigurationKeys();
            for (int j = 0; j < configurationKeys.length; j++) {
                String configurationKey = configurationKeys[j];
                String value = pluginConfig.getValue(configurationKey);
                String storageKey = pluginName + "." + configurationKey;
                storage.setValue(storageKey, value);
            }
        }

        log.debug("[ConfigurationProvider] Saving plugin configs");
        storage.save();
    }
}