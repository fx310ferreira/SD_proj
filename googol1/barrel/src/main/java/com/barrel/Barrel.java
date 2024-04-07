/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

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

/**
 * Represents a Barrel node in the system.
 * A Barrel is responsible for indexing URLs, performing searches, and managing linked pages.
 * It communicates with the GatewayBarrel and other Barrels via RMI and multicast.
 */
public class Barrel extends UnicastRemoteObject implements BarrelInt {
    int PORT;
    String MULTICAST_ADDRESS;
    String RMI_ADDRESS;
    String BARREL_ID;
    Database database;
    MulticastSocket socket;
    InetAddress mcastAddr;
    MessageQueue messageQueue;

    /**
     * Constructs a Barrel object with default properties.
     * Initializes the Barrel's socket, database, multicast address, and message queue.
     *
     * @throws IOException   If an I/O error occurs while creating the multicast socket.
     * @throws SQLException  If an SQL error occurs while initializing the database.
     */
    Barrel() throws IOException, SQLException {
        super();
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.BARREL_ID = Utils.readProperties(this, "BARREL_ID", "barrel0");
        this.socket = new MulticastSocket(this.PORT);
        this.database = new Database(this.BARREL_ID);
        this.mcastAddr = InetAddress.getByName(this.MULTICAST_ADDRESS);
        this.messageQueue = new MessageQueue(this.database.startId);
    }

    /**
     * Checks if a URL has been indexed by the Barrel.
     *
     * @param url The URL to check.
     * @return True if the URL is indexed, false otherwise.
     * @throws RemoteException If a remote error occurs.
     */
    @Override
    public boolean indexedUrl(String url) throws RemoteException {
        return database.indexedUrl(url);
    }

    /**
     * Searches for sites containing the specified words.
     *
     * @param words The words to search for.
     * @param page  The page number of results to retrieve.
     * @return An array of Site objects matching the search criteria.
     * @throws RemoteException If a remote error occurs.
     */
    @Override
    public Site[] search(String[] words, int page) throws RemoteException {
        if(words.length == 0){
            return new Site[0];
        }
        if(words.length == 1 && (words[0].startsWith("http://") || words[0].startsWith("https://"))){
            return new Site[]{database.searchUrl(words[0])};
        }
        return database.search(words, page);
    }

    /**
     * Retrieves the linked pages for a given URL.
     *
     * @param url The URL for which linked pages are requested.
     * @return An array of Site objects representing linked pages.
     * @throws RemoteException If a remote error occurs.
     */
    @Override
    public Site[] linkedPages(String url) throws RemoteException {
        return database.linkedPages(url);
    }

    /**
     * Notifies the Barrel that it is still alive.
     *
     * @throws RemoteException If a remote error occurs.
     */
    @Override
    public void alive() throws RemoteException {}

    /**
     * Receives a multicast message from the network.
     *
     * @return A JSONObject containing the received message.
     */
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
            return new JSONObject(out.toString());
        } catch (IOException e) {
            System.err.println("Error receiving multicast message: " + e.getMessage());
        }
        return null;
    }

    /**
     * The main method of the Barrel.
     * Initializes the Barrel and connects to the GatewayBarrel.
     * Starts the Recuperator and MessageProcessor threads.
     * Receives multicast messages and adds them to the message queue.
     *
     * @param args The command line arguments.
     */
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
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(0);
        }

        try {
            GatewayBarrelInt server = (GatewayBarrelInt) Naming.lookup("rmi://" + barrel.RMI_ADDRESS +"/barrels");
            if (!server.subscribe(barrel, barrel.BARREL_ID)) {
                System.out.println("Failed to subscribe barrel, ID already in use.");
                System.exit(0);
            }
            Recuperator recuperator = new Recuperator(barrel.messageQueue, barrel.socket, barrel.mcastAddr, barrel.PORT);
            MessageProcessor messageProcessor = new MessageProcessor(barrel.messageQueue, barrel.database, barrel.socket, barrel.mcastAddr, barrel.PORT);
            recuperator.start();
            messageProcessor.start();
            System.out.println("Barrel is ready");
            while(true){
                barrel.messageQueue.push(barrel.receiveMltcMsg());
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
