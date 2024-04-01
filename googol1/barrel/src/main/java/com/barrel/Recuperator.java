package com.barrel;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Recuperator extends Thread{
    private final MessageQueue messageQueue;
    private final MulticastSocket socket;
    private final InetAddress mcastGroup;
    private final int PORT;

    public Recuperator(MessageQueue messageQueue, MulticastSocket socket, InetAddress mcastGroup, int PORT){
        this.messageQueue = messageQueue;
        this.socket = socket;
        this.mcastGroup = mcastGroup;
        this.PORT = PORT;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
                for (Long i : messageQueue.getMissingIds().keySet())
                    if(System.currentTimeMillis() - messageQueue.getMissingIds().get(i) > 5000){
                        JSONObject content = new JSONObject();
                        content.put("type", "resend");
                        content.put("id", i);
                        String msg = content.toString();
                        byte[] buffer = msg.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.mcastGroup, this.PORT);
                        this.socket.send(packet);
                        messageQueue.resetTime(i);
                    }
            } catch (InterruptedException e) {
                System.err.println("Error sleeping: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error sending a resend message: " + e.getMessage());
            }
        }
    }
}
