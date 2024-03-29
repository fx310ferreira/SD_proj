package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.Site;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GatewayBarrel extends UnicastRemoteObject implements GatewayBarrelInt {
  ArrayList<String> barrel_ids;
  Map<String, BarrelInt> barrels;

  GatewayBarrel() throws RemoteException {
    super();
    this.barrel_ids = new ArrayList<>();
    this.barrels = new HashMap<>();
  }

  @Override
  public boolean indexedUrl(String message) throws RuntimeException {
    do {
      if (barrel_ids.isEmpty())
        throw new RuntimeException("No barrels available");
      try {
        return barrels.get(barrel_ids.get(0)).indexedUrl(message);
      } catch (RemoteException e) {
        System.out.println("Barrel with ID " + barrel_ids.get(0) + " is dead.");
        barrels.remove(barrel_ids.get(0));
        barrel_ids.remove(0);
      }
    } while (!barrel_ids.isEmpty());
    throw new RuntimeException("No barrels available");
  }

  public Site search(String[] words) throws RemoteException {
    do {
      if (barrel_ids.isEmpty())
        throw new RuntimeException("No barrels available");
      try {
        return barrels.get(barrel_ids.get(0)).search(words);
      } catch (RemoteException e) {
        System.out.println("Barrel with ID " + barrel_ids.get(0) + " is dead: " + e.getMessage());
        barrels.remove(barrel_ids.get(0));
        barrel_ids.remove(0);
      }
    } while (!barrel_ids.isEmpty());
    throw new RuntimeException("No barrels available");
  }

  @Override
  public synchronized void subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
    if (!barrels.containsKey(barrelId)) {
      barrels.put(barrelId, barrel);
      barrel_ids.add(barrelId);
      System.out.println("Barrel with ID " + barrelId + " subscribed.");
    } else {
      try {
        barrels.get(barrelId).alive();
        System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
      } catch (RemoteException e) {
        barrels.put(barrelId, barrel);
        System.out.println("Barrel with ID " + barrelId + " resubscribed.");
      }
    }
  }
}
