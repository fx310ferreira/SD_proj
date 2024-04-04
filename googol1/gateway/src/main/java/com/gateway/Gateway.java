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

public class Gateway extends UnicastRemoteObject implements GatewayInt {

    int PORT;
    GatewayBarrel gatewayBarrel;
    Dispatcher dispatcher;
    private final HashSet<ClientInt> clients;
    private final HashMap<String, Integer> searches;

    protected Gateway() throws RemoteException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "1099"));
        this.gatewayBarrel = new GatewayBarrel(this);
        this.dispatcher = new Dispatcher(this.gatewayBarrel);
        this.clients = new HashSet<>();
        this.searches = new HashMap<>();
    }


    @Override
    public boolean indexURL(String url) throws RemoteException {
        if (dispatcher.indexedUrl(url)) {
            System.out.println("Already indexed: " + url);
            return false;
        }
        System.out.println("Indexing " + url);
        dispatcher.pushFront(url);
        return true;
    }

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

    @Override
    public Site[] linkedPages(String url) throws RemoteException {
        return gatewayBarrel.linkedPages(url);
    }

    @Override
    public Map<String, List<Double>> getResponseTimes() throws RemoteException {
        return gatewayBarrel.getResponseTimes();
    }
    
    @Override
    public void addClient(ClientInt client) {
        clients.add(client);
        try {
            client.updateStatistics(gatewayBarrel.getBarrels(), getResponseTimes(), getTopSearches());
        } catch (RemoteException e) {
            System.err.println("Error fetching statistics: " + e.getMessage());
        }
    }

    public String[] getTopSearches() {
        return searches.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toArray(String[]::new);
    }

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
