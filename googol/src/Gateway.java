import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    Queue<String> url_queue;

    protected Gateway() throws RemoteException {
        super();
        url_queue = new LinkedList<>();
    }


    @Override
    public void indexURL(String url) throws RemoteException {
        System.out.println("Indexing " + url);
        url_queue.add(message);
        System.out.println(url_queue);
    }

    @Override
    public void search(String query) throws RemoteException {
        System.out.println("Searching for " + query);
    }

    public static void main(String[] args) {
        try {
            GatewayInt gateway = new Gateway();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("gateway", gateway);
            System.out.println("Gateway is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
