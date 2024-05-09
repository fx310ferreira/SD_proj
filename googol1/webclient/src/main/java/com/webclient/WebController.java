package com.webclient;
import com.common.GatewayInt;
import com.webclient.beans.Gateway;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

@Controller
public class WebController {
    @Resource(name = "sessionScopedGateway")
    private Gateway gateway;

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Gateway sessionScopedGateway() {
        return new Gateway();
    }

    @GetMapping("/")
    public String redirect() {
        return "redirect:/greeting";
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("othername", "SD");
        System.out.println(gateway);
        return "greeting";
    }

}