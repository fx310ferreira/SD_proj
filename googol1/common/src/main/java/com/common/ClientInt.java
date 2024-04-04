package com.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ClientInt extends Remote {
    void updateStatistics(Set<String> activeBarrels, Map<String, List<Double>> responseTimes, String[] topSearches) throws RemoteException;
}
