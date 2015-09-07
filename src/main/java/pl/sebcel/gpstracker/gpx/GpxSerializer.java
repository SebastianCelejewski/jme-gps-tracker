package pl.sebcel.gpstracker.gpx;

import pl.sebcel.gpstracker.model.Track;

public interface GpxSerializer {
    public String serialize(Track track);
}
