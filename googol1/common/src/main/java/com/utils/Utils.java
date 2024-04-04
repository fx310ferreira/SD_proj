package com.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read properties from a file.
 */
public class Utils {

    /**
     * Reads a property from the default properties file.
     *
     * @param obj        The object used to access the class loader.
     * @param field      The name of the property to read.
     * @param defaultVal The default value to return if the property is not found.
     * @return           The value of the property if found, otherwise the default value.
     */
    public static  String readProperties(Object obj, String filed, String defaultVal){
        final String propertiesFile = "config.properties";
        return readProperties(obj, filed, defaultVal, propertiesFile);
    }

    /**
     * Reads a property from a specified properties file.
     *
     * @param obj            The object used to access the class loader.
     * @param field          The name of the property to read.
     * @param defaultVal     The default value to return if the property is not found.
     * @param propertiesFile The name of the properties file to read from.
     * @return               The value of the property if found, otherwise the default value.
     */
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
