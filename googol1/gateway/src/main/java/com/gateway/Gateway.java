package com.gateway;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.common.GatewayInt;

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    Dispatcher dispatcher;

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
            java.rmi.Naming.rebind("dispatcher", gateway.dispatcher);
            System.out.println("Gateway is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
