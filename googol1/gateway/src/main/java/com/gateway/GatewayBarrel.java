package com.gateway;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;

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

  private BarrelInt getBarrel() throws RuntimeException {
    if (barrel_ids.isEmpty())
      throw new RuntimeException("No barrels available");
    return barrels.get(barrel_ids.get(0));
  }

  @Override
  public boolean indexedUrl(String message) {
    try {
      return getBarrel().indexedUrl(message);
    } catch (RemoteException e) {
      // disconeotu do barrel
      throw new RuntimeException(e);
    }
  }

  @Override
  public synchronized void subscribe(BarrelInt barrel, String barrelId) throws RemoteException {
    if (!barrels.containsKey(barrelId)) {
      barrels.put(barrelId, barrel);
      barrel_ids.add(barrelId);
      System.out.println("Barrel with ID " + barrelId + " subscribed.");
    } else {
      System.out.println("Barrel with ID " + barrelId + " is already subscribed.");
    }
  }
}
