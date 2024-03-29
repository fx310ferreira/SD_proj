package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BarrelInt extends Remote {
    boolean indexedUrl(String url) throws RemoteException;
    Site search(String[] words) throws RemoteException;
    boolean alive() throws RemoteException;
}
