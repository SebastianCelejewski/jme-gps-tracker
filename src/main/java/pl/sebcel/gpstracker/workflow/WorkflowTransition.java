package pl.sebcel.gpstracker.workflow;

public class WorkflowTransition {

    public static final WorkflowTransition INIT = new WorkflowTransition(0, "Initialize", WorkflowStatus.READY);
    public static final WorkflowTransition NEW = new WorkflowTransition(1, "New", WorkflowStatus.READY);
    public static final WorkflowTransition START = new WorkflowTransition(2, "Start", WorkflowStatus.STARTED);
    public static final WorkflowTransition PAUSE = new WorkflowTransition(3, "Pause", WorkflowStatus.PAUSED);
    public static final WorkflowTransition RESUME = new WorkflowTransition(4, "Resume", WorkflowStatus.STARTED);
    public static final WorkflowTransition STOP = new WorkflowTransition(5, "Stop", WorkflowStatus.STOPPED);
    public static final WorkflowTransition ABORT = new WorkflowTransition(6, "Abort", WorkflowStatus.READY);
    
    private int id;
    private WorkflowStatus targetStatus;
    private String name;

    public WorkflowTransition(int id, String name, WorkflowStatus targetStatus) {
        this.id = id;
        this.name = name;
        this.targetStatus = targetStatus;
    }

    public WorkflowStatus getTargetStatus() {
        return targetStatus;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        return id == ((WorkflowTransition) o).id;
    }
}