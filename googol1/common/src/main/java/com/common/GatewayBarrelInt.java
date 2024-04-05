package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote interface for subscribing barrels and checking indexed URLs in a gateway.
 */
public interface GatewayBarrelInt extends Remote {
    /**
     * Subscribes a barrel to the gateway.
     *
     * @param barrel   the barrel to subscribe
     * @param barrelId the ID of the barrel
     * @return true if the subscription is successful; false otherwise
     * @throws RemoteException if there is a communication-related issue
     */
    boolean subscribe(BarrelInt barrel, String barrelId) throws RemoteException;

    /**
     * Checks if the URL has been indexed in any of the barrels.
     *
     * @param message the URL to check for indexing
     * @return true if the URL is indexed; false otherwise
     * @throws RemoteException if there is a communication-related issue
     */
    boolean indexedUrl(String message) throws RemoteException;
}
