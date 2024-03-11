import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            GatewayInt server = (GatewayInt) Naming.lookup("rmi://localhost/gateway");

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
                    > """);
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
                    message = "index " + scanner.nextLine();
                    server.indexURL(message);
                } else {
                    System.out.println("Invalid option");
                    continue;
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
