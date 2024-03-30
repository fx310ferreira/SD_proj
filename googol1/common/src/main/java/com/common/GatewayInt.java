package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatewayInt extends Remote {
    boolean indexURL(String url) throws RemoteException;
    Site[] search(String query, int page) throws RemoteException;
    Site[] linkedPages(String url) throws RemoteException;
}
