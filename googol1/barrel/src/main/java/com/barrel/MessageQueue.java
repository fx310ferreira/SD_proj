package com.barrel;

import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a concurrent message queue for storing JSON messages.
 * A MessageQueue is responsible for storing messages in a queue and managing expected message IDs and missing messages.
 */
public class MessageQueue {
    private final Queue<JSONObject> queue;
    private final ConcurrentHashMap<Long, Long> missingIds;
    long expectedId;

    /**
     * Constructs a MessageQueue object with the given maximum message ID.
     * Initializes the message queue, missing message IDs, and expected message ID.
     *
     * @param maxId The maximum ID expected in the messages.
     */
    public MessageQueue(long maxId){
        this.queue = new ConcurrentLinkedQueue<>();
        this.missingIds = new ConcurrentHashMap<>();
        this.expectedId = maxId+1;
    }

    /**
     * Pushes a message into the message queue.
     * If the message is a resend message, it is added to the queue.
     * If the message ID is not the expected ID, it is added to the missing message IDs.
     * If the message ID is the expected ID, it is added to the queue and the expected ID is incremented.
     *
     * @param message The JSON message to push into the queue.
     */
    public synchronized void push(JSONObject message){
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

    /**
     * Pops a message from the message queue.
     * If the queue is empty, the thread waits until a message is added to the queue.
     *
     * @return The JSON message popped from the queue.
     */
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

    /**
     * Retrieves the ConcurrentHashMap containing missing message IDs and their arrival times.
     *
     * @return The missing message IDs.
     */
    public ConcurrentHashMap<Long, Long> getMissingIds() {
        return missingIds;
    }

    /**
     * Resets the arrival for a specific message ID in the ConcurrentHashMap.
     *
     * @param id The ID of the missing message for which to reset the arrival time.
     */
    public synchronized void resetTime(long id){
        missingIds.put(id, System.currentTimeMillis());
    }
}
