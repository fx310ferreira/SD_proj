package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BarrelInt extends Remote {
    public boolean indexedUrl(String url) throws RemoteException;
}
