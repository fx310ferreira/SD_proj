package com.webclient;

import com.common.GatewayInt;
import com.utils.Utils;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.rmi.Naming;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @PostMapping("/index")
    public ResponseEntity<String> index(@RequestBody String body) {
        try{
            String url = body;
            System.out.println("Indexing " + url);
            String RMI_ADDRESS = Utils.readProperties(this, "RMI_ADDRESS", "localhost");
            GatewayInt gateway = (GatewayInt) Naming.lookup("rmi://" + RMI_ADDRESS + "/gateway");
            if (url.startsWith("http://") || url.startsWith("https://")) {
                if (gateway.indexURL(url)) {
                    return ResponseEntity.ok("Indexing " + url);
                } else {
                    return ResponseEntity.status(500).body("Error indexing " + url);
                }
            }
            return ResponseEntity.status(400).body("Invalid URL: " + url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error indexing connection failed");
        }
    }
}
