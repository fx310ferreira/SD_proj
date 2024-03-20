package com.downloader;

import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Properties;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.common.DispatcherInt;

public class Downloader {

    String rmiAddress;

    public Downloader() {
        this.rmiAddress = readRMIAddress();
    }

    String readRMIAddress() {
        final String propertiesFile = "config.properties";
        String defaultAddress = "localhost";

        try (InputStream input = Downloader.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (input == null){
                System.out.println("Unable to find " + propertiesFile + " defaulting to: " + defaultAddress);
                return defaultAddress;
            }

            Properties prop = new Properties();
            prop.load(input);

            if (prop.getProperty("rmiAddress") != null) {
                System.out.println("Using address: " + prop.getProperty("rmiAddress"));
                return prop.getProperty("rmiAddress");
            }else {
                System.out.println("Unable to find property defaulting to: " + defaultAddress);
                return defaultAddress;
            }

        } catch (Exception e) {
            System.out.println("Error reading " + propertiesFile + " defaulting to: " + defaultAddress);
            return defaultAddress;
        }
    }

    void download(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Downloader downloaded: " + doc.title());
        } catch (HttpStatusException e){
            System.out.println("Downloader failed to download: " + e.getUrl() + " " + e.getStatusCode());
        } catch (Exception e){
            System.out.println("Downloader failed to download: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Downloader client = new Downloader();

        try {
            DispatcherInt dispatcher = (DispatcherInt) Naming.lookup("rmi://" + client.rmiAddress +"/dispatcher");
            System.out.println("Downloader is ready");
            while (true) {
                System.out.println("Downloader is waiting for a URL to download...");
                String url = dispatcher.pop();
                System.out.println("Downloader pooped url: " + url);
                client.download(url);
            }
        } catch (RemoteException e){
            System.out.println("Gateway is down please ty again later");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
