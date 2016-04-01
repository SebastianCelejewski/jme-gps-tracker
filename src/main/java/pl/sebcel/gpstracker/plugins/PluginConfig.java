package pl.sebcel.gpstracker.plugins;

import java.util.Enumeration;
import java.util.Hashtable;

public class PluginConfig {

    private String pluginName;
    private Hashtable configSettings = new Hashtable();

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public void setConfigurationKeys(String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            configSettings.put(keys[i], "");
        }
    }

    public String[] getConfigurationKeys() {
        String[] result = new String[configSettings.size()];
        Enumeration keys = configSettings.keys();
        for (int i = 0; i < configSettings.size(); i++) {
            result[i] = (String) keys.nextElement();
        }
        return result;
    }
    
    public void setValue(String key, String value) {
        configSettings.put(key,  value);
    }
    
    public String getValue(String key) {
        return (String) configSettings.get(key);
    }
}