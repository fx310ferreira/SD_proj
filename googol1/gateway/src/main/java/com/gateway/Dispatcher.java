package com.gateway;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import com.common.DispatcherInt;

public class Dispatcher extends UnicastRemoteObject implements DispatcherInt{
    ConcurrentLinkedQueue<String> url_queue;

    public Dispatcher() throws RemoteException{
        url_queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public synchronized void push(String url) {
        url_queue.add(url);
        System.out.println("Pushed:" + url_queue);
        notify();
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
        notify();
        System.out.println("Pooped:" + url_queue);
        return url;
    }

    @Override
    public String toString(){
        return url_queue.toString();
    }
    
}
