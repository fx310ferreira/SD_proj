package com.utils;

import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static  String readRMIAddress(Object obj) {
        final String propertiesFile = "config.properties";
        String defaultAddress = "localhost";

        try (InputStream input = obj.getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null){
                System.out.println("Unable to find " + propertiesFile + " defaulting to: " + defaultAddress);
                return defaultAddress;
            }

            Properties prop = new Properties();
            prop.load(input);

            if (prop.getProperty("rmiAddress") != null) {
                System.out.println("Using address: " + prop.getProperty("rmiAddress"));
                return prop.getProperty("rmiAddress");
            }else {
                System.out.println("Unable to find property defaulting to: " + defaultAddress);
                return defaultAddress;
            }

        } catch (Exception e) {
            System.err.println("Error reading " + propertiesFile + " defaulting to: " + defaultAddress);
            return defaultAddress;
        }
    }

}
