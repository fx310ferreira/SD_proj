package com.barrel;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.SQLException;

/**
 * Represents a message processor thread in the system.
 * A MessageProcessor is responsible for processing messages from the message queue and updating the database accordingly.
 */
public class MessageProcessor extends Thread{
    private final MessageQueue messageQueue;
    private final Database database;
    private final MulticastSocket socket;
    private final InetAddress mcastGroup;
    private final int PORT;

    /**
     * Constructs a MessageProcessor object with the given message queue, database, multicast socket, multicast group, and port.
     *
     * @param messageQueue The message queue to process messages from.
     * @param database     The database to update with the processed messages.
     * @param socket       The multicast socket used for sending messages.
     * @param mcastGroup   The multicast group to send messages to.
     * @param PORT         The port number used for multicast communication.
     */
    public MessageProcessor(MessageQueue messageQueue, Database database, MulticastSocket socket, InetAddress mcastGroup, int PORT){
        this.messageQueue = messageQueue;
        this.database = database;
        this.socket = socket;
        this.mcastGroup = mcastGroup;
        this.PORT = PORT;
    }

    /**
     * Runs the MessageProcessor thread, continuously processing messages from the message queue and updating the database accordingly.
     */
    @Override
    public void run(){
        while(true){
            try {
                JSONObject message = messageQueue.pop();
                if(message.getString("type").equals("index")){
                    this.database.insertMessage(message.getLong("id"), message.toString());
                    this.database.indexUrl(message.getString("url"), message.getJSONArray("words"), message.getString("title"));
                } else if(message.getString("type").equals("link_link")){
                    this.database.insertMessage(message.getLong("id"), message.toString());
                    this.database.addLink(message.getString("url"), message.getString("url1"));
                } else if(message.getString("type").equals("resend")){
                    JSONObject msg = this.database.getMessage(message.getLong("id"));
                    if(msg != null){
                        try {
                            String msgStr = msg.toString();
                            byte[] buffer = msgStr.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.mcastGroup, this.PORT);
                            this.socket.send(packet);
                        } catch (IOException e) {
                            System.err.println("Error sending a multicast message: " + e.getMessage() + "msg id : " + msg.getLong("id") + " size: " + msg.toString().length());
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
