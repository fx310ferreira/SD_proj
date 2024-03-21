package com.barrel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Barrel {
    final static private int PORT = 4321;
    final static private String MULTICAST_ADDRESS = "224.3.2.1";
    public static void main(String[] args){
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            InetAddress mcastAddr = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(new InetSocketAddress(mcastAddr, 0), NetworkInterface.getByIndex(0));
            System.out.println("Barrel is ready");
            while(true){
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Got message: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
