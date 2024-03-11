package Client;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import Gateway.GatewayInt;


public class Client {

    public static void main(String[] args) {
        try {
            String rmi_address = "localhost";
            if(args.length != 1){
                System.out.println("WRONG NUMBER OF PARAMS: defaulting to localhost");
            }else{
                rmi_address = args[0];
            }
            GatewayInt server = (GatewayInt) Naming.lookup("rmi://" + rmi_address +"/gateway");

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
