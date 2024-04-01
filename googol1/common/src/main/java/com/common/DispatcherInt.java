package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInt extends Remote {
    void push(String url) throws RemoteException;

    String pop() throws RemoteException;

    void finishedProcessing(String url) throws RemoteException;

    boolean indexedUrl(String url) throws RemoteException;

    long getId() throws RemoteException;
}
