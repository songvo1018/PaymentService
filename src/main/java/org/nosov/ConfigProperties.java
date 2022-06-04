package org.nosov;

import com.mongodb.client.model.Filters;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class ConfigProperties {
    private static ConfigProperties instance = null;
    private Properties property;
    public static final String FILENAME = "src/main/resources/application.config";
    private static final Logger LOGGER = Logger.getLogger(ConfigProperties.class.getName());
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
            property = new Properties();
            property.load(fis);
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
        return true;
    }

    public Properties getProperty() {
        return property;
    }
}
