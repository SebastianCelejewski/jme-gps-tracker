package pl.sebcel.gpstracker.gui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import pl.sebcel.gpstracker.AppColor;
import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.plugins.PluginStatus;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.GpsTrackerWorkflow;
import pl.sebcel.gpstracker.workflow.WorkflowStatus;
import pl.sebcel.gpstracker.workflow.WorkflowTransition;
import pl.sebcel.location.GpsStatus;

public class ApplicationView extends Canvas implements AppStateChangeListener {

    private final Logger log = Logger.getLogger();

    private AppModel model;
    private int selectedTransition = 0;
    private GpsTrackerWorkflow workflow = new GpsTrackerWorkflow();
    private Vector listeners = new Vector();
    private ConfigurationView configurationView;
    private Command configurationCommand;

    public ApplicationView(final Display display, AppModel model) {
        super();
        this.model = model;

        configurationCommand = new Command("Configuration", Command.SCREEN, 1);
        this.addCommand(configurationCommand);
        this.setCommandListener(new CommandListener() {

            public void commandAction(Command command, Displayable source) {
                if (command == configurationCommand) {
                    display.setCurrent(configurationView);
                }
            }
        });

        log.debug("Dimensions: " + this.getWidth() + "x" + this.getHeight());
    }

    public void setConfigurationView(ConfigurationView configurationView) {
        this.configurationView = configurationView;
    }

    public void addUserActionListener(UserActionListener listener) {
        listeners.addElement(listener);
    }

    protected void paint(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();

        g.setColor(0, 0, 0);
        g.fillRect(0, 0, width, height);

        GpsStatus gpsStatus = model.getAppState().getGpsStatus();
        WorkflowStatus appStatus = model.getAppState().getAppStatus();

        AppColor appStatusColor = appStatus.getColor();
        int boxMargin = 10;
        int boxWidth = width / 2 - boxMargin;
        int boxHeight = 120;
        g.setColor(appStatusColor.getRed(), appStatusColor.getGreen(), appStatusColor.getBlue());
        g.fillRect(0, 0, boxWidth, boxHeight / 2);
        g.setColor(0, 0, 0);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("APP", 0, 0, Graphics.TOP | Graphics.LEFT);

        AppColor gpsStatusColor = gpsStatus.getColor();
        g.setColor(gpsStatusColor.getRed(), gpsStatusColor.getGreen(), gpsStatusColor.getBlue());
        g.fillRect(boxWidth + boxMargin, 0, boxWidth, boxHeight / 2);
        g.setColor(0, 0, 0);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("GPS", boxWidth + boxMargin, 0, Graphics.TOP | Graphics.LEFT);

        Hashtable pluginsStatuses = model.getAppState().getPluginsStatuses();
        int numberOfPlugins = pluginsStatuses.size();
        if (numberOfPlugins > 0) {
            Enumeration pluginsIds = pluginsStatuses.keys();
            boxWidth = width / numberOfPlugins - boxMargin;
            int idx = 0;
            while (pluginsIds.hasMoreElements()) {
                String pluginId = (String) pluginsIds.nextElement();
                PluginStatus status = (PluginStatus) pluginsStatuses.get(pluginId);
                g.setColor(status.getColor().getRed(), status.getColor().getGreen(), status.getColor().getBlue());
                int x = idx * (boxWidth + boxMargin);
                int y = boxMargin + boxHeight / 2;
                g.fillRect(x, y, boxWidth, boxHeight / 2);
                g.setColor(0, 0, 0);
                g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
                g.drawString(pluginId, x, y, Graphics.TOP | Graphics.LEFT);
                idx++;
            }
        }

        g.setColor(255, 255, 255);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
        g.drawString("Status: " + appStatus.getDisplayName(), 5, boxHeight + 20, Graphics.TOP | Graphics.LEFT);
        g.drawString("GPS: " + gpsStatus.getDisplayName(), 5, boxHeight + 40, Graphics.TOP | Graphics.LEFT);
        g.drawString("Time: " + model.getAppState().getInfoLine1(), 5, boxHeight + 60, Graphics.TOP | Graphics.LEFT);
        g.drawString("Distance: " + model.getAppState().getInfoLine2(), 5, boxHeight + 80, Graphics.TOP | Graphics.LEFT);

        WorkflowTransition[] availableTransitions = workflow.getAvailableTransitions(appStatus);
        int i = boxHeight + 100;
        for (int k = 0; k < availableTransitions.length; k++) {
            g.setColor(255, 255, 255);
            if (k == selectedTransition) {
                g.setColor(255, 0, 0);
                g.drawString(">", 5, i, Graphics.TOP | Graphics.LEFT);
            }
            g.drawString(availableTransitions[k].getName(), 25, i, Graphics.TOP | Graphics.LEFT);
            i = i + 20;
        }

        synchronized (model.getAppState()) {
            model.getAppState().notifyAll();
        }

        synchronized (model.getAppState()) {
            model.getAppState().notifyAll();
        }
    }

    protected void keyPressed(int keyCode) {
        WorkflowTransition[] availableTransitions = workflow.getAvailableTransitions(model.getAppState().getAppStatus());

        if (getGameAction(keyCode) == Canvas.DOWN && selectedTransition < availableTransitions.length - 1) {
            selectedTransition += 1;
        }
        if (getGameAction(keyCode) == Canvas.UP && selectedTransition > 0) {
            selectedTransition -= 1;
        }
        if (getGameAction(keyCode) == Canvas.FIRE && availableTransitions.length > 0) {
            WorkflowTransition statusTransition = availableTransitions[selectedTransition];
            notifyListeners(statusTransition);
            selectedTransition = 0;
        }
        this.repaint();
    }

    public void appStateChanged() {
        this.repaint();
    }

    private void notifyListeners(WorkflowTransition statusTransition) {
        for (int i = 0; i < listeners.size(); i++) {
            UserActionListener listener = (UserActionListener) listeners.elementAt(i);
            listener.userSwitchedTo(statusTransition);
        }
    }
}