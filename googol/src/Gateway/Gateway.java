package Gateway;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    Dispatcher dispatcher;
    int downloaderNumber = 5;

    protected Gateway() throws RemoteException {
        super();
        this.dispatcher = new Dispatcher();
    }


    @Override
    public void indexURL(String url) throws RemoteException {
        System.out.println("Indexing " + url);
        dispatcher.push(url);
    }

    @Override
    public void search(String query) throws RemoteException {
        System.out.println("Searching for " + query);
    }

    public static void main(String[] args) {
        try {
            Gateway gateway = new Gateway();
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            java.rmi.Naming.rebind("gateway", gateway);
            System.out.println("Gateway is running...");

            for (int i = 0; i < gateway.downloaderNumber; i++) {
                new Downloader(gateway.dispatcher, i).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
