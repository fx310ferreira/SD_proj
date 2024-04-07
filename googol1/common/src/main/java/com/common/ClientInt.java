/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An interface defining methods for updating statistics on the client side.
 */
public interface ClientInt extends Remote {
    /**
     * Updates statistics displayed to the client.
     * 
     * @param activeBarrels    the set of active barrels.
     * @param responseTimes    the map of response times for each barrel.
     * @param topSearches      the array of top search queries.
     * @throws RemoteException if there is an error updating the client remotely.
     */
    void updateStatistics(Set<String> activeBarrels, Map<String, List<Double>> responseTimes, String[] topSearches) throws RemoteException;
}
