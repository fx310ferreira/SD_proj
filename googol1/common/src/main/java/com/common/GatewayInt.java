package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GatewayInt extends Remote {
    boolean indexURL(String url) throws RemoteException;
    Site[] search(String query, int page) throws RemoteException;
    Site[] linkedPages(String url) throws RemoteException;
    Set<String> getBarrelIds() throws RemoteException;
    Map<String, List<Double>> getResponseTimes() throws RemoteException;
}
