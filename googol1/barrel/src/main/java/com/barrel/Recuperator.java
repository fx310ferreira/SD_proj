/**
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.barrel;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Represents a Recuperator thread in the system.
 * A Recuperator is responsible for periodically checking for missing messages in the message queue and resending them over multicast if necessary.
 */
public class Recuperator extends Thread{
    private final MessageQueue messageQueue;
    private final MulticastSocket socket;
    private final InetAddress mcastGroup;
    private final int PORT;

    /**
     * Constructs a Recuperator object with the given message queue, multicast socket, multicast group, and port.
     *
     * @param messageQueue The message queue to check for missing messages.
     * @param socket       The multicast socket used for sending messages.
     * @param mcastGroup   The multicast group to send messages to.
     * @param PORT         The port number used for multicast communication.
     */
    public Recuperator(MessageQueue messageQueue, MulticastSocket socket, InetAddress mcastGroup, int PORT){
        this.messageQueue = messageQueue;
        this.socket = socket;
        this.mcastGroup = mcastGroup;
        this.PORT = PORT;
    }

    /**
     * Periodically checks for missing messages in the message queue and resends them over multicast if necessary.
     */
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
