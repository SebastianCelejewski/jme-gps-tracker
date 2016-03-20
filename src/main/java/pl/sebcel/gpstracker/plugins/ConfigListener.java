package pl.sebcel.gpstracker.plugins;

public interface ConfigListener {

    public PluginConfig getPluginConfig();

    public void onConfigUpdated(PluginConfig pluginConfig);

}