package pl.sebcel.gpstracker.export.gpx;

import pl.sebcel.gpstracker.model.Track;

public interface GpxSerializer {
    public String serialize(Track track);
}
