package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote interface for Barrel objects.
 * Specifies the methods that can be invoked remotely on a Barrel.
 */
public interface BarrelInt extends Remote {

    /**
     * Checks if a URL has been indexed by the Barrel.
     *
     * @param url The URL to check.
     * @return True if the URL is indexed, false otherwise.
     * @throws RemoteException If a remote error occurs.
     */
    boolean indexedUrl(String url) throws RemoteException;

    /**
     * Searches for sites containing the specified words.
     *
     * @param words The words to search for.
     * @param page  The page number of results to retrieve.
     * @return An array of Site objects matching the search criteria.
     * @throws RemoteException If a remote error occurs.
     */
    Site[] search(String[] words, int page) throws RemoteException;

    /**
     * Retrieves the linked pages for a given URL.
     *
     * @param url The URL for which linked pages are requested.
     * @return An array of Site objects representing linked pages.
     * @throws RemoteException If a remote error occurs.
     */
    Site[] linkedPages(String url) throws RemoteException;

    /**
     * Notifies the Barrel that it is still alive.
     *
     * @throws RemoteException If a remote error occurs.
     */
    void alive() throws RemoteException;
}
