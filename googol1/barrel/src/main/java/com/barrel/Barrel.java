package com.barrel;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.GatewayInt;
import com.utils.Utils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    Database database;
    MulticastSocket socket;
    InetAddress mcastAddr;

    Barrel() throws IOException, SQLException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.BARREL_ID = Utils.readProperties(this, "BARREL_ID", "barrel0");
        this.socket = new MulticastSocket(this.PORT);
        this.database = new Database(this.BARREL_ID);
        this.mcastAddr = InetAddress.getByName(this.MULTICAST_ADDRESS);
    }

    @Override
    public boolean indexedUrl(String url) throws RemoteException {
        return database.indexedUrl(url);
    }

    private JSONObject receiveMltcMsg() {
        byte[] buffer = new byte[10240];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            // Create a string from the received data and parse it as a JSONObject
            String message = new String(buffer, 0, packet.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message = new String(buffer, 0, packet.getLength());
        return new JSONObject(message);
    }

    public static void main(String[] args){
        Barrel barrel = null;

        try {
            barrel = new Barrel();
            barrel.database = new Database(barrel.BARREL_ID);
            barrel.socket.joinGroup(new InetSocketAddress(barrel.mcastAddr, 0), NetworkInterface.getByIndex(0));
        } catch (RemoteException e) {
            System.err.println("Error connecting to rmi: " + e.getMessage());
            System.exit(0);
        } catch (SQLException e){
            System.err.println("Error connecting to the database: " + e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            System.err.println("Error creating the multicast socket: " + e.getMessage());
            System.exit(0);
        }

        try {
            GatewayBarrelInt server = (GatewayBarrelInt) Naming.lookup("rmi://" + barrel.RMI_ADDRESS +"/barrels");
            server.subscribe(barrel, barrel.BARREL_ID);
            System.out.println("Barrel is ready");
            while(true){
                JSONObject message = barrel.receiveMltcMsg();
                if(message.getString("type").equals("index"))
                    barrel.database.indexUrl(message.getString("url"), message.getJSONArray("words"));
                else if(message.getString("type").equals("link_link")){
                    barrel.database.addLink(message.getString("url"), message.getString("url1"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
