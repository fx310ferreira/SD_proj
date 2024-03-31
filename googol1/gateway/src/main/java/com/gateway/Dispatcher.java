package com.gateway;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentSkipListSet;

import com.common.DispatcherInt;

public class Dispatcher extends UnicastRemoteObject implements DispatcherInt{
    ConcurrentLinkedQueue<String> url_queue;
    Set<String> processing;
    GatewayBarrel gatewayBarrel;
    long id = 0;

    public Dispatcher(GatewayBarrel gatewayBarrel) throws RemoteException{
        this.url_queue = new ConcurrentLinkedQueue<>();
        this.processing = new ConcurrentSkipListSet<>();
        this.gatewayBarrel = gatewayBarrel;
    }

    @Override
    public synchronized void push(String url) {
        if(url.startsWith("http://") || url.startsWith("https://")){
            url_queue.add(url);
            System.out.println("Pushed: " + url + " Size: " + url_queue.size());
            notify();
        }
    }

    @Override
    public synchronized String pop(){
        while(url_queue.isEmpty())
            try {
                wait();
            } catch (Exception e) {
                System.out.println("interruptedException caught");
            }
        String url = url_queue.poll();
        processing.add(url);
        System.out.println("Processing: " + processing);
        return url;
    }

    @Override
    public synchronized long getId() throws RemoteException {
        return id++;
    }

    @Override
    public synchronized void finishedProcessing(String url) throws RemoteException {
        processing.remove(url);
    }

    @Override
    public synchronized boolean indexedUrl(String url) throws RemoteException {
        return (processing.contains(url) || url_queue.contains(url) || gatewayBarrel.indexedUrl(url));
    }

    @Override
    public String toString(){
        return url_queue.toString();
    }
    
}
