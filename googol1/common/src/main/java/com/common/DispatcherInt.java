package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote interface for Dispatcher objects.
 * Specifies the methods that can be invoked remotely on a Dispatcher object.
 */
public interface DispatcherInt extends Remote {

    /**
     * Pushes a URL to the Dispatcher for further processing.
     *
     * @param url The URL to be pushed for processing.
     * @throws RemoteException If a remote communication error occurs.
     */
    void push(String url) throws RemoteException;

    /**
     * Pushes a URL to the front of the processing queue.
     *
     * @param url The URL to be pushed to the front of the queue.
     * @throws RemoteException If a remote communication error occurs.
     */
    void pushFront(String url) throws RemoteException;

    /**
     * Retrieves and removes the URL from the processing queue.
     *
     * @return The URL retrieved from the queue.
     * @throws RemoteException If a remote communication error occurs.
     */
    String pop() throws RemoteException;

    /**
     * Notifies the Dispatcher that processing for a URL is finished.
     *
     * @param url The URL for which processing is completed.
     * @throws RemoteException If a remote communication error occurs.
     */
    void finishedProcessing(String url) throws RemoteException;

    /**
     * Checks if a URL is already indexed.
     *
     * @param url The URL to check for indexing.
     * @return true if the URL is indexed, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    boolean indexedUrl(String url) throws RemoteException;

    /**
     * Retrieves the ID of the Dispatcher.
     *
     * @return The ID of the Dispatcher.
     * @throws RemoteException If a remote communication error occurs.
     */
    long getId() throws RemoteException;
}
