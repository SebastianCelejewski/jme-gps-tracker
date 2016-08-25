package pl.sebcel.gpstracker.gui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import pl.sebcel.gpstracker.ConfigurationProvider;
import pl.sebcel.gpstracker.plugins.DataType;
import pl.sebcel.gpstracker.plugins.PluginConfig;

public class ConfigurationView extends Form {

    private PluginConfig[] pluginConfigs;
    private Vector textFields = new Vector();
    private ApplicationView applicationView;
    private ConfigurationProvider configurationProvider;
    private Command saveConfigurationCommand = new Command("Save", Command.SCREEN, 1);
    private Command cancelCommand = new Command("Cancel", Command.SCREEN, 2);

    public ConfigurationView(final Display display) {
        super("Configuration");

        this.addCommand(saveConfigurationCommand);
        this.addCommand(cancelCommand);
        this.setCommandListener(new CommandListener() {

            public void commandAction(Command command, Displayable source) {
                if (command == saveConfigurationCommand) {
                    updatePluginConfigs();
                    configurationProvider.updateStorage();
                    display.setCurrent(applicationView);
                    return;
                }
                if (command == cancelCommand) {
                    display.setCurrent(applicationView);
                    return;
                }
            }
        });
    }

    public void setApplicationView(ApplicationView applicationView) {
        this.applicationView = applicationView;
    }

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    public void setPluginConfigs(PluginConfig[] pluginConfigs) {
        this.pluginConfigs = pluginConfigs;
        renderPluginConfigs();
    }

    private void renderPluginConfigs() {
        this.deleteAll();
        for (int j = 0; j < pluginConfigs.length; j++) {
            PluginConfig pluginConfig = pluginConfigs[j];
            this.append(pluginConfig.getPluginName());
            String[] configurationKeys = pluginConfig.getConfigurationKeys();
            for (int i = 0; i < configurationKeys.length; i++) {
                String key = configurationKeys[i];
                String value = pluginConfig.getValue(key);
                DataType type = pluginConfig.getType(key);
                if (type == DataType.TEXT) {
                    TextField textField = new TextField(key, "", 32, TextField.ANY);
                    textField.setString(value);
                    textFields.addElement(textField);
                    this.append(textField);
                } else if (type == DataType.HIDDEN) {
                    TextField textField = new TextField(key, "", 32, TextField.PASSWORD);
                    textField.setString(value);
                    textFields.addElement(textField);
                    this.append(textField);
                }
            }
        }
    }

    private void updatePluginConfigs() {
        int idx = 0;
        for (int j = 0; j < pluginConfigs.length; j++) {
            PluginConfig pluginConfig = pluginConfigs[j];
            String[] configurationKeys = pluginConfig.getConfigurationKeys();
            for (int i = 0; i < configurationKeys.length; i++) {
                TextField textField = (TextField) textFields.elementAt(idx++);
                String key = configurationKeys[i];
                String value = textField.getString();
                System.out.println("Setting " + key + "=" + value);
                pluginConfig.setValue(key, value);
            }
        }
    }
}