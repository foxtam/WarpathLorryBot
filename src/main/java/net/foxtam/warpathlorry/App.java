package net.foxtam.warpathlorry;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static net.foxtam.foxclicker.GlobalLogger.enter;
import static net.foxtam.foxclicker.GlobalLogger.exit;

public class App {
    public static void main(String[] args) {
        enter((Object[]) args);
        try {
            SwingUtilities.invokeAndWait(SwingApp::new);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        exit();
    }

    public static Version getAppCurrentVersion() {
        try {
            Properties properties = new Properties();
            String propertiesFile = "project.properties";
            InputStream resourceAsStream = App.class.getResourceAsStream("/" + propertiesFile);
            if (resourceAsStream == null) throw new RuntimeException("Unable to read file: " + propertiesFile);
            properties.load(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
            return new Version(properties.getProperty("version"));
        } catch (IOException e) {
            showErrorMessage("Unable to read file with application current version. " + e);
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
