package pl.sebcel.gpstracker.plugins.endomondo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import pl.sebcel.gpstracker.GpsTracker;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.plugins.GpsTrackerPlugin;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.TrackListener;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

/**
 * Uploads track data to Endomondo server
 * 
 * @author Sebastian Celejewski
 */
public class EndomondoConnector implements GpsTrackerPlugin, TrackListener {

    private final Logger log = Logger.getLogger();
    private final static String endomondoAuthenticationToken = "PUT_AUTHENTICATION_TOKEN_HERE";

    private long startTime;
    private int sportId = 1;
    private boolean startCommandWasSent = false;
    private TrackPoint lastTrackPoint = null;

    public void register(PluginRegistry registry) {
        registry.addTrackListener(this);
    }

    public void onTrackCreated(Track track) {
        log.debug("[EncomondoConnector] onTrackCreated");

        this.startTime = System.currentTimeMillis();
        this.startCommandWasSent = false;
        this.lastTrackPoint = null;

        sendConfigureRequest();

        log.debug("[EncomondoConnector] onNewTrackCreated completed");
    }

    public void onTrackUpdated(Track track, Vector trackPoints) {
        log.debug("[EncomondoConnector] onTrackUpdated");
        uploadToEndomondo(trackPoints, false);
        log.debug("[EncomondoConnector] onTrackUpdated completed");
    }

    public void onTrackCompleted(Track track) {
        log.debug("[EncomondoConnector] onTrackCompleted");
        Vector lastTrackPointVector = new Vector();
        if (lastTrackPoint != null) {
            TrackPoint trackPoint = new TrackPoint(new Date(), lastTrackPoint.getLatitude(), lastTrackPoint.getLongitude(), lastTrackPoint.getAltitude(), lastTrackPoint.getHorizontalAccuracy(), lastTrackPoint.getVerticalAccuracy());
            lastTrackPointVector.addElement(trackPoint);
        }
        uploadToEndomondo(lastTrackPointVector, true);
        log.debug("[EncomondoConnector] onTrackCompleted completed");
    }

    private void sendConfigureRequest() {
        HttpConnection connection = null;
        try {
            log.debug("[EndomondoConnector] Sending configuration request to Endomondo server.");
            log.debug("[EndomondoConnector] Opening connection to Endomondo");

            connection = (HttpConnection) Connector.open("http://www.endomondo.com/mobile/config?authToken=" + endomondoAuthenticationToken + "&vendor=Unknown&model=generic&os=Java&appVariant=Website&appVersion=" + GpsTracker.version);
            connection.setRequestMethod(HttpConnection.GET);
            connection.setRequestProperty("User-Agent", "jme-gps-tracker " + GpsTracker.version);

            int responseCode = connection.getResponseCode();
            log.debug("[EndomondoConnector] Response code: " + responseCode);

            if (responseCode != HttpConnection.HTTP_OK) {
                log.debug("[EndomondoConnector] Invalid HTTP response code. Aborting.");
                return;
            }

            int responseLength = (int) connection.getLength();
            log.debug("[EndomondoConnector] Received " + responseLength + " bytes of data.");

            InputStream in = connection.openInputStream();
            if (in == null) {
                log.debug("[EndomondoConnector] Cannot open input stream. Aborting.");
                return;
            }
            byte[] data = new byte[responseLength];
            in.read(data);
            in.close();

            String serverResponse = new String(data);
            log.debug("[EndomondoConnector] Server response: " + serverResponse);
            log.debug("[EndomondoConnector] Done");

        } catch (Exception ex) {
            log.debug("[EndomondoConnector] Failed to connect to Endomondo server: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                // intentional
            }
        }
    }

