package pl.sebcel.gpstracker.workflow;

import pl.sebcel.gpstracker.state.AppStatus;

public class StatusTransition {

    private int id;
    private AppStatus targetStatus;
    private String name;

    public StatusTransition(int id, String name, AppStatus targetStatus) {
        this.id = id;
        this.name = name;
        this.targetStatus = targetStatus;
    }

    public AppStatus getTargetStatus() {
        return targetStatus;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        return id == ((StatusTransition) o).id;
    }
}