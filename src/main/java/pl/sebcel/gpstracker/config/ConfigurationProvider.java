package pl.sebcel.gpstracker.config;

import pl.sebcel.gpstracker.utils.Logger;

public class ConfigurationProvider {

    private final Logger log = Logger.getLogger();

    public Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.setLocationInterval(5);
        config.setSaveInterval(20);
        config.setGpsHorizontalAccuracy(500);
        config.setGpsLocationFindTimeout(600);
        config.setGpsLocationFindRetryDelay(10);

        log.debug("[ConfigurationProvider] Current configuration: ");
        log.debug(config.toString());

        return config;
    }
}