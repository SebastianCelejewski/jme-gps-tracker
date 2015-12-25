package pl.sebcel.gpstracker.config;

import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.location.LocationManagerConfiguration;

public class ConfigurationProvider {

    private final Logger log = Logger.getLogger();

    public LocationManagerConfiguration getConfiguration() {
        LocationManagerConfiguration config = new LocationManagerConfiguration();
        config.setGpsLocationInterval(5);
        config.setGpsHorizontalAccuracyForLocationProvider(500);
        config.setGpsHorizontalAccuracyForTrackPointsFiltering(60);
        config.setGpsLocationFindTimeout(600);
        config.setGpsLocationFindRetryDelay(10);
        config.setGpsLocationSingalLossTimeout(15);

        log.debug("[ConfigurationProvider] Current configuration: ");
        log.debug(config.toString());

        return config;
    }

    public GpsTrackerConfiguration getGpsTrackerConfiguration() {
        GpsTrackerConfiguration config = new GpsTrackerConfiguration();
        config.setSaveInterval(20);
        return config;
    }
}