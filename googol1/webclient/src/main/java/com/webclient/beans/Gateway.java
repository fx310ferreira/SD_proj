package com.webclient.beans;

import com.common.GatewayBarrelInt;
import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;
import org.springframework.ui.Model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Gateway {
    private String RMI_ADDRESS;
    private GatewayInt server;

    public Gateway() {
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        try {
            this.server  = (GatewayInt) Naming.lookup("rmi://" + this.RMI_ADDRESS +"/gateway");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String query(String query, int page,  Model model) {
        query = query.strip();
        if(query.isEmpty()) {
            return "redirect:/";
        }
        if ((query.startsWith("http://") || query.startsWith("https://"))){
            try {
                server.indexURL(query);
                return "redirect:"+query;
            } catch (Exception e) {
                System.err.println("Error indexing");
            }
        }
        try {
            Site[] sites = server.search(query, page);
            for (Site site : sites) {
                site.setPagesThatContain(server.linkedPages(site.getUrl()));
            }

            for (Site site : sites) {
                System.out.println(site.getPagesThatContain());
            }

            if (sites.length == 0) {
                model.addAttribute("resultNotFound", true);
            }

            if(sites.length == 10) {
                model.addAttribute("canGoForward", true);
                if(page > 0) {
                    model.addAttribute("canGoBack", true);
                }
            } else if(sites.length < 10) {
                if(page > 0) {
                    model.addAttribute("canGoBack", true);
                }
            }
            List<Site> siteList = new ArrayList<>();
            siteList.addAll(List.of(sites));
            model.addAttribute("sites", siteList);
        } catch (RemoteException e) {
            System.out.println("Error searching");
        }
        return "searches";
    }
}
