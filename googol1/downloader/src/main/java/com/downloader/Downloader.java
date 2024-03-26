package com.downloader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Arrays;

import com.utils.Utils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.common.DispatcherInt;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {
    String RMI_ADDRESS;
    int PORT;
    String MULTICAST_ADDRESS;
    DispatcherInt dispatcher;
    MulticastSocket socket;
    InetAddress mcastGroup;

    public Downloader() throws IOException, NotBoundException {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        this.PORT = Integer.parseInt(Utils.readProperties(this, "PORT", "4321"));
        this.MULTICAST_ADDRESS = Utils.readProperties(this, "MULTICAST_ADDRESS", "224.3.2.1");
        this.dispatcher = (DispatcherInt) Naming.lookup("rmi://" + this.RMI_ADDRESS + "/dispatcher");
        this.socket = new MulticastSocket(this.PORT);
        this.mcastGroup = InetAddress.getByName(this.MULTICAST_ADDRESS);
    }

    void download(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String text = doc.text();

            // Remove espaços, tabulações e quebras de linha
            text = text.replaceAll("\\p{Punct}", "").replaceAll("[^\\p{ASCII}]", "").toLowerCase();
            String[] words = text.split("\\s+");
            System.out.println(Arrays.toString(words));
//            for (String word : words) {
//                // Remove pontuação, acentos e converte maiúsculas para minúsculas
//                word = word.replaceAll("\\p{Punct}", "").replaceAll("[^\\p{ASCII}]", "").toLowerCase();
//                if (!word.isEmpty()) {
//
//                }
//            }

            Elements elements = doc.select("a[href]");
            for (Element element : elements) {
                String link = element.attr("abs:href");
                this.dispatcher.push(link);
            }
        } catch (HttpStatusException e) {
            System.err.println("Downloader failed to download: " + e.getUrl() + " " + e.getStatusCode());
        } catch (IOException e) {
            System.err.println("Downloader failed to download: " + e.getMessage());
        } catch (IllegalArgumentException e){
            System.out.println("Invalid link");
        }
    }

    void multicastMsg(String content) {
        try {
            byte[] buffer = content.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.mcastGroup, this.PORT);
            this.socket.send(packet);
        } catch (IOException e) {
            System.err.println("Error sending a multicast message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Downloader downloader = new Downloader();

            System.out.println("Downloader is ready");
            while (true) {
                System.out.println("Downloader is waiting for a URL to download...");
                String url = downloader.dispatcher.pop();
                System.out.println("Downloader pooped url: " + url);
                downloader.download(url);
                downloader.multicastMsg(url);
            }
        } catch (IOException | NotBoundException e) {
            System.err.println("Gateway is down please ty again later");
        }
    }
}
