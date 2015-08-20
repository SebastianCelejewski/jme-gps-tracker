package pl.sebcel.gpstracker;

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import pl.sebcel.gpstracker.location.LocationManager;
import pl.sebcel.gpstracker.repository.TrackRepository;

public class GpsTracker extends MIDlet {

    private Display display;
    private AppView view;
    private AppEngine engine;
    private AppModel model;
    private LocationManager locationManager;

    public GpsTracker() {
        
        this.display = Display.getDisplay(this);

        AppState state = new AppState(AppStatus.UNINITIALIZED, GpsStatus.UNINITIALIZED);
        TrackRepository trackRepository = new TrackRepository();
        model = new AppModel(state);
        view = new AppView(model);
        engine = new AppEngine(state, trackRepository);
        
        state.addListener(view);
        view.addListener(engine);

        locationManager = new LocationManager(state, engine);
    }

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("[StartApp]");
        this.display.setCurrent(view);
        engine.init();
        locationManager.start();
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }
}