    private void uploadToEndomondo(Vector trackPoints, boolean thisIsLastBatchOfTrackPoints) {
        log.debug("[EncomondoConnector] uploadToEndomondo: " + trackPoints.size() + " points");
        if (trackPoints.size() == 0) {
            return;
        }
        HttpConnection connection = null;
        try {
            String trackData = composeTrackData(trackPoints, thisIsLastBatchOfTrackPoints);
            log.debug("[EndomondoConnector] Batch of track points to be sent to Endomondo server:");
            log.debug(trackData);

            byte[] data = deflate(trackData);

            log.debug("[EndomondoConnector] Opening connection to Endomondo");

            long duration = (System.currentTimeMillis() - startTime) / 1000;

            connection = (HttpConnection) Connector.open("http://www.endomondo.com/mobile/track?gzip=false&deflate=true&workoutId=" + startTime + "&authToken=" + endomondoAuthenticationToken + "&duration=" + duration + "&sport=" + sportId);
            connection.setRequestMethod(HttpConnection.POST);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Transfer-Encoding", "chunked");
            connection.setRequestProperty("User-Agent", "jme-gps-tracker " + GpsTracker.version);

            OutputStream os = connection.openOutputStream();
            PrintStream out = new PrintStream(os);
            out.flush();
            os.write(data, 0, data.length);
            os.flush();
            out.flush();

            log.debug("[EndomondoConnector] Data sent. Waiting for response");

            InputStream is = connection.openInputStream();
            String serverResponse = "";

            int b = 0;
            long length = 0;
            do {
                b = is.read();
                if (b != -1) {
                    length++;
                    serverResponse += (char) b;
                }
            } while (b != -1);

            os.close();
            is.close();

            log.debug("[EndomondoConnector] Received " + length + " bytes of data.");
            log.debug("[EndomondoConnector] Server response:");
            log.debug(serverResponse);
            log.debug("[EndomondoConnector] Done");

        } catch (Exception ex) {
            log.debug("[EndomondoConnector] Failed to send data to Endomondo server: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                // intentional
            }
        }
    }

    private String composeTrackData(Vector trackPoints, boolean thisIsLastBatchOfTrackPoints) {
        String result = "";
        for (int i = 0; i < trackPoints.size(); i++) {
            String command = "";
            if (startCommandWasSent == false) {
                command = "2";
            }
            if (i == trackPoints.size() - 1 && thisIsLastBatchOfTrackPoints) {
                command = "3";
            }
            TrackPoint trackPoint = ((TrackPoint) trackPoints.elementAt(i));
            result += composeTrackPoint(trackPoint, command) + "\n";
            startCommandWasSent = true;
            lastTrackPoint = trackPoint;
        }
        return result;
    }

    private String composeTrackPoint(TrackPoint trackPoint, String command) {
        String dateTimeStr = DateFormat.endomondoFormat(trackPoint.getDateTime());
        return dateTimeStr + ";" + command + ";" + trackPoint.getLatitude() + ";" + trackPoint.getLongitude() + ";;;";
    }

    private byte[] deflate(String input) {
        int err;
        int comprLen = 40000;
        byte[] compr = new byte[comprLen];

        Deflater deflater = null;

        try {
            deflater = new Deflater(JZlib.Z_DEFAULT_COMPRESSION);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize deflater: " + ex.getMessage());
        }

        byte[] inputBytes = input.getBytes();

        deflater.setInput(inputBytes);
        deflater.setOutput(compr);

        while (deflater.total_in != inputBytes.length && deflater.total_out < comprLen) {
            deflater.avail_in = deflater.avail_out = 1; // force small buffers
            err = deflater.deflate(JZlib.Z_NO_FLUSH);
            CHECK_ERR(deflater, err, "deflate");
        }

        while (true) {
            deflater.avail_out = 1;
            err = deflater.deflate(JZlib.Z_FINISH);
            if (err == JZlib.Z_STREAM_END) {
                break;
            }
            CHECK_ERR(deflater, err, "deflate");
        }

        err = deflater.end();
        CHECK_ERR(deflater, err, "deflateEnd");

        try {
            ByteArrayOutputStream werwer = new ByteArrayOutputStream();
            werwer.write(compr, 0, (int) deflater.getTotalOut());
            werwer.close();

            return werwer.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void CHECK_ERR(ZStream z, int err, String msg) {
        if (err != JZlib.Z_OK) {
            String message = "";
            if (z.msg != null) {
                message += z.msg + " ";
            }
            message += " error: " + err;
            throw new RuntimeException("Failed to deflate: " + message);
        }
    }
}