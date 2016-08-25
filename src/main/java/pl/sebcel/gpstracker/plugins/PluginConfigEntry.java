package pl.sebcel.gpstracker.plugins;

public class PluginConfigEntry {

    private String id;
    private DataType type;
    private String value;

    public PluginConfigEntry(String id, DataType type) {
        this.id = id;
        this.type = type;
        this.value = "";
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public DataType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}