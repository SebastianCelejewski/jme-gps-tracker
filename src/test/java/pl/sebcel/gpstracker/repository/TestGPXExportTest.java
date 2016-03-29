package pl.sebcel.gpstracker.repository;

import java.util.Date;

import junit.framework.TestCase;
import pl.sebcel.gpstracker.export.gpx.JSR280GpxSerializer;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;

public class TestGPXExportTest extends TestCase {

    public void manualGpsExportTest() {
        String trackId = "track-id";
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 30000);

        Track track = new Track(trackId, date1);
        track.addPoint(new TrackPoint(date1, 12.5, 15.6, 12.1, 0.5, 1.0, 2.0));
        track.addPoint(new TrackPoint(date2, 12.4, 15.7, 12.1, 0.6, 1.0, 2.0));

        JSR280GpxSerializer serializer = new JSR280GpxSerializer();

        String output = serializer.serialize(track);
        System.out.println(output);
    }
}
