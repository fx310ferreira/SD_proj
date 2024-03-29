package com.barrel;

import com.common.BarrelInt;
import com.common.GatewayBarrelInt;
import com.common.Site;
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

    @Override
    public Site[] search(String[] words) throws RemoteException {
        if(words.length == 0){
            return new Site[0];
        }
        if(words.length == 1 && (words[0].startsWith("http://") || words[0].startsWith("https://"))){
            return new Site[]{database.searchUrl(words[0])};
        }
        return database.search(words);
    }

    @Override
    public void alive() throws RemoteException {}


    private JSONObject receiveMltcMsg() {
        try {
            byte[] buffer = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            byte[] data = packet.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read;
            while ((read = in.read()) != 0) {
                out.write(read);
            }
            return new JSONObject(new String(out.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        Barrel barrel = null;

        try {
            barrel = new Barrel();
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
                    barrel.database.indexUrl(message.getString("url"), message.getJSONArray("words"), message.getString("title"));
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
