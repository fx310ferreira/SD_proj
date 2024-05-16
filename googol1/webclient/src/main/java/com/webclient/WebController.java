package com.webclient;
import com.webclient.beans.Gateway;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import java.rmi.RemoteException;

@Controller
public class WebController {

    @Autowired
    private SimpMessagingTemplate template;

    @Resource(name = "appScopedGateway")
    private Gateway gateway;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Gateway appScopedGateway() {
        try {
            return new Gateway(template);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping
    public String googol(Model model) {
        if (gateway == null) {
            //todo retry connection
            return "redirect:/";
        }
        return "landing";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q") String search, @RequestParam(name = "page", required = false, defaultValue = "0") int page, Model model) {
        model.addAttribute("query", search);
        model.addAttribute("nextPage", String.valueOf(Integer.valueOf(page) + 1));
        model.addAttribute("prevPage", String.valueOf(Integer.valueOf(page) - 1));
        if (gateway == null) {
            //todo retry connection
            return "redirect:/";
        }
        return gateway.query(search, page, model);
    }

}