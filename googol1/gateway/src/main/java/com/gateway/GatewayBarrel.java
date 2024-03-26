package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
    ArrayList<BarrelInt> barrels;
    Map<String, BarrelInt> subscribedBarrels;

    GatewayBarrel() throws RemoteException {
        super();
        this.barrels = new ArrayList<>();
        this.subscribedBarrels = new HashMap<>();
    }

    public void message(String message) {
        if(barrels.isEmpty())
            System.out.println("No barrels found");
        else{
            try {
                barrels.get(0).test(message);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
        if (!subscribedBarrels.containsKey(barrelId)) {
            subscribedBarrels.put(barrelId, barrel);
            System.out.println("Barrel with ID " + barrelId + " subscribed.");
        } else {
            System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
        }
    }
}
