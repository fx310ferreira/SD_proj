package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
    ArrayList<BarrelInt> barrels;

    GatewayBarrel() throws RemoteException {
        super();
        this.barrels = new ArrayList<>();
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
    public void subscribe(BarrelInt barrel) throws RemoteException {
        barrels.add(barrel);
    }
}
