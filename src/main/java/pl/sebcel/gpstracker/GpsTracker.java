package pl.sebcel.gpstracker;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pl.sebcel.gpstracker.config.Configuration;
import pl.sebcel.gpstracker.config.ConfigurationProvider;
import pl.sebcel.gpstracker.gui.AppModel;
import pl.sebcel.gpstracker.gui.AppView;
import pl.sebcel.gpstracker.location.LocationManager;
import pl.sebcel.gpstracker.repository.GpxSerializer;
import pl.sebcel.gpstracker.repository.TrackRepository;
import pl.sebcel.gpstracker.state.AppState;
import pl.sebcel.gpstracker.state.AppStatus;
import pl.sebcel.gpstracker.state.GpsStatus;
import pl.sebcel.gpstracker.utils.Logger;

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
        Configuration config = configurationProvider.getConfiguration();
        AppState state = new AppState(AppStatus.UNINITIALIZED, GpsStatus.UNINITIALIZED);
        GpxSerializer gpxSerializer = new GpxSerializer();
        TrackRepository trackRepository = new TrackRepository(gpxSerializer);
        model = new AppModel(state);
        view = new AppView(model);
        engine = new AppEngine(state, config, display, trackRepository);

        state.addListener(view);
        view.addListener(engine);

        locationManager = new LocationManager(state, config, display, engine);
    }

    protected void startApp() throws MIDletStateChangeException {
        log.debug("[GpsTracker] startApp");
        this.display.setCurrent(view);
        engine.init();
        locationManager.start();
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        log.debug("[GpsTracker] destroyApp");
    }

    protected void pauseApp() {
        log.debug("[GpsTracker] pauseApp");
    }
}