package pl.sebcel.gpstracker.gpx;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;

public class JSR280GpxSerializer implements GpxSerializer {

    private final Logger log = Logger.getLogger();

    public String serialize(Track track) {
        try {

            log.debug("[JSR280GpxSerializer] Serializing track data into GPX.");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);

            log.debug("[JSR280GpxSerializer] XMLStreamWriter: " + writer);

            writer.writeStartDocument();

            writer.writeStartElement("gpx");
            writer.writeAttribute("version", "1.1");
            writer.writeAttribute("creator", "JME GPS Tracker");
            writer.writeAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
            writer.writeStartElement("trk");
            writer.writeStartElement("trkseg");

            Vector points = track.getPoints();
            for (int i = 0; i < points.size(); i++) {
                TrackPoint point = (TrackPoint) points.elementAt(i);
                writer.writeStartElement("trkpt");
                writer.writeAttribute("lat", Double.toString(point.getLatitude()));
                writer.writeAttribute("lon", Double.toString(point.getLongitude()));
                writer.writeStartElement("time");
                writer.writeCharacters(DateFormat.format(point.getDateTime()));
                writer.writeEndElement();
                writer.writeEndElement();
            }

            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();

            writer.close();
            out.close();

            byte[] output = out.toByteArray();
            String xml = new String(output);

            log.debug("[JSR280GpxSerializer] Track data serialized successfully.");

            return xml;
        } catch (Exception ex) {
            throw new RuntimeException("[JSR280GpxSerializer] Failed to serialize Gpx Track into XML: " + ex.getMessage());
        }
    }
}