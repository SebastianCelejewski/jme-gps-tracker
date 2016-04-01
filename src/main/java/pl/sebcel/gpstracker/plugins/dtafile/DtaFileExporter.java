package pl.sebcel.gpstracker.plugins.dtafile;

import java.io.PrintStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.plugins.GpsTrackerPlugin;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.TrackListener;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.FileUtils;
import pl.sebcel.gpstracker.utils.Logger;

/**
 * Exports track data to text file
 * 
 * @author Sebastian Celejewski
 */
public class DtaFileExporter implements GpsTrackerPlugin, TrackListener {

    private final Logger log = Logger.getLogger();

    private PrintStream writer;
    private FileUtils fileUtils;

    public DtaFileExporter(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public void register(PluginRegistry registry) {
        registry.addTrackListener(this);
    }

    public void onTrackCreated(Track track) {
        try {
            String root = fileUtils.findRoot();
            String fileName = DateFormat.getFilename(track.getStartDate(), "", "dta");
            String uri = "file:///" + root + fileName;
            FileConnection fconn = (FileConnection) Connector.open(uri);
            fconn.create();
            writer = new PrintStream(fconn.openOutputStream());
        } catch (Exception ex) {
            log.debug("Cannot create PrintStream for track " + track.getId() + ": " + ex.getMessage());
        }
    }

    public void onTrackUpdated(Track track, Vector trackPoints) {
        StringBuffer data = new StringBuffer();

        for (int i = 0; i < trackPoints.size(); i++) {
            TrackPoint point = (TrackPoint) trackPoints.elementAt(i);
            data.append(DateFormat.format(point.getDateTime()) + ";" + point.getLatitude() + ";" + point.getLongitude() + ";" + point.getAltitude() + ";" + point.getHorizontalAccuracy() + ";" + point.getVerticalAccuracy() + "\n");
        }

        String dataString = data.toString();
        byte[] dataBytes = dataString.getBytes();

        try {
            writer.write(dataBytes);
        } catch (Exception ex) {
            log.debug("Failed to save data to file: " + ex.getMessage());
        }
        writer.flush();
    }

    public void onTrackCompleted(Track track) {
    }
}