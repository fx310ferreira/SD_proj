package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.Site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
  private final List<String> barrelIds;
  private final Map<String, BarrelInt> barrels;
  private final Map<String, Double> averageResponseTime;
  private int currentBarrelIndex;

  public GatewayBarrel() throws RemoteException {
    super();
    this.barrelIds = new ArrayList<>();
    this.barrels = new HashMap<>();
    this.averageResponseTime = new HashMap<>();
    this.currentBarrelIndex = 0;
  }

  private void registerAverageTime(String idBarrel, double responseTime) {
    if (!averageResponseTime.containsKey(idBarrel)) {
      averageResponseTime.put(idBarrel, responseTime);
    } else {
        double previousAverageTime = averageResponseTime.get(idBarrel);
        averageResponseTime.put(idBarrel, (previousAverageTime + responseTime) / 2);
    }
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
          averageResponseTime.remove(currentBarrelId);
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
        registerAverageTime(currentBarrelId, elapsedTime);
        return sites;
      } catch (RemoteException e) {
        barrels.remove(currentBarrelId);
        barrelIds.remove(currentBarrelId);
        averageResponseTime.remove(currentBarrelId);
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
  public synchronized void subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
    // Verifica se o barril já está inscrito
    if (!barrels.containsKey(barrelId)) {
      barrels.put(barrelId, barrel);
      barrelIds.add(barrelId);
      System.out.println("Barrel with ID " + barrelId + " subscribed.");
    } else {
      // Se o barril já estiver inscrito, verifica se está ativo
      try {
        barrels.get(barrelId).alive();
        System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
      } catch (RemoteException e) {
        // Se ocorrer uma exceção ao verificar a atividade do barril, assume-se que o barril está inativo
        barrels.put(barrelId, barrel);
        System.out.println("Barrel with ID " + barrelId + " resubscribed.");
      }
    }
  }
}
