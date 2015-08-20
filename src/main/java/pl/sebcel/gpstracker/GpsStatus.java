package pl.sebcel.gpstracker;

public class GpsStatus {

    public static GpsStatus UNINITIALIZED = new GpsStatus(0, "Uninitialized", new AppColor(255, 255, 255, false));
    public static GpsStatus NOT_AVAILABLE = new GpsStatus(1, "Not available", new AppColor(20, 20, 20, false));
    public static GpsStatus LOCATING = new GpsStatus(2, "Locating", new AppColor(255, 0, 0, false));
    public static GpsStatus OK = new GpsStatus(3, "OK", new AppColor(0, 255, 0, false));

    private int id;
    private String displayName;
    private AppColor color;

    private GpsStatus(int id, String displayName, AppColor color) {
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
        return this.id == ((GpsStatus) obj).id;
    }
}
