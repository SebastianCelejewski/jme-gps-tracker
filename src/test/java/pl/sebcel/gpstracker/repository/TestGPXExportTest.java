package pl.sebcel.gpstracker.repository;

import java.util.Date;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import junit.framework.TestCase;

public class TestGPXExportTest extends TestCase {

    public void testHopsaTest() {
        String trackId = "track-id";
        Date date1 = new Date();
        Date date2 = new Date(date1.getTime() + 30000);

        Track track = new Track(trackId, date1);
        track.addPoint(new TrackPoint(date1, 12.5, 15.6, 12.1, 1.0, 2.0));
        track.addPoint(new TrackPoint(date2, 12.4, 15.7, 12.1, 1.0, 2.0));

        GpxSerializer serializer = new GpxSerializer();

        String output = serializer.serialize(track);
        System.out.println(output);

    }
}
