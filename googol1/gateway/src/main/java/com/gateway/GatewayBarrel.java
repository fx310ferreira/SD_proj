package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.Site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a GatewayBarrel that serves as an interface between barrels and the gateway.
 * Implements the GatewayBarrelInt interface.
 */
public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
  private final List<String> barrelIds;
  private final Map<String, BarrelInt> barrels;
  private final Map<String, List<Double>> responseTimes;
  private int currentBarrelIndex;
  private final Gateway gateway;

  /**
   * Constructor for the GatewayBarrel class.
   *
   * @param gateway the gateway to which the barrel is subscribed
   * @throws RemoteException if there is an error creating the GatewayBarrel
   */
  public GatewayBarrel(Gateway gateway) throws RemoteException {
    super();
    this.barrelIds = new ArrayList<>();
    this.barrels = new HashMap<>();
    this.responseTimes = new HashMap<>();
    this.currentBarrelIndex = 0;
    this.gateway = gateway;
  }

  /**
   * Checks if the URL has been indexed in any of the barrels.
   *
   * @param message the URL to check for indexing
   * @return true if the URL is indexed; false otherwise
   * @throws RemoteException if there is a communication-related issue
   */
  @Override
  public boolean indexedUrl(String message) throws RemoteException {
    if (barrelIds.isEmpty()) {
      throw new RuntimeException("No barrels available");
    }

    int numBarrels = barrelIds.size();

    while (!barrelIds.isEmpty()) {
      String currentBarrelId = barrelIds.get(currentBarrelIndex);
      BarrelInt currentBarrel = barrels.get(currentBarrelId);

      try {
        currentBarrelIndex = (currentBarrelIndex + 1) % numBarrels;
        return currentBarrel.indexedUrl(message);
      } catch (RemoteException e) {
          barrels.remove(currentBarrelId);
          barrelIds.remove(currentBarrelId);
          responseTimes.remove(currentBarrelId);
          numBarrels--;
          if(currentBarrelIndex >= numBarrels) {
            currentBarrelIndex = 0;
          }
      }
    }
    throw new RuntimeException("No active barrels available");
  }

  /**
   * Searches for sites containing the given words.
   *
   * @param words an array of words to search for
   * @param page  the page number of the search results
   * @return an array of sites matching the search criteria
   * @throws RemoteException if there is a communication-related issue
   */
  public Site[] search(String[] words, int page) throws RemoteException {
    if (barrelIds.isEmpty()) {
      throw new RuntimeException("No barrels available");
    }

    int numBarrels = barrelIds.size();

    while (!barrelIds.isEmpty()) {
      String currentBarrelId = barrelIds.get(currentBarrelIndex);
      BarrelInt currentBarrel = barrels.get(currentBarrelId);

      try {
        currentBarrelIndex = (currentBarrelIndex + 1) % numBarrels;
        long startTime = System.nanoTime();
        Site[] sites = currentBarrel.search(words, page);
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1e7;
        if (!responseTimes.containsKey(currentBarrelId)) {
          responseTimes.put(currentBarrelId, new ArrayList<>());
        }
        responseTimes.get(currentBarrelId).add(elapsedTime);
        return sites;
      } catch (RemoteException e) {
        barrels.remove(currentBarrelId);
        barrelIds.remove(currentBarrelId);
        responseTimes.remove(currentBarrelId);
        numBarrels--;
        if(currentBarrelIndex >= numBarrels) {
          currentBarrelIndex = 0;
        }
      }
    }
    throw new RuntimeException("No active barrels available");
  }

  /**
   * Retrieves linked pages for a given URL.
   *
   * @param url the URL for which linked pages are to be retrieved
   * @return an array of linked pages
   * @throws RemoteException if there is a communication-related issue
   */
  public Site[] linkedPages(String url) throws RemoteException {
    if (barrelIds.isEmpty()) {
      throw new RuntimeException("No barrels available");
    }

    int numBarrels = barrelIds.size();

    while (!barrelIds.isEmpty()) {
      String currentBarrelId = barrelIds.get(currentBarrelIndex);
      BarrelInt currentBarrel = barrels.get(currentBarrelId);

      try {
        currentBarrelIndex = (currentBarrelIndex + 1) % numBarrels;
        return currentBarrel.linkedPages(url);
      } catch (RemoteException e) {
        barrels.remove(currentBarrelId);
        barrelIds.remove(currentBarrelId);
        numBarrels--;
        if(currentBarrelIndex >= numBarrels) {
          currentBarrelIndex = 0;
        }
      }
    }
    throw new RuntimeException("No active barrels available");
  }

  /**
   * Subscribes a barrel to the gateway.
   *
   * @param barrel   the barrel to subscribe
   * @param barrelId the ID of the barrel
   * @return true if the subscription is successful; false otherwise
   * @throws RemoteException if there is a communication-related issue
   */
  @Override
  public synchronized boolean subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
    if (!barrels.containsKey(barrelId)) {
      barrels.put(barrelId, barrel);
      barrelIds.add(barrelId);
      responseTimes.put(barrelId, new ArrayList<>());
      System.out.println("Barrel with ID " + barrelId + " subscribed.");
      this.gateway.sendUpdatedStatistics();
      return true;
    } else {
      try {
          barrels.get(barrelId).alive();
          System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
          return false;
      } catch (RemoteException e) {
          barrels.put(barrelId, barrel);
          System.out.println("Barrel with ID " + barrelId + " resubscribed.");
          this.gateway.sendUpdatedStatistics();
          return true;
      }
    }
  }

  /**
   * Retrieves the response times for the Gateway.
   *
   * @return a map of response times for the Gateway
   * @throws RemoteException if there is an error retrieving response times
   */
  public Map<String, List<Double>> getResponseTimes() throws RemoteException {
    return new HashMap<>(responseTimes);
  }

  /**
   * Retrieves the IDs of all barrels registered with the gateway.
   * 
   * @return a set of barrel IDs.
   * @throws RemoteException if there is an error retrieving barrel IDs.
   */
  public Set<String> getBarrels() throws RemoteException {
    return new HashSet<>(barrelIds);
  }
}
