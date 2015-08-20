package pl.sebcel.gpstracker;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import pl.sebcel.gpstracker.events.AppStateChangeListener;
import pl.sebcel.gpstracker.events.UserActionListener;

public class AppView extends Canvas implements AppStateChangeListener {

    private AppModel model;
    private int selectedTransition = 0;
    private AppWorkflow workflow = new AppWorkflow();
    private Vector listeners = new Vector();

    public AppView(AppModel model) {
        super();
        this.model = model;
    }

    public void addListener(UserActionListener listener) {
        listeners.addElement(listener);
    }

    protected void paint(Graphics g) {
        g.setColor(0, 0, 0);
        g.fillRect(0, 0, 320, 320);

        GpsStatus gpsStatus = model.getAppState().getGpsStatus();
        AppStatus appStatus = model.getAppState().getAppStatus();

        AppColor appStatusColor = appStatus.getColor();
        g.setColor(appStatusColor.getRed(), appStatusColor.getGreen(), appStatusColor.getBlue());
        g.fillRect(0, 0, 100, 100);

        AppColor gpsStatusColor = gpsStatus.getColor();
        g.setColor(gpsStatusColor.getRed(), gpsStatusColor.getGreen(), gpsStatusColor.getBlue());
        g.fillRect(120, 0, 100, 100);

        g.setColor(255, 255, 255);
        g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
        g.drawString("Status: " + appStatus.getDisplayName(), 5, 110, Graphics.TOP | Graphics.LEFT);
        g.drawString("GPS: " + gpsStatus.getDisplayName(), 5, 130, Graphics.TOP | Graphics.LEFT);
        g.drawString("Info: " + model.getAppState().getInfo(), 5, 170, Graphics.TOP | Graphics.LEFT);

        StatusTransition[] availableTransitions = workflow.getAvailableTransitions(appStatus);
        int i = 230;
        for (int k = 0; k < availableTransitions.length; k++) {
            g.setColor(255, 255, 255);
            if (k == selectedTransition) {
                g.setColor(255, 0, 0);
                g.drawString(">", 5, i, Graphics.TOP | Graphics.LEFT);
            }
            g.drawString(availableTransitions[k].getName(), 25, i, Graphics.TOP | Graphics.LEFT);
            i = i + 20;
        }
        this.getGameAction(DOWN);
    }

    protected void keyPressed(int keyCode) {
        StatusTransition[] availableTransitions = workflow.getAvailableTransitions(model.getAppState().getAppStatus());

        if (keyCode == getKeyCode(DOWN) && selectedTransition < availableTransitions.length - 1) {
            selectedTransition += 1;
        }
        if (keyCode == getKeyCode(UP) && selectedTransition > 0) {
            selectedTransition -= 1;
        }
        if (keyCode == getKeyCode(FIRE) && availableTransitions.length > 0) {
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