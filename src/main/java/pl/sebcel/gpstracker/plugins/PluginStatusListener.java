package pl.sebcel.gpstracker.plugins;

public interface PluginStatusListener {
    public void pluginStatusChanged(String pluginId, PluginStatus status);
}