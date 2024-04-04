package com.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.common.ClientInt;
import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;

public class Client extends UnicastRemoteObject implements ClientInt {

    String RMI_ADDRESS;
    GatewayInt server;

    public Client() throws MalformedURLException, NotBoundException, RemoteException {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.server = (GatewayInt) Naming.lookup("rmi://" + this.RMI_ADDRESS + "/gateway");
        server.addClient(this);
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
        while (true) {
            Site[] sites = server.search(message, page);

            System.out.print("""
                    --------------------------
                    Results
                    """);
            int i = 1;
            for (var site : sites) {
                System.out.println(i++ + ":" + site);
            }
            if (sites.length == 0) {
                System.out.println("No sites were found");
                return;
            }
            System.out.println("--------------------------");
            if (sites.length < 10 && page == 0) {
                System.out.print("""
                        1 - Exit
                        2 - Linked pages
                        --------------------------
                        ->""");
                comand = scanner.nextLine();
                switch (comand) {
                    case "1" -> {
                        return;
                    }
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
                    case "2" -> {
                        return;
                    }
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
                    case "2" -> {
                        return;
                    }
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
                    case "3" -> {
                        return;
                    }
                    case "4" -> linkedPages(sites, scanner);
                }
            }
        }
    }

    @Override
    public void updateStatistics(Set<String> activeBarrels, Map<String, List<Double>> responseTimes, String[] topSearches){
        try {
            // Display active barrels
            System.out.println("--------------------------\nActive barrels:");
            if(activeBarrels.isEmpty()){
                System.out.println("No barrels recorded");
            }
            int count = 0;
            for (String barrelId : activeBarrels) {
                System.out.print(barrelId);
                count++;
                if (count < activeBarrels.size()) {
                    System.out.print(", ");
                }
            }
            System.out.println("\n--------------------------");

            // Display average response time per barrel
            System.out.println("--------------------------\nAverage response time per barrel:");
            if(responseTimes.isEmpty()){
                System.out.println("No response time recorded");
            }
            for (Map.Entry<String, List<Double>> entry : responseTimes.entrySet()) {
                String barrelId = entry.getKey();
                List<Double> times = entry.getValue();

                if (!times.isEmpty()) {
                    double total = 0.0;
                    for (Double time : times) {
                        total += time;
                    }
                    double average = total / times.size();
                    System.out.println(barrelId + ": " + average + " ds");
                } else {
                    System.out.println(barrelId + ": No response time recorded");
                }
            }
            System.out.println("--------------------------");
            if(topSearches.length > 0){
                System.out.println("--------------------------\nTop searches:");
                for (int i = 0; i < topSearches.length; i++) {
                    System.out.println((i + 1) + ": " + topSearches[i]);
                }
            } else {
                System.out.println("No searches recorded");
            }
            System.out.print("--------------------------\n->");
        } catch (Exception e) {
            System.out.println("Error fetching statistics: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        try {
            Client client = new Client();
            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                    --------------------------
                    Welcome to Googol""");
            label: while (true) {
                System.out.print("""
                        --------------------------
                        1 - Search
                        2 - Index a URL
                        3 - Exit
                        --------------------------
                        ->""");
                String message = scanner.nextLine().strip();
                switch (message) {
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
                        if ((message.startsWith("http://") || message.startsWith("https://"))) {
                            if (!client.server.indexURL(message)) {
                                System.out.println("URL already indexed");
                            } else {
                                System.out.println("URL indexed");
                            }
                        } else {
                            System.out.println("Invalid link write an absolute link like: http://google.com");
                        }
                        break;
                    case "3":
                        System.out.println("Goodbye");
                        break label;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }
            scanner.close();
        } catch (RemoteException | NotBoundException | MalformedURLException | RuntimeException e) {
            System.out.println("Gateway is down please try again later: " + e.getMessage());
        }
    }
}
