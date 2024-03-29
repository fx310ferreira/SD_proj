package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
  private List<String> barrelIds;
  private Map<String, BarrelInt> barrels;
  private int currentBarrelIndex;

  public GatewayBarrel() throws RemoteException {
    super();
    this.barrelIds = new ArrayList<>();
    this.barrels = new HashMap<>();
    this.currentBarrelIndex = 0;
  }

  @Override
  public boolean indexedUrl(String message) throws RemoteException {
    if (barrelIds.isEmpty()) {
      throw new RuntimeException("No barrels available");
    }

    int numAttempts = 0;
    int numBarrels = barrelIds.size();

    while (numAttempts < numBarrels) {
      String currentBarrelId = barrelIds.get(currentBarrelIndex);
      BarrelInt currentBarrel = barrels.get(currentBarrelId);

      try {
        if (currentBarrel.alive()) {
          return currentBarrel.indexedUrl(message);
        } else {
          barrels.remove(currentBarrelId);
          barrelIds.remove(currentBarrelId);
        }
      } catch (RemoteException e) {
          barrels.remove(currentBarrelId);
          barrelIds.remove(currentBarrelId);
      }

      currentBarrelIndex = (currentBarrelIndex + 1) % numBarrels;
      numAttempts++;
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
        if (barrel.alive()) {
          System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
        } else {
          // Se o barril estiver inativo, este é removido e inscreve-se o novo barril
          barrels.remove(barrelId);
          barrelIds.remove(barrelId);
          barrels.put(barrelId, barrel);
          barrelIds.add(barrelId);
          System.out.println("Barrel with ID " + barrelId + " resubscribed.");
        }
      } catch (RemoteException e) {
        // Se ocorrer uma exceção ao verificar a atividade do barril, assume-se que o barril está inativo
        barrels.remove(barrelId);
        barrelIds.remove(barrelId);
        barrels.put(barrelId, barrel);
        barrelIds.add(barrelId);
        System.out.println("Barrel with ID " + barrelId + " resubscribed.");
      }
    }
  }
}
