package pl.sebcel.gpstracker.events;

import pl.sebcel.gpstracker.workflow.WorkflowTransition;

public interface UserActionListener {

    public void userSwitchedTo(WorkflowTransition statusTransition);
}