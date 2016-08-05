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

import pl.sebcel.gpstracker.ConfigurationProvider;
import pl.sebcel.gpstracker.GpsTracker;
import pl.sebcel.gpstracker.model.Track;
import pl.sebcel.gpstracker.model.TrackPoint;
import pl.sebcel.gpstracker.plugins.ConfigListener;
import pl.sebcel.gpstracker.plugins.GpsTrackerPlugin;
import pl.sebcel.gpstracker.plugins.PluginConfig;
import pl.sebcel.gpstracker.plugins.PluginRegistry;
import pl.sebcel.gpstracker.plugins.PluginStatus;
import pl.sebcel.gpstracker.plugins.PluginStatusListener;
import pl.sebcel.gpstracker.plugins.TrackListener;
import pl.sebcel.gpstracker.utils.DateFormat;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.utils.StringUtils;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

/**
 * Uploads track data to Endomondo server
 * 
 * @author Sebastian Celejewski
 */
public class EndomondoConnector implements GpsTrackerPlugin, TrackListener, ConfigListener {

    private final Logger log = Logger.getLogger();

    private final static String ID = "ENDO";

    private final static String USER_NAME = "User name";
    private final static String PASSWORD = "Password";
    private final static String AUTHENTICATION_TOKEN = "Authentication token";

    private long startTime;
    private int sportId = 1;
    private boolean startCommandWasSent = false;
    private TrackPoint lastTrackPoint = null;
    private PluginConfig pluginConfig = new PluginConfig();
    private ConfigurationProvider configurationProvider;
    private boolean connectedToServer = false;
    private Vector trackPointsToBeUploaded = null;
    private PluginStatusListener statusListener;

