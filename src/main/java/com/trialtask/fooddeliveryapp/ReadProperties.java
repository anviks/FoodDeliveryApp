package com.trialtask.fooddeliveryapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * A utility class that reads properties from the "application.properties" file located in the "resources" folder.
 */
public class ReadProperties {
    private static Properties properties = null;

    /**
     * This method returns the application properties read from the file located at "./src/main/resources/application.properties".
     * If the properties have not been loaded yet, they will be loaded into a Properties object and returned.
     * If an IOException occurs during loading, the stack trace will be printed and an empty Properties object will be returned.
     *
     * @return a {@link Properties} object containing the properties from the file.
     */
    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();

            try (FileInputStream stream = new FileInputStream("./src/main/resources/application.properties")) {
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                properties.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }
}
