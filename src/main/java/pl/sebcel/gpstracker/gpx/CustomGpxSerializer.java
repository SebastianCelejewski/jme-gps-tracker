package pl.sebcel.gpstracker.gpx;

import java.util.Vector;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;

public class CustomGpxSerializer implements GpxSerializer {

    private final Logger log = Logger.getLogger();

    public String serialize(Track track) {
        try {

            log.debug("[CustomGpxSerializer] Serializing track data into GPX.");

            StringBuffer result = new StringBuffer();

            result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            result.append("<gpx version=\"1.1\" creator=\"JavaME GPS Tracker\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
            result.append("<trk>\n");
            result.append("<trkseg>\n");

            Vector points = track.getPoints();
            for (int i = 0; i < points.size(); i++) {
                TrackPoint point = (TrackPoint) points.elementAt(i);
                result.append("<trkpt lat=\"" + Double.toString(point.getLatitude()) + "\" lon=\"" + Double.toString(point.getLongitude()) + "\">\n");
                result.append("<time>" + DateFormat.format(point.getDateTime()) + "</time>\n");
                result.append("</trkpt>\n");
            }

            result.append("</trkseg>\n");
            result.append("</trk>\n");
            result.append("</gpx>\n");

            String xml = result.toString();

            log.debug("[CustomGpxSerializer] Track data serialized successfully.");

            return xml;
        } catch (Exception ex) {
            throw new RuntimeException("[CustomGpxSerializer] Failed to serialize Gpx Track into XML: " + ex.getMessage());
        }
    }
}