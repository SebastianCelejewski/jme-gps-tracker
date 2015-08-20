package pl.sebcel.gpstracker.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;

public class TrackRepository {

    private final static Logger log = Logger.getLogger();

    public void saveTrack(Track track) {
        log.debug("[TrackRepository] Saving track " + track.getId());
        Date startTime = new Date();
        String root = (String) FileSystemRegistry.listRoots().nextElement();
        String fileName = DateFormat.getFilename(track.getStartDate());

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

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            PrintStream out = new PrintStream(buffer);

            out.println("Track " + track.getId());
            out.println("Start date: " + track.getStartDate());

            Vector points = track.getPoints();
            for (int i = 0; i < points.size(); i++) {
                TrackPoint point = (TrackPoint) points.elementAt(i);
                out.println(DateFormat.format(point.getDateTime()) + ";" + point.getLatitude() + ";" + point.getLongitude());
            }

            System.out.println("Closing file ");

            byte[] data = buffer.toByteArray();

            fconn.openOutputStream().write(data);

            fconn.close();

            Date endTime = new Date();

            long duration = endTime.getTime() - startTime.getTime();

            log.debug("[TrackRepository] Track " + track.getId() + " saved (" + points.size() + " points, " + data.length + " bytes, " + duration + " ms)");
        } catch (IOException ioe) {
            log.debug("[TrackRepository] Failed to save track " + track.getId() + ": " + ioe.getMessage());
        }
    }
}