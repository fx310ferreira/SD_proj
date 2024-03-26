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
import java.sql.SQLException;

public class Barrel extends UnicastRemoteObject implements BarrelInt {
    int PORT;
    String MULTICAST_ADDRESS;
    String RMI_ADDRESS;
    String BARREL_ID;

    Barrel() throws RemoteException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.BARREL_ID = Utils.readProperties(this, "BARREL_ID", "barrel0");
    }

    @Override
    public void test(String str) throws RemoteException {
        System.out.println(str);
    }
    public static void main(String[] args){
        Barrel barrel = null;
        Database database = null;
        try {
            barrel = new Barrel();
            database = new Database(barrel.BARREL_ID);
        } catch (RemoteException e) {
            System.err.println("Error connecting to rmi: " + e.getMessage());
            System.exit(0);
        } catch (SQLException e){
            System.err.println("Error connecting to the database: " + e.getMessage());
            System.exit(0);
        }
        try (MulticastSocket socket = new MulticastSocket(barrel.PORT)) {
            InetAddress mcastAddr = InetAddress.getByName(barrel.MULTICAST_ADDRESS);
            socket.joinGroup(new InetSocketAddress(mcastAddr, 0), NetworkInterface.getByIndex(0));

            GatewayBarrelInt server = (GatewayBarrelInt) Naming.lookup("rmi://" + barrel.RMI_ADDRESS +"/barrels");
            server.subscribe(barrel, barrel.BARREL_ID);
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
