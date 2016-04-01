package pl.sebcel.gpstracker.plugins;

import java.util.Vector;

import pl.sebcel.gpstracker.model.Track;

public interface TrackListener {

    public void onTrackCreated(Track track);

    public void onTrackUpdated(Track track, Vector trackPoints);

    public void onTrackCompleted(Track track);
}