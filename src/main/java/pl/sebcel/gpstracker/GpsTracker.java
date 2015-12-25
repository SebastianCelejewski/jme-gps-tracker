package pl.sebcel.gpstracker;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pl.sebcel.gpstracker.config.ConfigurationProvider;
import pl.sebcel.gpstracker.config.GpsTrackerConfiguration;
import pl.sebcel.gpstracker.gpx.CustomGpxSerializer;
import pl.sebcel.gpstracker.gpx.GpxSerializer;
import pl.sebcel.gpstracker.gui.AppModel;
import pl.sebcel.gpstracker.gui.AppView;
import pl.sebcel.gpstracker.repository.TrackRepository;
import pl.sebcel.gpstracker.state.AppState;
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

    private final Logger log = Logger.getLogger();

    private Display display;
    private AppView view;
    private AppEngine engine;
    private AppModel model;
    private LocationManager locationManager;

    public GpsTracker() {
        log.debug("[GpsTracker] initialization");
        this.display = Display.getDisplay(this);

        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        LocationManagerConfiguration locationManagerConfig = configurationProvider.getConfiguration();
        GpsTrackerConfiguration gpsTrackerConfig = configurationProvider.getGpsTrackerConfiguration();
        AppState state = new AppState(WorkflowStatus.UNINITIALIZED, GpsStatus.UNINITIALIZED);
        GpxSerializer gpxSerializer = new CustomGpxSerializer();
        FileUtils fileUtils = new FileUtils();
        TrackRepository trackRepository = new TrackRepository(gpxSerializer, fileUtils);
        model = new AppModel(state);
        view = new AppView(model);
        engine = new AppEngine(state, gpsTrackerConfig, display, trackRepository);

        state.addListener(view);
        view.addListener(engine);

        locationManager = new LocationManager(locationManagerConfig);
        locationManager.addLocationListener(engine);
        locationManager.addStatusListener(engine);
    }

    protected void startApp() throws MIDletStateChangeException {
        log.debug("[GpsTracker] startApp");
        this.display.setCurrent(view);
        engine.init();
        locationManager.initialize();
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        log.debug("[GpsTracker] destroyApp");
    }

    protected void pauseApp() {
        log.debug("[GpsTracker] pauseApp");
    }
}