package pl.sebcel.gpstracker;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pl.sebcel.gpstracker.export.gpx.CustomGpxSerializer;
import pl.sebcel.gpstracker.export.gpx.GpxSerializer;
import pl.sebcel.gpstracker.gui.AppModel;
import pl.sebcel.gpstracker.gui.AppView;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.dtafile.DtaFileExporter;
import pl.sebcel.gpstracker.plugins.endomondo.EndomondoConnector;
import pl.sebcel.gpstracker.plugins.gpxfile.GpxFileExporter;
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
    private AppView view;
    private AppEngine engine;
    private AppModel model;
    private LocationManager locationManager;

    public GpsTracker() {
        log.debug("[GpsTracker] Midlet initialization started");
        this.display = Display.getDisplay(this);

        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        LocationManagerConfiguration locationManagerConfig = configurationProvider.getConfiguration();
        GpsTrackerConfiguration gpsTrackerConfig = configurationProvider.getGpsTrackerConfiguration();
        AppState state = new AppState(WorkflowStatus.UNINITIALIZED, GpsStatus.UNINITIALIZED);
        GpxSerializer gpxSerializer = new CustomGpxSerializer();
        FileUtils fileUtils = new FileUtils();
        
        PluginRegistry pluginRegistry = new PluginRegistry();
        DtaFileExporter dtaFileExporter = new DtaFileExporter(fileUtils);
        GpxFileExporter gpxFileExporter = new GpxFileExporter(fileUtils, gpxSerializer);
        EndomondoConnector endomondoConnector = new EndomondoConnector();
        
        dtaFileExporter.register(pluginRegistry);
        gpxFileExporter.register(pluginRegistry);
        endomondoConnector.register(pluginRegistry);
        
        model = new AppModel(state);
        view = new AppView(model);
        engine = new AppEngine(state, gpsTrackerConfig, display, pluginRegistry);

        state.addListener(view);
        view.addListener(engine);

        locationManager = new LocationManager(locationManagerConfig);
        locationManager.addLocationListener(engine);
        locationManager.addStatusListener(engine);

        log.debug("[GpsTracker] Midlet initialization complete");
    }

    protected void startApp() throws MIDletStateChangeException {
        log.debug("[GpsTracker] Midlet event: startApp");
        this.display.setCurrent(view);
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