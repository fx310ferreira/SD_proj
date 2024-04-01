package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatewayBarrelInt extends Remote {
    boolean subscribe(BarrelInt barrel, String barrelId) throws RemoteException;
    boolean indexedUrl(String message) throws RemoteException;
}
