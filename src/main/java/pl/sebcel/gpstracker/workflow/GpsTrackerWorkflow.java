package pl.sebcel.gpstracker.workflow;

public class GpsTrackerWorkflow {

    public WorkflowTransition[] getAvailableTransitions(WorkflowStatus currentStatus) {
        if (currentStatus.equals(WorkflowStatus.READY)) {
            return new WorkflowTransition[] { WorkflowTransition.START };
        }
        if (currentStatus.equals(WorkflowStatus.STARTED)) {
            return new WorkflowTransition[] { WorkflowTransition.PAUSE, WorkflowTransition.STOP };
        }
        if (currentStatus.equals(WorkflowStatus.PAUSED)) {
            return new WorkflowTransition[] { WorkflowTransition.RESUME, WorkflowTransition.STOP };
        }
        if (currentStatus.equals(WorkflowStatus.STOPPED)) {
            return new WorkflowTransition[] { WorkflowTransition.NEW };
        }
        return new WorkflowTransition[] {};
    }
}