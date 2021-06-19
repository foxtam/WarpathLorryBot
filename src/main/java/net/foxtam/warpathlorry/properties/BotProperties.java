package net.foxtam.warpathlorry.properties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class BotProperties {

    private static final Path propertiesPath = Path.of("config.properties");

    public static String getServerIP() {
        return getProperty("server_ip");
    }

    private static String getProperty(String key) {
        try {
            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(propertiesPath));
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new CantReadPropertiesException("Can't read properties file: " + propertiesPath, e);
        }
    }

    public static int getServerPort() {
        return Integer.parseInt(getProperty("server_port"));
    }
}
