package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BarrelInt extends Remote {
    boolean indexedUrl(String url) throws RemoteException;
    Site[] search(String[] words, int page) throws RemoteException;
    Site[] linkedPages(String url) throws RemoteException;
    void alive() throws RemoteException;
}
