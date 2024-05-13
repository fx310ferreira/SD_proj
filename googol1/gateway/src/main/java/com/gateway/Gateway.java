/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.gateway;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.common.ClientInt;
import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Gateway that serves as an interface between clients and the search system.
 * Implements the GatewayInt interface.
 */
public class Gateway extends UnicastRemoteObject implements GatewayInt {

    int PORT;
    GatewayBarrel gatewayBarrel;
    Dispatcher dispatcher;
    private final HashSet<ClientInt> clients;
    private final HashMap<String, Integer> searches;

    /**
     * Constructor for the Gateway class.
     * 
     * @throws RemoteException if there is an error creating the Gateway.
     */
    protected Gateway() throws RemoteException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "1099"));
        this.gatewayBarrel = new GatewayBarrel(this);
        this.dispatcher = new Dispatcher(this.gatewayBarrel);
        this.clients = new HashSet<>();
        this.searches = new HashMap<>();
    }

    /**
     * Indexes a URL.
     * 
     * @param url the URL to be indexed.
     * @return true if the URL was indexed, false otherwise.
     * @throws RemoteException if there is an error indexing the URL.
     */
    @Override
    public boolean indexURL(String url) throws RemoteException {
        System.out.println("Trying to index url: " + url);
        if (dispatcher.indexedUrl(url)) {
            System.out.println("Already indexed: " + url);
            return false;
        }
        System.out.println("Indexing " + url);
        dispatcher.pushFront(url);
        return true;
    }

    /**
     * Searches for a query.
     * 
     * @param query the query to search for.
     * @param page the page number to search for.
     * @return an array of Sites that match the query.
     * @throws RemoteException if there is an error searching for the query.
     */
    @Override
    public Site[] search(String query, int page) throws RemoteException {
        System.out.println("Searching for " + query);
        Site[] response = gatewayBarrel.search(query.split(" "), page);
        if(searches.containsKey(query)){
            searches.put(query, searches.get(query) + 1);
        } else {
            searches.put(query, 1);
        }
        this.sendUpdatedStatistics();
        return response;
    }

    /**
     * Retrieves linked pages for a URL.
     * 
     * @param url the URL to retrieve linked pages for.
     * @return an array of Sites that are linked to the URL.
     * @throws RemoteException if there is an error retrieving linked pages.
     */
    @Override
    public Site[] linkedPages(String url) throws RemoteException {
        return gatewayBarrel.linkedPages(url);
    }

    /**
     * Retrieves the response times for the Gateway's barrels.
     *
     * @return A Map containing the response times for the Gateway's barrels.
     * @throws RemoteException if there is an error retrieving response times.
     */
    @Override
    public Map<String, List<Double>> getResponseTimes() throws RemoteException {
        return gatewayBarrel.getResponseTimes();
    }
    
    /**
     * Adds a client to the Gateway.
     * 
     * @param client the client to add.
     */
    @Override
    public void addClient(ClientInt client) {
        clients.add(client);
        try {
            client.updateStatistics(gatewayBarrel.getBarrels(), getResponseTimes(), getTopSearches());
        } catch (RemoteException e) {
            System.err.println("Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Retrieves the top search queries from the Gateway.
     * 
     * @return an array of top search queries.
     */
    public String[] getTopSearches() {
        return searches.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .map(Map.Entry::getKey)
            .toArray(String[]::new);
    }

    /**
     * Sends updated statistics to all connected clients.
     */
    public void sendUpdatedStatistics() {
        try {
            Set<String> activeBarrels = gatewayBarrel.getBarrels();
            Map<String, List<Double>> responseTimes = getResponseTimes();
    
            for (ClientInt client : clients) {
                try {
                    client.updateStatistics(activeBarrels, responseTimes, getTopSearches());
                } catch (RemoteException e) {
                    System.err.println("Client is not reachable: " + e.getMessage());
                    clients.remove(client);
                }
            }
        } catch (RemoteException e) {
            System.err.println("Error fetching statistics: " + e.getMessage());
        }
    }

    /**
     * Main method for the Gateway class.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            Gateway gateway = new Gateway();
            java.rmi.registry.LocateRegistry.createRegistry(gateway.PORT);
            java.rmi.Naming.rebind("gateway", gateway);
            java.rmi.Naming.rebind("dispatcher", gateway.dispatcher);
            java.rmi.Naming.rebind("barrels", gateway.gatewayBarrel);
            System.out.println("Gateway is running...");
        } catch (RemoteException e) {
            System.err.println("Failed creating registry: " + e.getMessage());
            System.exit(0);
        } catch (MalformedURLException e){
            System.out.println("Rebind url is malformed: " + e.getMessage());
            System.exit(0);
        }
    }
}
