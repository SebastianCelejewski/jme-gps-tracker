package pl.sebcel.gpstracker;

public class GpsTrackerConfiguration {

    private int saveInterval;

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public String toString() {
        return "SaveInterval:" + saveInterval;
    }
}