package com.client;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.common.GatewayInt;
import com.utils.Utils;


public class Client {

    String rmiAddress;

    public Client() {
        this.rmiAddress = Utils.readProperties(this, "rmiAddress", "localhost");
    }


    public static void main(String[] args) {
        Client client = new Client();

        try {
            GatewayInt server = (GatewayInt) Naming.lookup("rmi://" + client.rmiAddress +"/gateway");

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
                String message = scanner.nextLine();
                switch (message) {
                    case "3":
                        System.out.println("Goodbye");
                        break label;
                    case "1":
                        System.out.print("Enter the search query: ");
                        message = scanner.nextLine();
                        server.search(message);
                        break;
                    case "2":
                        System.out.print("Enter the URL: ");
                        message = scanner.nextLine();
                        server.indexURL(message);
                        break;
                    default:
                        System.out.println("Invalid option");
                        break;
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
