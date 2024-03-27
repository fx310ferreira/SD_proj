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

    public Dispatcher() throws RemoteException{
        url_queue = new ConcurrentLinkedQueue<>();
        processing = new ConcurrentSkipListSet<>();
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
    public void finishedProcessing(String url) throws RemoteException {
        processing.remove(url);
    }

    @Override
    public String toString(){
        return url_queue.toString();
    }
    
}
