/**
 * @author Diogo Filipe Serra e Silva
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.downloader;

import java.text.Normalizer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import com.utils.Utils;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.common.DispatcherInt;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Represents a Downloader node in the system.
 * A Downloader is responsible for downloading web pages and extracting their content.
 * It communicates with the Dispatcher via RMI and multicast.
 */
public class Downloader {
    String RMI_ADDRESS;
    int PORT;
    String MULTICAST_ADDRESS;
    DispatcherInt dispatcher;
    MulticastSocket socket;
    InetAddress mcastGroup;

    /**
     * Constructs a Downloader object with default properties.
     * Initializes the Downloader's socket, multicast address, and connects to the Dispatcher via RMI.
     *
     * @throws IOException        If an I/O error occurs while creating the multicast socket.
     * @throws NotBoundException  If the Dispatcher RMI lookup fails.
     */
    public Downloader() throws IOException, NotBoundException {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.dispatcher = (DispatcherInt) Naming.lookup("rmi://" + this.RMI_ADDRESS + "/dispatcher");
        this.socket = new MulticastSocket(this.PORT);
        this.mcastGroup = InetAddress.getByName(this.MULTICAST_ADDRESS);
    }

    /**
     * Downloads a web page, extracts its content, and sends it to the Dispatcher.
     *
     * @param url The URL of the web page to download.
     * @throws RuntimeException If an error occurs while downloading the web page.
     */
    void download(String url) throws RuntimeException {
        try {
            Document doc = Jsoup.connect(url).get();
            String text = doc.text();
            String[] words = Normalizer.normalize(text, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")
                    .replaceAll("\\p{Punct}", "")
                    .toLowerCase()
                    .split("\\s+");
            // Split words into chunks of 5000
            int start = 0;
            JSONObject content;
            while (start < words.length) {
                int end = Math.min(start + 5000, words.length);
                String[] temp = new String[end - start];
                System.arraycopy(words, start, temp, 0, end - start);
                start = end;
                content = new JSONObject();
                content.put("url", url);
                content.put("words", temp);
                content.put("title", doc.title());
                content.put("type", "index");
                content.put("id", this.dispatcher.getId());
                multicastMsg(content);
            }

            Elements elements = doc.select("a[href]");
            for (Element element : elements) {
                String link = element.attr("abs:href");
                content = new JSONObject();
                content.put("url", url);
                content.put("url1", link);
                content.put("type", "link_link");
                content.put("id", this.dispatcher.getId());
                multicastMsg(content);
                if (dispatcher.indexedUrl(link)) {
                    // System.out.println("Link already indexed: " + link);
                } else {
                    this.dispatcher.push(link);
                }
            }
        } catch (HttpStatusException e) {
            System.err.println("Downloader failed to download: " + e.getUrl() + " " + e.getStatusCode());
        } catch (IOException e) {
            System.err.println("Downloader failed to download: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid link");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sends a multicast message to the network.
     *
     * @param content The content of the message to send.
     */
    void multicastMsg(JSONObject content) {
        try {
            String msg = content.toString();
            byte[] buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.mcastGroup, this.PORT);
            this.socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending a multicast message: " + e.getMessage() + "msg id : " + content.getLong("id") + " size: " + content.toString().length());
        }
    }

    /**
     * The main method of the Downloader.
     * Continuously downloads web pages from the Dispatcher.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
            Downloader downloader = new Downloader();

            System.out.println("Downloader is ready");
            while (true) {
                String url = downloader.dispatcher.pop();
                downloader.download(url);
                downloader.dispatcher.finishedProcessing(url);
            }
        } catch (IOException | NotBoundException | RuntimeException e) {
            System.err.println("Gateway is down please ty again later: " + e.getMessage());
        }
    }
}
