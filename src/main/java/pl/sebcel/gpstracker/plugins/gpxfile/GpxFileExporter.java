package pl.sebcel.gpstracker.plugins.gpxfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import pl.sebcel.gpstracker.export.gpx.GpxSerializer;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.plugins.GpsTrackerPlugin;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.PluginStatus;
import pl.sebcel.gpstracker.plugins.PluginStatusListener;
import pl.sebcel.gpstracker.plugins.TrackListener;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.FileUtils;
import pl.sebcel.gpstracker.utils.Logger;

/**
 * Exports track data to GPX file
 * 
 * @author Sebastian Celejewski
 */
public class GpxFileExporter implements GpsTrackerPlugin, TrackListener {

    private final static String ID = "GPX";
    private final Logger log = Logger.getLogger();

    private FileUtils fileUtils;
    private GpxSerializer gpxSerializer;

    private PluginStatusListener statusListener;

    public GpxFileExporter(FileUtils fileUtils, GpxSerializer gpxSerializer) {
        this.fileUtils = fileUtils;
        this.gpxSerializer = gpxSerializer;
    }

    public void register(PluginRegistry registry) {
        registry.addTrackListener(this);
        this.statusListener = registry.getPluginStatusListener();
        statusListener.pluginStatusChanged(ID, PluginStatus.UNINITIALIZED);
    }

    public void onTrackCreated(Track track) {
    }

    public void onTrackUpdated(Track track, Vector trackPoints) {
    }

    public void onTrackCompleted(Track track) {
        log.debug("[GpxFileExporter] Saving track " + track.getId());
        Date startTime = new Date();
        String root = fileUtils.findRoot();
        String fileName = DateFormat.getFilename(track.getStartDate(), "", "gpx");

        try {
            String uri = "file:///" + root + fileName;
            FileConnection fconn = (FileConnection) Connector.open(uri);
            if (!fconn.exists()) {
                log.debug("File " + fileName + " does not exist. Creating new file.");
                fconn.create();
            }

            String xml = gpxSerializer.serialize(track);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(buffer);
            out.println(xml);

            byte[] data = buffer.toByteArray();
            fconn.openOutputStream().write(data);
            fconn.close();
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            log.debug("[GpxFileExporter] Track " + track.getId() + " saved (" + track.getPoints().size() + " points, " + data.length + " bytes, " + duration + " ms)");
            statusListener.pluginStatusChanged(ID, PluginStatus.OK);
        } catch (IOException ioe) {
            log.error("[GpxFileExporter] Failed to save track " + track.getId() + ": " + ioe.getMessage());
            statusListener.pluginStatusChanged(ID, PluginStatus.ERROR);
        }
    }
}