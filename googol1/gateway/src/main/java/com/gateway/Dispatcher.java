package com.gateway;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import com.common.DispatcherInt;

public class Dispatcher extends UnicastRemoteObject implements DispatcherInt{
    ConcurrentLinkedQueue<String> url_queue;
    HashSet<String> processed;

    public Dispatcher() throws RemoteException{
        url_queue = new ConcurrentLinkedQueue<>();
        processed = new HashSet<>();
    }

    @Override
    public synchronized void push(String url) {
        if(!processed.contains(url)){
            url_queue.add(url);
            processed.add(url);
            System.out.println("Pushed: " + url + " Size: " + url_queue.size());
            System.out.println("Processed size:" + processed.size());
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
        notify(); // this notify is probably useless
        System.out.println("Pooped: " + url + " Size: " + url_queue.size());
        return url;
    }

    @Override
    public String toString(){
        return url_queue.toString();
    }
    
}
