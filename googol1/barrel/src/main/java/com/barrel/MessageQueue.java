package com.barrel;

import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private final Queue<JSONObject> queue;
    private final ConcurrentHashMap<Long, Long> missingIds;
    long expectedId;


    public MessageQueue(long maxId){
        this.queue = new ConcurrentLinkedQueue<>();
        this.missingIds = new ConcurrentHashMap<>();
        this.expectedId = maxId+1;
    }

    public synchronized void  push(JSONObject message){
        long receivedId = message.getLong("id");
        String type = message.getString("type");
        if(type.equals("resend")){
            queue.add(message);
        } else if(expectedId != receivedId){
            if(receivedId > expectedId){
                for(long i = expectedId; i < receivedId; i++){
                    missingIds.put(i, System.currentTimeMillis());
                }
                queue.add(message);
                expectedId = receivedId+1;
            } else {
                if(missingIds.remove(receivedId) != null){
                    queue.add(message);
                } else {
                    System.out.println("Received a repeated message: " + receivedId);
                }
            }
        }else{
            queue.add(message);
            expectedId++;
        }
        notify();
    }

    public synchronized JSONObject pop(){
        while(queue.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return queue.poll();
    }

    public ConcurrentHashMap<Long, Long> getMissingIds() {
        return missingIds;
    }

    public synchronized void resetTime(long id){
        missingIds.put(id, System.currentTimeMillis());
    }
}
