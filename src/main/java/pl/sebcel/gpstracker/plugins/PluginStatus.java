package pl.sebcel.gpstracker.plugins;

import pl.sebcel.gpstracker.AppColor;

public class PluginStatus {

    public static PluginStatus UNINITIALIZED = new PluginStatus(0, "Uninitialized", new AppColor(255, 255, 255));
    public static PluginStatus READY = new PluginStatus(1, "Ready", new AppColor(255, 255, 0));
    public static PluginStatus OK = new PluginStatus(2, "OK", new AppColor(0, 200, 0));
    public static PluginStatus ERROR = new PluginStatus(3, "ERROR", new AppColor(255, 0, 0));

    private int id;
    private String displayName;
    private AppColor color;

    private PluginStatus(int id, String displayName, AppColor color) {
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
        return this.id == ((PluginStatus) obj).id;
    }
}