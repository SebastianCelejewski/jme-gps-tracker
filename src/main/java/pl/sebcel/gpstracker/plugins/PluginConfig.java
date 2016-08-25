package pl.sebcel.gpstracker.plugins;

import java.util.Hashtable;
import java.util.Vector;

public class PluginConfig {

    private String pluginName;
    private Vector entryIds = new Vector();
    private Hashtable configSettings = new Hashtable();

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public void addConfigurationEntry(PluginConfigEntry entry) {
        entryIds.addElement(entry.getId());
        configSettings.put(entry.getId(), entry);
    }

    public String[] getConfigurationKeys() {
        String[] result = new String[configSettings.size()];
        for (int i = 0; i < entryIds.size(); i++) {
            result[i] = (String) entryIds.elementAt(i);
        }
        return result;
    }

    public void setValue(String key, String value) {
        ((PluginConfigEntry) configSettings.get(key)).setValue(value);
    }

    public String getValue(String key) {
        return ((PluginConfigEntry) configSettings.get(key)).getValue();
    }

    public DataType getType(String key) {
        return ((PluginConfigEntry) configSettings.get(key)).getType();
    }
}