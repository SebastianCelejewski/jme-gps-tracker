package pl.sebcel.gpstracker.gui;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import pl.sebcel.gpstracker.AppColor;
import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.events.UserActionListener;
import pl.sebcel.gpstracker.state.AppStatus;
import pl.sebcel.gpstracker.state.GpsStatus;
import pl.sebcel.gpstracker.utils.Logger;
import pl.sebcel.gpstracker.workflow.AppWorkflow;
import pl.sebcel.gpstracker.workflow.StatusTransition;

public class AppView extends Canvas implements AppStateChangeListener {

    private final Logger log = Logger.getLogger();

    private AppModel model;
    private int selectedTransition = 0;
    private AppWorkflow workflow = new AppWorkflow();
    private Vector listeners = new Vector();

    public AppView(AppModel model) {
        super();
        this.model = model;
        log.debug("Dimensions: " + this.getWidth() + "x" + this.getHeight());
    }

    public void addListener(UserActionListener listener) {
        listeners.addElement(listener);
    }

    protected void paint(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, width, height);

        GpsStatus gpsStatus = model.getAppState().getGpsStatus();
        AppStatus appStatus = model.getAppState().getAppStatus();

        AppColor appStatusColor = appStatus.getColor();
        int boxMargin = 10;
        int boxWidth = width / 2 - boxMargin;
        int boxHeight = 80;
        g.setColor(appStatusColor.getRed(), appStatusColor.getGreen(), appStatusColor.getBlue());
        g.fillRect(0, 0, boxWidth, boxHeight);

        AppColor gpsStatusColor = gpsStatus.getColor();
        g.setColor(gpsStatusColor.getRed(), gpsStatusColor.getGreen(), gpsStatusColor.getBlue());
        g.fillRect(boxWidth + boxMargin, 0, boxWidth, boxHeight);

        g.setColor(255, 255, 255);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
        g.drawString("Status: " + appStatus.getDisplayName(), 5, boxHeight + 20, Graphics.TOP | Graphics.LEFT);
        g.drawString("GPS: " + gpsStatus.getDisplayName(), 5, boxHeight + 40, Graphics.TOP | Graphics.LEFT);
        g.drawString("Info: " + model.getAppState().getInfo(), 5, boxHeight + 60, Graphics.TOP | Graphics.LEFT);

        StatusTransition[] availableTransitions = workflow.getAvailableTransitions(appStatus);
        int i = boxHeight + 80;
        for (int k = 0; k < availableTransitions.length; k++) {
            g.setColor(255, 255, 255);
            if (k == selectedTransition) {
                g.setColor(255, 0, 0);
                g.drawString(">", 5, i, Graphics.TOP | Graphics.LEFT);
            }
            g.drawString(availableTransitions[k].getName(), 25, i, Graphics.TOP | Graphics.LEFT);
            i = i + 20;
        }

        System.out.println("AppView is about to enter synchronized block");
        System.out.println("Semaphor: "+model.getAppState());
        synchronized (model.getAppState()) {
            System.out.println("AppView entered synchronized block and is about to notify all");
            model.getAppState().notifyAll();
            System.out.println("AppView notified all is about to exit synchronized block");
        }
        System.out.println("AppView exited synchronized block");
        
        synchronized (model.getAppState()) {
            model.getAppState().notifyAll();
        }
    }

    protected void keyPressed(int keyCode) {
        StatusTransition[] availableTransitions = workflow.getAvailableTransitions(model.getAppState().getAppStatus());

        if (getGameAction(keyCode) == Canvas.DOWN && selectedTransition < availableTransitions.length - 1) {
            selectedTransition += 1;
        }
        if (getGameAction(keyCode) == Canvas.UP && selectedTransition > 0) {
            selectedTransition -= 1;
        }
        if (getGameAction(keyCode) == Canvas.FIRE && availableTransitions.length > 0) {
            StatusTransition statusTransition = availableTransitions[selectedTransition];
            notifyListeners(statusTransition);
            selectedTransition = 0;
        }
        this.repaint();
    }

    public void appStateChanged() {
        this.repaint();
    }

    private void notifyListeners(StatusTransition statusTransition) {
        for (int i = 0; i < listeners.size(); i++) {
            UserActionListener listener = (UserActionListener) listeners.elementAt(i);
            listener.userSwitchedTo(statusTransition);
        }
    }
}