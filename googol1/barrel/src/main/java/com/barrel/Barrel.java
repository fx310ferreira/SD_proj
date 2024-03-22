package com.barrel;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.GatewayInt;
import com.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Barrel extends UnicastRemoteObject implements BarrelInt {
    int PORT;
    String MULTICAST_ADDRESS;
    String RMI_ADDRESS;

    Barrel() throws RemoteException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
    }

    @Override
    public void test(String str) throws RemoteException {
        System.out.println(str);
    }
    public static void main(String[] args){
        Barrel barrel = null;
        Database database = new Database();
        database.getConnection();
        try {
            barrel = new Barrel();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        try (MulticastSocket socket = new MulticastSocket(barrel.PORT)) {
            InetAddress mcastAddr = InetAddress.getByName(barrel.MULTICAST_ADDRESS);
            socket.joinGroup(new InetSocketAddress(mcastAddr, 0), NetworkInterface.getByIndex(0));

            GatewayBarrelInt server = (GatewayBarrelInt) Naming.lookup("rmi://" + barrel.RMI_ADDRESS +"/barrels");
            server.subscribe(barrel);
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
