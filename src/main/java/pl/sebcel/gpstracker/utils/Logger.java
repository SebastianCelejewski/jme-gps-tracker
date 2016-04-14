package pl.sebcel.gpstracker.utils;

import java.io.PrintStream;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class Logger {

    private static Logger instance;
    private PrintStream out;
    private boolean isDebugEnabled = false;
    private boolean isInfoEnabled = false;
    private boolean isErrorEnabled = true;

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
        String fileName = DateFormat.getFilename(new Date(), "", "log.txt");
        try {
            String uri = "file:///" + root + fileName;
            FileConnection fconn = (FileConnection) Connector.open(uri);
            if (!fconn.exists()) {
                fconn.create();
            }
            out = new PrintStream(fconn.openOutputStream());
        } catch (Exception ex) {
            System.err.println("Failed to create log file " + fileName);
        }
    }
    
    public void setInfoEnabled(boolean infoEnabled) {
        this.isInfoEnabled = infoEnabled;
    }
    
    public void setDebugEnabled(boolean debugEnabled) {
        this.isDebugEnabled = debugEnabled;
    }
    
    public void setErrorEnabled(boolean errorEnabled) {
        this.isErrorEnabled = errorEnabled;
    }

    public void debug(String message) {
        if (isDebugEnabled) {
            print("DEBUG", message);
        }
    }

    public void info(String message) {
        if (isInfoEnabled) {
            print("INFO", message);
        }
    }

    public void error(String message) {
        if (isErrorEnabled) {
            print("ERROR", message);
        }
    }

    private void print(String level, String message) {
        String date = DateFormat.format(new Date());
        String text = date + " [" + level.toUpperCase() + "] " + message;
        System.out.println(text);
        out.println(text);
        out.flush();
    }
}