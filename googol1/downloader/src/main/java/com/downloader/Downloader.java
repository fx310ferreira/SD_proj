package com.downloader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.RemoteException;

import com.utils.Utils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.common.DispatcherInt;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {
    String rmiAddress;
    DispatcherInt dispatcher;
    MulticastSocket socket;
    InetAddress mcastGroup;
    int PORT;
    String mcastAddress;

    public Downloader() {
        this.rmiAddress = Utils.readProperties(this, "rmiAddress", "localhost");
        this.PORT = Integer.parseInt(Utils.readProperties(this, "port", "4321"));
        this.mcastAddress = Utils.readProperties(this, "multicastAddress", "224.3.2.1");
    }

    void download(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Downloader downloaded: " + doc.title());
            Elements elements = doc.select("a[href]");

            for(Element element : elements){
                String link = element.attr("abs:href");
                this.dispatcher.push(link);
            }
        } catch (HttpStatusException e){
            System.out.println("Downloader failed to download: " + e.getUrl() + " " + e.getStatusCode());
        } catch (Exception e){
            System.out.println("Downloader failed to download: " + e.getMessage());
        }
    }

    void multicastMsg(String content){
        try {
            byte[] buffer = content.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.mcastGroup, this.PORT);
            this.socket.send(packet);
        } catch (IOException e){
            System.err.println("Error sending a multicast message: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Downloader downloader = new Downloader();

        try {
            downloader.dispatcher = (DispatcherInt) Naming.lookup("rmi://" + downloader.rmiAddress +"/dispatcher");

            downloader.socket =  new MulticastSocket(downloader.PORT);
            downloader.mcastGroup = InetAddress.getByName(downloader.mcastAddress);


            System.out.println("Downloader is ready");
            while (true) {
                System.out.println("Downloader is waiting for a URL to download...");
                String url = downloader.dispatcher.pop();
                System.out.println("Downloader pooped url: " + url);
                downloader.download(url);
                downloader.multicastMsg(url);
                Thread.sleep(1000);
            }
        } catch (RemoteException e){
            System.out.println("Gateway is down please ty again later");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
