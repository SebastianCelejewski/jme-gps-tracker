package pl.sebcel.gpstracker.state;

import pl.sebcel.gpstracker.AppColor;

public class AppStatus {

    public static AppStatus UNINITIALIZED = new AppStatus(0, "Uninitialized", new AppColor(255, 255, 255));
    public static AppStatus READY = new AppStatus(1, "Ready", new AppColor(255, 255, 0));
    public static AppStatus STARTED = new AppStatus(2, "Started", new AppColor(0, 255, 0));
    public static AppStatus STOPPING = new AppStatus(3, "Stopping", new AppColor(0, 0, 200));
    public static AppStatus STOPPED = new AppStatus(4, "Stopped", new AppColor(0, 0, 255));
    public static AppStatus PAUSED = new AppStatus(5, "Paused", new AppColor(255, 128, 0));

    private int id;
    private String displayName;
    private AppColor color;

    private AppStatus(int id, String displayName, AppColor color) {
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
        return this.id == ((AppStatus) obj).id;
    }
}