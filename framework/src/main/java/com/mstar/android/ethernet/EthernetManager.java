package com.mstar.android.ethernet;

import android.net.DhcpInfo;

/**
 * Created by Inpur on 2016/10/8.
 */

public class EthernetManager {

    public static final String ETHERNET_CONNECT_MODE_DHCP = "";
    public static final String ETHERNET_CONNECT_MODE_MANUAL = "manual";

    public static EthernetManager getInstance(){
        return null;
    }

    public DhcpInfo getDhcpInfo(){
        return null;
    }

    public EthernetDevInfo getSavedConfig(){
        return null;
    }

    public boolean isNetworkConnected(){
        return false;
    }

    public void setEthernetMode(String mode, DhcpInfo info){

    }

    public String getEthernetMode(){
        return "";
    }
}
