package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInt extends Remote {
    void push(String url) throws RemoteException;
    String pop() throws RemoteException;
}
