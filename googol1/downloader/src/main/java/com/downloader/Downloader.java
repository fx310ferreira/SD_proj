package com.downloader;

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

    public Downloader() {
        this.rmiAddress = Utils.readRMIAddress(this);
        try {
            this.dispatcher = (DispatcherInt) Naming.lookup("rmi://" + this.rmiAddress +"/dispatcher");
        } catch (RemoteException e){
            System.out.println("Gateway is down please ty again later");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void download(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Downloader downloaded: " + doc.title());
            Elements elements = doc.select("a[href]");

            for(Element element : elements){
                String link = element.attr("href");
                this.dispatcher.push(link);
            }
        } catch (HttpStatusException e){
            System.out.println("Downloader failed to download: " + e.getUrl() + " " + e.getStatusCode());
        } catch (Exception e){
            System.out.println("Downloader failed to download: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Downloader client = new Downloader();

        try {
            System.out.println("Downloader is ready");
            while (true) {
                System.out.println("Downloader is waiting for a URL to download...");
                String url = client.dispatcher.pop();
                System.out.println("Downloader pooped url: " + url);
                client.download(url);
                Thread.sleep(1000);
            }
        } catch (RemoteException e){
            System.out.println("Gateway is down please ty again later");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
