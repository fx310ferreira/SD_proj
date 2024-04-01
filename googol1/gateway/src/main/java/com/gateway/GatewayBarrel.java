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

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
  private final List<String> barrelIds;
  private final Map<String, BarrelInt> barrels;
  private final Map<String, List<Double>> responseTimes;
  private int currentBarrelIndex;

  public GatewayBarrel() throws RemoteException {
    super();
    this.barrelIds = new ArrayList<>();
    this.barrels = new HashMap<>();
    this.responseTimes = new HashMap<>();
    this.currentBarrelIndex = 0;
  }

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

  @Override
  public synchronized boolean subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
    if (!barrels.containsKey(barrelId)) {
      barrels.put(barrelId, barrel);
      barrelIds.add(barrelId);
      responseTimes.put(barrelId, new ArrayList<>());
      System.out.println("Barrel with ID " + barrelId + " subscribed.");
      return true;
    } else {
      try {
          barrels.get(barrelId).alive();
          System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
          return true;
      } catch (RemoteException e) {
          barrels.put(barrelId, barrel);
          System.out.println("Barrel with ID " + barrelId + " resubscribed.");
          return false;
      }
    }
  }

  public Set<String> getAliveBarrels() throws RemoteException {
    Set<String> aliveBarrels = new HashSet<>();
    for (Map.Entry<String, BarrelInt> entry : barrels.entrySet()) {
      String barrelId = entry.getKey();
      BarrelInt barrel = entry.getValue();
      try {
        barrel.alive();
        aliveBarrels.add(barrelId);
      } catch (RemoteException e) {
        
      }
    }
    return aliveBarrels;
  }


  public Map<String, List<Double>> getResponseTimes() throws RemoteException {
    return new HashMap<>(responseTimes);
  }
}
