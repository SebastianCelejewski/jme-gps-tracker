package pl.sebcel.gpstracker.events;

import pl.sebcel.gpstracker.workflow.StatusTransition;

public interface UserActionListener {
    
    public void userSwitchedTo(StatusTransition statusTransition);
    
}