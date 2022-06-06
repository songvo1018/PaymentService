package org.nosov;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties {
    private static ConfigProperties instance = null;
    private Properties properties;
    public static final String FILENAME = "src/main/resources/application.config";

    public Logger getLOGGER() {
        return LOGGER;
    }

    private final Logger LOGGER = Logger.getLogger("[Payment]");
    private ConfigProperties () {
        if (!this.readConfigFile()) {
            System.exit(0); //Catastrophic
        }
    }

    public static ConfigProperties getInstance() {
        if (instance == null) {
            return new ConfigProperties();
        }
        return ConfigProperties.instance;
    }
    public boolean readConfigFile() {
        FileInputStream fis;

        try {
            fis = new FileInputStream(FILENAME);
            properties = new Properties();
            properties.load(fis);
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
        return true;
    }

    public Properties getProperties() {
        return properties;
    }
}
