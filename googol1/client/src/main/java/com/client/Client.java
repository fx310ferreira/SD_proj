package com.client;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;


public class Client {

    String RMI_ADDRESS;

    public Client() {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
    }


    public static void main(String[] args) {
        Client client = new Client();

        try {
            GatewayInt server = (GatewayInt) Naming.lookup("rmi://" + client.RMI_ADDRESS +"/gateway");

            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                                --------------------------
                                Welcome to Googol""");
            label:
            while (true) {
                System.out.print("""
                    --------------------------
                    1 - Search
                    2 - Index a URL
                    3 - Exit
                    --------------------------
                    >""");
                String message = scanner.nextLine().strip();
                switch (message) {
                    case "3":
                        System.out.println("Goodbye");
                        break label;
                    case "1":
                        System.out.print("Enter the search query: ");
                        message = scanner.nextLine().strip();
                        if (message.isBlank()) {
                            System.out.println("Invalid search query");
                            break;
                        }
                        Site[] sites = server.search(message);
                        if (sites.length == 0) {
                            System.out.println("No results found");
                        }
                        for (var site : sites) {
                            System.out.println(site);
                        }
                        break;
                    case "2":
                        System.out.print("Enter the URL: ");
                        message = scanner.nextLine().strip();
                        if((message.startsWith("http://") || message.startsWith("https://"))){
                            if (!server.indexURL(message)) {
                                System.out.println("URL already indexed");
                            } else {
                                System.out.println("URL indexed");
                            }
                        } else {
                            System.out.println("Invalid link write an absolute link like: http://google.com");
                        }
                        break;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }
            scanner.close();
        }
        catch (RemoteException | NotBoundException | MalformedURLException | RuntimeException e) {
            System.out.println("Gateway is down please try again later: " + e.getMessage());
        }
    }
}
