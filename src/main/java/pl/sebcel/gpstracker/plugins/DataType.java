package pl.sebcel.gpstracker.plugins;

public class DataType {

    public static final DataType TEXT = new DataType("text");
    public static final DataType HIDDEN = new DataType("hidden");

    private String id;

    private DataType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}