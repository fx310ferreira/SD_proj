import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatewayInt extends Remote {
    void indexURL(String url) throws RemoteException;
    void search(String query) throws RemoteException;
}
