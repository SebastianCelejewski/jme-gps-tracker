package pl.sebcel.gpstracker.workflow;

import pl.sebcel.gpstracker.AppColor;

public class WorkflowStatus {

    public static WorkflowStatus UNINITIALIZED = new WorkflowStatus(0, "Uninitialized", new AppColor(255, 255, 255));
    public static WorkflowStatus READY = new WorkflowStatus(1, "Ready", new AppColor(255, 255, 0));
    public static WorkflowStatus STARTING = new WorkflowStatus(2, "Starting", new AppColor(0, 200, 0));
    public static WorkflowStatus STARTED = new WorkflowStatus(3, "Started", new AppColor(0, 255, 0));
    public static WorkflowStatus STOPPING = new WorkflowStatus(4, "Stopping", new AppColor(0, 0, 200));
    public static WorkflowStatus STOPPED = new WorkflowStatus(5, "Stopped", new AppColor(0, 0, 255));
    public static WorkflowStatus PAUSED = new WorkflowStatus(6, "Paused", new AppColor(255, 128, 0));

    private int id;
    private String displayName;
    private AppColor color;

    private WorkflowStatus(int id, String displayName, AppColor color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AppColor getColor() {
        return color;
    }

    public int hashCode() {
        return 5;
    }

    public boolean equals(Object obj) {
        return this.id == ((WorkflowStatus) obj).id;
    }
}