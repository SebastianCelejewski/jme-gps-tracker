package pl.sebcel.gpstracker.events;

import pl.sebcel.gpstracker.StatusTransition;

public interface UserActionListener {
    
    public void userSwitchedTo(StatusTransition statusTransition);
    
}