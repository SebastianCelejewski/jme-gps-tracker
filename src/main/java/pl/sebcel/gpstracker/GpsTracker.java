package pl.sebcel.gpstracker;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pl.sebcel.gpstracker.export.gpx.CustomGpxSerializer;
import pl.sebcel.gpstracker.export.gpx.GpxSerializer;
import pl.sebcel.gpstracker.gui.AppModel;
import pl.sebcel.gpstracker.gui.ApplicationView;
import pl.sebcel.gpstracker.gui.ConfigurationView;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.dtafile.DtaFileExporter;
import pl.sebcel.gpstracker.plugins.endomondo.EndomondoConnector;
import pl.sebcel.gpstracker.plugins.gpxfile.GpxFileExporter;
import pl.sebcel.gpstracker.storage.Storage;
import pl.sebcel.gpstracker.utils.FileUtils;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.WorkflowStatus;
import pl.sebcel.location.GpsStatus;
import pl.sebcel.location.LocationManager;
import pl.sebcel.location.LocationManagerConfiguration;

/**
 * Application entry point
 * 
 * <p>
 * Initializes all components and handles MIDlet lifecycle.
 * </p>
 * 
 * @author Sebastian Celejewski
 */
public class GpsTracker extends MIDlet {

    public final static String version = "0.4";

    private final Logger log = Logger.getLogger();

    private Display display;
    private ApplicationView applicationView;
    private ConfigurationView configurationView;
    private AppEngine engine;
    private AppModel model;
    private LocationManager locationManager;

    public GpsTracker() {
        log.setInfoEnabled(true);
        log.setDebugEnabled(false);
        
        log.debug("[GpsTracker] Midlet initialization started");
        this.display = Display.getDisplay(this);

        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        LocationManagerConfiguration locationManagerConfig = configurationProvider.getConfiguration();
        GpsTrackerConfiguration gpsTrackerConfig = configurationProvider.getGpsTrackerConfiguration();
        AppState state = new AppState(WorkflowStatus.UNINITIALIZED, GpsStatus.UNINITIALIZED);
        GpxSerializer gpxSerializer = new CustomGpxSerializer();
        Storage storage = new Storage();
        FileUtils fileUtils = new FileUtils();

        PluginRegistry pluginRegistry = new PluginRegistry();
        DtaFileExporter dtaFileExporter = new DtaFileExporter(fileUtils);
        GpxFileExporter gpxFileExporter = new GpxFileExporter(fileUtils, gpxSerializer);
        EndomondoConnector endomondoConnector = new EndomondoConnector();
        
        endomondoConnector.setConfigurationProvider(configurationProvider);

        dtaFileExporter.register(pluginRegistry);
        gpxFileExporter.register(pluginRegistry);
        endomondoConnector.register(pluginRegistry);

        model = new AppModel(state);
        applicationView = new ApplicationView(display, model);
        configurationView = new ConfigurationView(display);

        engine = new AppEngine(state, gpsTrackerConfig, display, pluginRegistry);

        state.addListener(applicationView);
        applicationView.addUserActionListener(engine);
        applicationView.setConfigurationView(configurationView);

        configurationView.setApplicationView(applicationView);
        configurationView.setConfigurationProvider(configurationProvider);

        configurationProvider.setConfigurationView(configurationView);
        configurationProvider.setPluginRegistry(pluginRegistry);
        configurationProvider.setStorage(storage);
        configurationProvider.init();

        locationManager = new LocationManager(locationManagerConfig);
        locationManager.addLocationListener(engine);
        locationManager.addStatusListener(engine);

        log.debug("[GpsTracker] Midlet initialization complete");
    }

    protected void startApp() throws MIDletStateChangeException {
        log.debug("[GpsTracker] Midlet event: startApp");
        this.display.setCurrent(applicationView);
        engine.init();
        locationManager.initialize();
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        log.debug("[GpsTracker] Midlet event: destroyApp (unconditional=" + unconditional + ")");
    }

    protected void pauseApp() {
        log.debug("[GpsTracker] Midlet event: pauseApp");
    }
}