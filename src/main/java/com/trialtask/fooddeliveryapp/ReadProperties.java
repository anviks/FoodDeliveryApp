package com.trialtask.fooddeliveryapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ReadProperties {
    private static Properties properties = null;

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
