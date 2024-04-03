package com.utils;

import java.io.InputStream;
import java.util.Properties;

public class Utils {

        public static  String readProperties(Object obj, String filed, String defaultVal){
            final String propertiesFile = "config.properties";
            return readProperties(obj, filed, defaultVal, propertiesFile);
        }


        public static  String readProperties(Object obj, String filed, String defaultVal, String propertiesFile) {
            try (InputStream input = obj.getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
                if (input == null){
                    System.out.println("Unable to find " + propertiesFile + " defaulting to: " + defaultVal);
                    return defaultVal;
                }

                Properties prop = new Properties();
                prop.load(input);

                if (prop.getProperty(filed) != null) {
                    System.out.println("Using address: " + prop.getProperty(filed));
                    return prop.getProperty(filed);
                }else {
                    System.out.println("Unable to find property defaulting to: " + defaultVal);
                    return defaultVal;
                }

            } catch (Exception e) {
                System.err.println("Error reading " + propertiesFile + " defaulting to: " + defaultVal);
                return defaultVal;
            }
    }

}
