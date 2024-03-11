import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    Dispatcher dispatcher;

    protected Gateway() throws RemoteException {
        super();
        dispatcher = new Dispatcher();
    }


    @Override
    public void indexURL(String url) throws RemoteException {
        System.out.println("Indexing " + url);
        dispatcher.push(url);
        System.out.println(dispatcher);
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