    public void register(PluginRegistry registry) {
        registry.addTrackListener(this);
        registry.addConfigListener(this);

        pluginConfig.setPluginName("Endomondo Connector");
        pluginConfig.setConfigurationKeys(new String[] { USER_NAME, PASSWORD, AUTHENTICATION_TOKEN });

        statusListener = registry.getPluginStatusListener();
        statusListener.pluginStatusChanged(ID, PluginStatus.UNINITIALIZED);
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public void onTrackCreated(Track track) {
        this.startTime = System.currentTimeMillis();
        this.startCommandWasSent = false;
        this.lastTrackPoint = null;

        connectedToServer = false;

        String authenticationToken = pluginConfig.getValue("Authentication token");
        if (authenticationToken != null && authenticationToken.length() > 0) {
            log.debug("[EndomondoConnector] Authentication token is provided. Trying to establish connection to Endomondo server using authentication token.");
            connectedToServer = sendConfigureRequest();
            if (connectedToServer) {
                log.debug("[EndomondoConnector] Connection to Endomondo server has been established successfully using authentication token.");
            } else {
                log.debug("[EndomondoConnector] Failed to establish connection to Endomondo server using authentication token. Trying to establish connection to Endomondo server using login and password.");
                connectedToServer = connectToServer();
            }
        } else {
            log.debug("[EndomondoConnector] Authentication token is not provided. Trying to establish connection to Endomondo server using login and password.");
            connectedToServer = connectToServer();
        }

        if (connectedToServer) {
            statusListener.pluginStatusChanged(ID, PluginStatus.OK);
        } else {
            statusListener.pluginStatusChanged(ID, PluginStatus.ERROR);
        }

        trackPointsToBeUploaded = new Vector();

        log.debug("[EndomondoConnector] Connected to server: " + connectedToServer);
    }

    private boolean connectToServer() {
        boolean connectedToServer = sendConnectRequest();
        if (connectedToServer) {
            log.debug("[EndomondoConnector] Connection to Endomondo server has been established successfully using login and password.");
            configurationProvider.updateViewAndStorage();
            sendConfigureRequest();
        } else {
            log.info("[EndomondoConnector] Failed to establish connection to Endomondo server using login and password.");
        }
        return connectedToServer;
    }

    public void onTrackUpdated(Track track, Vector trackPoints) {
        for (int i = 0; i < trackPoints.size(); i++) {
            trackPointsToBeUploaded.addElement(trackPoints.elementAt(i));
        }
        if (connectedToServer) {
            uploadToEndomondo(trackPointsToBeUploaded, false);
        }
    }

    public void onTrackCompleted(Track track) {
        if (connectedToServer) {
            Vector lastTrackPointVector = new Vector();
            if (lastTrackPoint != null) {
                TrackPoint trackPoint = new TrackPoint(new Date(), lastTrackPoint.getLatitude(), lastTrackPoint.getLongitude(), lastTrackPoint.getAltitude(), lastTrackPoint.getDistance(), lastTrackPoint.getHorizontalAccuracy(), lastTrackPoint.getVerticalAccuracy());
                lastTrackPointVector.addElement(trackPoint);
            }
            uploadToEndomondo(lastTrackPointVector, true);
        }
    }

    private boolean sendConnectRequest() {
        HttpConnection connection = null;
        try {
            log.debug("[EndomondoConnector] Sending login request to Endomondo server.");

            String email = pluginConfig.getValue(USER_NAME);
            String password = pluginConfig.getValue(PASSWORD);

            String url = "http://www.endomondo.com/mobile/auth?action=PAIR&email=" + email + "&password=" + password + "&country=US&deviceId=1456610093443-2958678941403580632&vendor=Unknown&model=generic&os=Java&appVariant=Website&appVersion=" + GpsTracker.version + "&measure=METRIC";

            connection = (HttpConnection) Connector.open(url);
            connection.setRequestMethod(HttpConnection.GET);
            connection.setRequestProperty("User-Agent", "jme-gps-tracker " + GpsTracker.version);

            int responseCode = connection.getResponseCode();
            log.debug("[EndomondoConnector] Response code: " + responseCode);

            if (responseCode != HttpConnection.HTTP_OK) {
                log.debug("[EndomondoConnector] Invalid HTTP response code. Aborting.");
                return false;
            }

            int responseLength = (int) connection.getLength();
            log.debug("[EndomondoConnector] Received " + responseLength + " bytes of data.");

            InputStream in = connection.openInputStream();
            if (in == null) {
                log.debug("[EndomondoConnector] Cannot open input stream. Aborting.");
                return false;
            }
            byte[] data = new byte[responseLength];
            in.read(data);
            in.close();

            String serverResponse = new String(data);

            if (!serverResponse.startsWith("OK")) {
                log.info("[EndomondoConnector] Authentication failed: " + serverResponse);
                return false;
            }

            String[] lines = StringUtils.split(serverResponse, '\n');
            for (int i = 0; i < lines.length; i++) {
                String[] tokens = StringUtils.split(lines[i], '=');
                if (tokens[0].equals("authToken")) {
                    pluginConfig.setValue(AUTHENTICATION_TOKEN, tokens[1]);
                }
            }

            return true;

        } catch (Exception ex) {
            log.error("[EndomondoConnector] Failed to connect to Endomondo server: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                // intentional
            }
        }
    }

    private boolean sendConfigureRequest() {
        HttpConnection connection = null;
        try {
            log.debug("[EndomondoConnector] Sending configuration request to Endomondo server.");

            connection = (HttpConnection) Connector.open("http://www.endomondo.com/mobile/config?authToken=" + pluginConfig.getValue(AUTHENTICATION_TOKEN) + "&vendor=Unknown&model=generic&os=Java&appVariant=Website&appVersion=" + GpsTracker.version);
            connection.setRequestMethod(HttpConnection.GET);
            connection.setRequestProperty("User-Agent", "jme-gps-tracker " + GpsTracker.version);

            int responseCode = connection.getResponseCode();
            log.debug("[EndomondoConnector] Response code: " + responseCode);

            if (responseCode != HttpConnection.HTTP_OK) {
                log.debug("[EndomondoConnector] Invalid HTTP response code. Aborting.");
                return false;
            }

            int responseLength = (int) connection.getLength();
            log.debug("[EndomondoConnector] Received " + responseLength + " bytes of data.");

            InputStream in = connection.openInputStream();
            if (in == null) {
                log.debug("[EndomondoConnector] Cannot open input stream. Aborting.");
                return false;
            }
            byte[] data = new byte[responseLength];
            in.read(data);
            in.close();

            String serverResponse = new String(data);

            if (!serverResponse.startsWith("OK")) {
                log.debug("[EndomondoConnector] Authentication failed: " + serverResponse);
                return false;
            }

            return true;
        } catch (Exception ex) {
            log.error("[EndomondoConnector] Failed to connect to Endomondo server: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                // intentional
            }
        }
    }

    private void uploadToEndomondo(Vector trackPoints, boolean thisIsLastBatchOfTrackPoints) {
        log.debug("[EndomondoConnector] Uploading " + trackPoints.size() + " points to Endomondo server.");
        if (trackPoints.size() == 0) {
            return;
        }
        HttpConnection connection = null;
        try {
            String trackData = composeTrackData(trackPoints, thisIsLastBatchOfTrackPoints);

            byte[] data = deflate(trackData);

            long duration = (System.currentTimeMillis() - startTime) / 1000;

            connection = (HttpConnection) Connector.open("http://www.endomondo.com/mobile/track?gzip=false&deflate=true&workoutId=" + startTime + "&authToken=" + pluginConfig.getValue(AUTHENTICATION_TOKEN) + "&duration=" + duration + "&sport=" + sportId);
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

            InputStream is = connection.openInputStream();
            String serverResponse = "";

            int b = 0;
            do {
                b = is.read();
                if (b != -1) {
                    serverResponse += (char) b;
                }
            } while (b != -1);

            os.close();
            is.close();

            if (!serverResponse.trim().startsWith("OK")) {
                log.debug("[EndomondoConnector] WARNING: Endomondo server response: " + serverResponse);
            }
            trackPointsToBeUploaded.removeAllElements();
            log.debug("[EndomondoConnector] Upload to Endomondo server completed.");
            statusListener.pluginStatusChanged(ID, PluginStatus.OK);
        } catch (Exception ex) {
            log.error("[EndomondoConnector] Failed to send data to Endomondo server: " + ex.getMessage());
            ex.printStackTrace();
            statusListener.pluginStatusChanged(ID, PluginStatus.ERROR);
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
        return dateTimeStr + ";" + command + ";" + trackPoint.getLatitude() + ";" + trackPoint.getLongitude() + ";" + trackPoint.getDistance() + ";;";
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

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public void onConfigUpdated(PluginConfig pluginConfig) {
    }
}