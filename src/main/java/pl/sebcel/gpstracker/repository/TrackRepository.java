package pl.sebcel.gpstracker.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import pl.sebcel.gpstracker.gpx.GpxSerializer;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.FileUtils;
import pl.sebcel.gpstracker.utils.Logger;

public class TrackRepository {

    private final Logger log = Logger.getLogger();

    private GpxSerializer gpxSerializer;
    private PrintStream writer;
    private FileUtils fileUtils;

    public TrackRepository(GpxSerializer gpxSerializer, FileUtils fileUtils) {
        this.gpxSerializer = gpxSerializer;
        this.fileUtils = fileUtils;
    }

    public void createNewTrack(Track track) {
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

    public void appendTrackPoints(Track track, Vector trackPoints) {
        StringBuffer data = new StringBuffer();
        Date startTime = new Date();

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

        Date endTime = new Date();
        long duration = endTime.getTime() - startTime.getTime();

        log.debug("[TrackRepository] Saved " + trackPoints.size() + " track points (" + dataBytes.length + " bytes, " + duration + " ms)");
    }

    public void saveTrack(Track track) {
        log.debug("[TrackRepository] Saving track " + track.getId());
        Date startTime = new Date();
        String root = fileUtils.findRoot();
        String fileName = DateFormat.getFilename(track.getStartDate(), "", "gpx");

        try {
            String uri = "file:///" + root + fileName;
            System.out.println("Tryging to save" + uri);
            FileConnection fconn = (FileConnection) Connector.open(uri);
            System.out.println("We have file connection: " + fconn);
            if (!fconn.exists()) {
                System.out.println("File does not exist. Tryging to create it");
                fconn.create(); // create the file if it doesn't exist
                System.out.println("OK!");
            } else {
                System.out.println("File already exists");
            }

            System.out.println("Writing to file");

            String xml = gpxSerializer.serialize(track);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(buffer);
            out.println(xml);
            System.out.println("Closing file ");
            byte[] data = buffer.toByteArray();
            fconn.openOutputStream().write(data);
            fconn.close();
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            log.debug("[TrackRepository] Track " + track.getId() + " saved (" + track.getPoints().size() + " points, " + data.length + " bytes, " + duration + " ms)");
        } catch (IOException ioe) {
            log.debug("[TrackRepository] Failed to save track " + track.getId() + ": " + ioe.getMessage());
        }
    }
}