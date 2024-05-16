package com.webclient.beans;

import com.common.ClientInt;
import com.common.GatewayInt;
import com.common.Site;
import com.utils.Utils;
import com.webclient.Message;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Gateway extends UnicastRemoteObject implements ClientInt {
    private String RMI_ADDRESS;
    private GatewayInt server;
    private final SimpMessagingTemplate template;
    ArrayList<String> topSearches = new ArrayList<>();
    ArrayList<String> activeBarrels = new ArrayList<>();
    ArrayList<Double> responseTimes = new ArrayList<>();

    public Gateway(SimpMessagingTemplate template) throws RemoteException {
        super();
        this.template = template;
        this.RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
        try {
            this.server  = (GatewayInt) Naming.lookup("rmi://" + this.RMI_ADDRESS + "/gateway");
            this.server.addClient(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatistics(Set<String> activeBarrels, Map<String, List<Double>> responseTimes, String[] topSearches) throws RemoteException {
        JSONObject json = new JSONObject();
        json.put("activeBarrels", activeBarrels);
        json.put("responseTimes", responseTimes);
        json.put("topSearches", topSearches);
        this.topSearches = new ArrayList<>(List.of(topSearches));
        this.activeBarrels = new ArrayList<>(activeBarrels);
        this.responseTimes = new ArrayList<>();
        for (String barrel : activeBarrels){
            if(responseTimes.containsKey(barrel)) {
               double sum = 0;
               for (Double time : responseTimes.get(barrel)) {
                     sum += time;
               }
               this.responseTimes.add(sum/responseTimes.get(barrel).size());
            }
        }
        template.convertAndSend("/topic/messages", new Message(json.toString()));
    }

    public void displayStatistics(Model model) {
        model.addAttribute("topSearches", topSearches);
        model.addAttribute("activeBarrels", activeBarrels);
        model.addAttribute("responseTimes", responseTimes);
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
            displayStatistics(model);
        } catch (RemoteException e) {
            System.out.println("Error searching");
        }
        return "searches";
    }
}
