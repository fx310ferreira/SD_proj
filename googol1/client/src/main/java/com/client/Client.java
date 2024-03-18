package com.client;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Scanner;

import com.common.GatewayInt;


public class Client {

    String rmiAddress;

    public Client() {
        this.rmiAddress = readRMIAddress();
    }

    String readRMIAddress() {
        final String propertiesFile = "config.properties";
        String defaultAddress = "localhost";

        try (InputStream input = Client.class.getClassLoader().getResourceAsStream(propertiesFile)) {
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
            System.out.println("Error reading " + propertiesFile + " defaulting to: " + defaultAddress);
            return defaultAddress;
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        try {
            GatewayInt server = (GatewayInt) Naming.lookup("rmi://" + client.rmiAddress +"/gateway");

            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                                --------------------------
                                Welcome to Googol""");
            while (true) {
                System.out.print("""
                    --------------------------
                    1 - Search
                    2 - Index a URL
                    3 - Exit
                    --------------------------
                    >""");
                String message = scanner.nextLine();
                if (message.equals("3")) {
                    System.out.println("Goodbye");
                    break;
                } else if(message.equals("1")) {
                    System.out.print("Enter the search query: ");
                    message = scanner.nextLine();
                    server.search(message);
                } else if(message.equals("2")) {
                    System.out.print("Enter the URL: ");
                    message = scanner.nextLine();
                    server.indexURL(message);
                } else {
                    System.out.println("Invalid option");
                }
            }
            scanner.close();
        }
        catch (RemoteException e) {
            System.out.println("Gateway is down please ty again later");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
