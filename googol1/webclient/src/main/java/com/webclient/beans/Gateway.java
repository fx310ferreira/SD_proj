package com.webclient.beans;

import com.common.GatewayBarrelInt;
import com.common.GatewayInt;
import com.utils.Utils;

import java.rmi.Naming;

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
}
