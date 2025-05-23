/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.gateway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.common.DispatcherInt;

/**
 * Represents a Dispatcher responsible for managing URL queues and processing.
 * Implements the DispatcherInt interface.
 */
public class Dispatcher extends UnicastRemoteObject implements DispatcherInt {
    ConcurrentLinkedDeque<String> url_queue;
    Map<String, Long> processing;
    GatewayBarrel gatewayBarrel;
    long id;

    /**
     * Constructor for the Dispatcher class.
     *
     * @param gatewayBarrel the GatewayBarrel to which the Dispatcher is subscribed.
     * @throws RemoteException if there is an error creating the Dispatcher.
     */
    public Dispatcher(GatewayBarrel gatewayBarrel) throws RemoteException {
        this.url_queue = new ConcurrentLinkedDeque<>();
        this.processing = new ConcurrentHashMap<>();
        this.gatewayBarrel = gatewayBarrel;
        this.id = 1;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            for (String url : processing.keySet())
                if (System.currentTimeMillis() - processing.get(url) > 5000){
                    System.out.println("Repushing: " + url);
                    pushFront(url);
                    processing.remove(url);
                }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Pushes a URL to the end of the URL queue.
     * 
     * @param url the URL to be pushed.
     */
    @Override
    public synchronized void push(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url_queue.addLast(url);
            System.out.println("Pushed: " + url + " Size: " + url_queue.size());
            notify();
        }
    }

    /**
     * Pushes a URL to the front of the URL queue.
     * 
     * @param url the URL to be pushed.
     */
    @Override
    public synchronized void pushFront(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url_queue.addFirst(url);
            System.out.println("Pushed: " + url + " Size: " + url_queue.size());
            notify();
        }
    }

    /**
     * Removes and returns the URL at the front of the URL queue.
     * 
     * @return the URL popped from the URL queue.
     */
    @Override
    public synchronized String pop() {
        while (url_queue.isEmpty())
            try {
                wait();
            } catch (Exception e) {
                System.out.println("interruptedException caught");
            }
        String url = url_queue.poll();
        processing.put(url,System.currentTimeMillis());
        System.out.println("Processing: " + processing);
        return url;
    }

    /**
     * Retrieves a unique identifier.
     * 
     * @return a unique identifier.
     * @throws RemoteException if an RMI-related error occurs.
     */
    @Override
    public synchronized long getId() throws RemoteException {
        return id++;
    }

    /**
     * Notifies the Dispatcher that processing for a URL is finished.
     * 
     * @param url the URL for which processing is completed.
     * @throws RemoteException if an RMI-related error occurs.
     */
    @Override
    public synchronized void finishedProcessing(String url) throws RemoteException {
        processing.remove(url);
    }

    /**
     * Checks if a URL has been indexed.
     * 
     * @param url the URL to check.
     * @return true if the URL has been indexed, false otherwise.
     * @throws RemoteException if an RMI-related error occurs.
     */
    @Override
    public synchronized boolean indexedUrl(String url) throws RemoteException {
        return (processing.containsKey(url) || url_queue.contains(url) || gatewayBarrel.indexedUrl(url));
    }

    /**
     * Returns a string representation of the URL queue.
     */
    @Override
    public String toString() {
        return url_queue.toString();
    }
}
