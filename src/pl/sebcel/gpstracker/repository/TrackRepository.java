package pl.sebcel.gpstracker.repository;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.utils.DateFormat;

public class TrackRepository {

    public void saveTrack(Track track) {
        System.out.println("Saving track " + track.getId());
        Enumeration roots = FileSystemRegistry.listRoots();
        while (roots.hasMoreElements()) {
            System.out.println("Root: " + roots.nextElement());
        }

        String root = (String) FileSystemRegistry.listRoots().nextElement();
        System.out.println("Root: " + root);

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

            PrintStream out = new PrintStream(fconn.openOutputStream());

            out.println("Track " + track.getId());
            out.println("Start date: " + track.getStartDate());

            Vector points = track.getPoints();
            for (int i = 0; i < points.size(); i++) {
                TrackPoint point = (TrackPoint) points.elementAt(i);
                out.println(DateFormat.format(point.getDateTime()) + ";" + point.getLatitude() + ";" + point.getLongitude());
            }

            System.out.println("Closing file ");

            fconn.close();
        } catch (IOException ioe) {
            System.out.println("exception = " + ioe);
        }
    }
}