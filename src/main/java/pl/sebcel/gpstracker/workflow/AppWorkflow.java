package pl.sebcel.gpstracker.workflow;

import pl.sebcel.gpstracker.state.AppStatus;

public class AppWorkflow {

    public static final StatusTransition INIT = new StatusTransition(0, "Initialize", AppStatus.READY);
    public static final StatusTransition NEW = new StatusTransition(1, "New", AppStatus.READY);
    public static final StatusTransition START = new StatusTransition(2, "Start", AppStatus.STARTED);
    public static final StatusTransition PAUSE = new StatusTransition(3, "Pause", AppStatus.PAUSED);
    public static final StatusTransition RESUME = new StatusTransition(4, "Resume", AppStatus.STARTED);
    public static final StatusTransition STOP = new StatusTransition(5, "Stop", AppStatus.STOPPED);
    public static final StatusTransition ABORT = new StatusTransition(6, "Abort", AppStatus.READY);

    public StatusTransition[] getAvailableTransitions(AppStatus currentStatus) {
        if (currentStatus.equals(AppStatus.READY)) {
            return new StatusTransition[] { START };
        }
        if (currentStatus.equals(AppStatus.STARTED)) {
            return new StatusTransition[] { PAUSE, STOP };
        }
        if (currentStatus.equals(AppStatus.PAUSED)) {
            return new StatusTransition[] { RESUME, STOP };
        }
        if (currentStatus.equals(AppStatus.STOPPED)) {
            return new StatusTransition[] { NEW };
        }
        return new StatusTransition[] {};
    }
}