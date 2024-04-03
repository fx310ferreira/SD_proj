package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.Map;

public interface GatewayInt extends Remote {
    boolean indexURL(String url) throws RemoteException;
    Site[] search(String query, int page) throws RemoteException;
    Site[] linkedPages(String url) throws RemoteException;
    Map<String, List<Double>> getResponseTimes() throws RemoteException;
    public void addClient(ClientInt client) throws RemoteException;
}
