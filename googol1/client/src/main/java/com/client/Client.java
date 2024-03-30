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
    GatewayInt server;

    public Client() throws MalformedURLException, NotBoundException, RemoteException {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.server = (GatewayInt) Naming.lookup("rmi://" + this.RMI_ADDRESS +"/gateway");
    }

    public void linkedPages(Site[] url, Scanner scanner) throws RemoteException {
        while (true)
            try {
                System.out.print("Enter the number of the site you want to see the linked pages\n->");
                int index = Integer.parseInt(scanner.nextLine());
                if (index < 1 || index > url.length) {
                    System.out.println("Invalid number");
                    return;
                }
                Site[] sites = server.linkedPages(url[index - 1].getLink());
                if (sites.length == 0) {
                    System.out.println("No linked pages found");
                    return;
                }
                System.out.print("""
                    --------------------------
                    Linked pages
                    """);
                for (var site : sites) {
                    System.out.println(site);
                }
                scanner.nextLine();
                return;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }

    }

    public void searchMenu(String message, Scanner scanner) throws RemoteException {
        int page = 0;
        String comand;
        while (true){
            Site[] sites = server.search(message, page);

            System.out.print("""
                        --------------------------
                        Results
                        """);
            int i = 1;
            for (var site : sites) {
                System.out.println(i++ + ":" + site);
            }
            if(sites.length == 0){
                System.out.println("No sites were found");
                return;
            }
            System.out.println("--------------------------");
            if (sites.length < 10 && page == 0){
                System.out.print("""
                        1 - Exit
                        2 - Linked pages
                        --------------------------
                        ->""");
                comand = scanner.nextLine();
                switch (comand) {
                    case "1" -> {return;}
                    case "2" -> linkedPages(sites, scanner);
                }
            } else if (sites.length < 10 && page > 0) {
                System.out.print("""
                        1 - Previous page
                        2 - Exit
                        3 - Linked pages
                        --------------------------
                        ->""");
                comand = scanner.nextLine();
                switch (comand) {
                    case "1" -> page--;
                    case "2" -> {return;}
                    case "3" -> linkedPages(sites, scanner);
                }
            } else if (sites.length >= 10 && page == 0) {
                System.out.print("""
                        1 - Next page
                        2 - Exit
                        3 - Linked pages
                        --------------------------
                        ->""");
                comand = scanner.nextLine();
                switch (comand) {
                    case "1" -> page++;
                    case "2" -> {return;}
                    case "3" -> linkedPages(sites, scanner);
                }
            } else {
                System.out.print("""
                        1 - Previous page
                        2 - Next page
                        3 - Exit
                        4 - Linked pages
                        --------------------------
                        ->""");
                comand = scanner.nextLine();
                switch (comand) {
                    case "1" -> page--;
                    case "2" -> page++;
                    case "3" -> {return;}
                    case "4" -> linkedPages(sites, scanner);
                }
            }
        }
    }

    public static void main(String[] args) {

        try {
            Client client = new Client();
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
                    ->""");
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
                        client.searchMenu(message, scanner);
                        break;
                    case "2":
                        System.out.print("Enter the URL: ");
                        message = scanner.nextLine().strip();
                        if((message.startsWith("http://") || message.startsWith("https://"))){
                            if (!client.server.indexURL(message)) {
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
