package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BarrelInt extends Remote {
    boolean indexedUrl(String url) throws RemoteException;
    void alive() throws RemoteException;
    Site search(String[] words) throws RemoteException;
}
