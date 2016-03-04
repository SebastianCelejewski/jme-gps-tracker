package pl.sebcel.gpstracker.plugins;

import java.util.Vector;

import pl.sebcel.gpstracker.model.Track;

public interface GpsTrackerPlugin {

    public void onNewTrackCreated(Track track);

    public void onTrackUpdated(Track track, Vector trackPoints);

    public void onTrackCompleted(Track track);
}