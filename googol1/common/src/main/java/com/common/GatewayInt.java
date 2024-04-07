/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * The GatewayInt interface defines remote methods that can be invoked on the Gateway server from a client.
 * Extends the Remote interface, indicating that objects implementing this interface can be invoked remotely.
 */
public interface GatewayInt extends Remote {

    /**
     * Indexes the given URL for searching.
     *
     * @param url The URL to be indexed.
     * @return true if indexing is successful, false otherwise.
     * @throws RemoteException if a remote error occurs.
     */
    boolean indexURL(String url) throws RemoteException;

    /**
     * Searches for sites based on the given query.
     *
     * @param query The search query.
     * @param page  The page number of the search results.
     * @return An array of Site objects representing the search results.
     * @throws RemoteException if a remote error occurs.
     */
    Site[] search(String query, int page) throws RemoteException;

    /**
     * Retrieves linked pages for a given URL.
     *
     * @param url The URL for which linked pages are to be retrieved.
     * @return An array of Site objects representing the linked pages.
     * @throws RemoteException if a remote error occurs.
     */
    Site[] linkedPages(String url) throws RemoteException;

    /**
     * Retrieves the response times of the Gateway's barrels.
     *
     * @return A Map containing the response times of the Gateway's barrels.
     * @throws RemoteException if a remote error occurs.
     */
    Map<String, List<Double>> getResponseTimes() throws RemoteException;

    /**
     * Adds a client to receive updates from the Gateway.
     *
     * @param client The client to be added.
     * @throws RemoteException if a remote error occurs.
     */
    void addClient(ClientInt client) throws RemoteException;
}
