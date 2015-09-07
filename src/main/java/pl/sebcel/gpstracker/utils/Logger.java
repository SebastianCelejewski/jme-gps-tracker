package pl.sebcel.gpstracker.utils;

import java.io.PrintStream;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Logger {

    private static Logger instance;
    private PrintStream out;

    public static Logger getLogger() {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private static Logger createInstance() {
        return new Logger();
    }

    public Logger() {
        String root = new FileUtils().findRoot();
        String fileName = DateFormat.getFilename(new Date(), "", "log");
        try {
            String uri = "file:///" + root + fileName;
            FileConnection fconn = (FileConnection) Connector.open(uri);
            if (!fconn.exists()) {
                fconn.create(); // create the file if it doesn't exist
            }

            out = new PrintStream(fconn.openOutputStream());
        } catch (Exception ex) {
            System.err.println("Failed to create log file " + fileName);
        }
    }

    public void debug(String info) {
        String date = DateFormat.format(new Date());
        String message = date + ": " + info;
        System.out.println(message);
        out.println(message);
        out.flush();
    }
}