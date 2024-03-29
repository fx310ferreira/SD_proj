package com.gateway;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    GatewayBarrel gatewayBarrel;
    Dispatcher dispatcher;
    int PORT;

    protected Gateway() throws RemoteException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "1099"));
        this.gatewayBarrel = new GatewayBarrel();
        this.dispatcher = new Dispatcher(this.gatewayBarrel);
    }


    @Override
    public boolean indexURL(String url) throws RemoteException {
        if (dispatcher.indexedUrl(url)) {
            System.out.println("Already indexed: " + url);
            return false;
        }
        System.out.println("Indexing " + url);
        dispatcher.push(url);
        return true;
    }

    @Override
    public Site search(String query) throws RemoteException {
        System.out.println("Searching for " + query);
        return gatewayBarrel.search(query.split(" "));
    }

    public static void main(String[] args) {
        try {
            Gateway gateway = new Gateway();
            java.rmi.registry.LocateRegistry.createRegistry(gateway.PORT);
            java.rmi.Naming.rebind("gateway", gateway);
            java.rmi.Naming.rebind("dispatcher", gateway.dispatcher);
            java.rmi.Naming.rebind("barrels", gateway.gatewayBarrel);
            System.out.println("Gateway is running...");

        } catch (RemoteException e) {
            System.err.println("Failed creating registry: " + e.getMessage());
        } catch (MalformedURLException e){
            System.out.println("Rebind url is malformed: " + e.getMessage());
        }
    }
}
