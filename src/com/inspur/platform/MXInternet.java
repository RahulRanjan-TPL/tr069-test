package com.inspur.platform;

public class MXInternet {

//    public static String getDefaultGateway() {
//        if (InspurNetwork.isPppoeConn()) {
//            if (GlobalContext.getGlobalContext() == null) {
//                TR069Log.say("eeeeeeeeee fatal  MXInternet error,pppoe InspurNetwork GlobalContext.getGlobalContext() null");
//                return null;
//            }
//            PppoeManager mPppoeManager = (PppoeManager) GlobalContext.getGlobalContext().getSystemService(Context.PPPOE_SERVICE);
//            DhcpInfo info = mPppoeManager.getDhcpInfo();
//            return Formatter.formatIpAddress(info.gateway);
//        }
//        if (InspurNetwork.checkEthernetConn()) {
////          EthernetManager em = (EthernetManager) GlobalContext.getGlobalContext().getSystemService(Context.ETHERNET_SERVICE);
//        	EthernetManager em = EthernetManager.getInstance();
//            DhcpInfo info = em.getDhcpInfo();
//            return Formatter.formatIpAddress(info.gateway);
//        }
//    	Platform platform = Platform.get();
//        return platform.getActiveGatway(platform.getNetWorkType());
//    }

//    public static String getSubnetMask() {
//        if (InspurNetwork.isPppoeConn()) {
//            if (GlobalContext.getGlobalContext() == null) {
//                TR069Log.say("eeeeeeeeee fatal  MXInternet error,pppoe InspurNetwork GlobalContext.getGlobalContext() null");
//                return null;
//            }
//            PppoeManager mPppoeManager = (PppoeManager) GlobalContext.getGlobalContext().getSystemService(Context.PPPOE_SERVICE);
//            DhcpInfo info = mPppoeManager.getDhcpInfo();
//            return Formatter.formatIpAddress(info.netmask);
//        }
//        if (InspurNetwork.checkEthernetConn()) {
////          EthernetManager em = (EthernetManager) GlobalContext.getGlobalContext().getSystemService(Context.ETHERNET_SERVICE);
//        	EthernetManager em = EthernetManager.getInstance();
//            DhcpInfo info = em.getDhcpInfo();
//            return Formatter.formatIpAddress(info.netmask);
//        }
//    	Platform platform = Platform.get();
//        return platform.getActiveNetmask(platform.getNetWorkType());
//    }

//    public static String getDNSServers() {
//        if (InspurNetwork.isPppoeConn()) {
//            if (GlobalContext.getGlobalContext() == null) {
//                TR069Log.say("eeeeeeeeee fatal  MXInternet error,pppoe InspurNetwork GlobalContext.getGlobalContext() null");
//                return null;
//            }
//            PppoeManager mPppoeManager = (PppoeManager) GlobalContext.getGlobalContext().getSystemService(Context.PPPOE_SERVICE);
//            DhcpInfo info = mPppoeManager.getDhcpInfo();
//            return Formatter.formatIpAddress(info.dns1);
//        }
//        if (InspurNetwork.checkEthernetConn()) {
////          EthernetManager em = (EthernetManager) GlobalContext.getGlobalContext().getSystemService(Context.ETHERNET_SERVICE);
//        	EthernetManager em = EthernetManager.getInstance();
//            DhcpInfo info = em.getDhcpInfo();
//            return Formatter.formatIpAddress(info.dns1);
//        }
//    	Platform platform = Platform.get();
//        return platform.getActiveDNS(platform.getNetWorkType());
//    }

//    public static void setEthStaticNetwork(String ip, String netmask, String gateway, String dns) {
//      EthernetManager em = (EthernetManager) GlobalContext.getGlobalContext().getSystemService(Context.ETHERNET_SERVICE);
//    	EthernetManager em = EthernetManager.getInstance();
//        DhcpInfo info = new DhcpInfo();
//        info.ipAddress = InspurNetwork.stringToInt(ip);
//        info.netmask = InspurNetwork.stringToInt(netmask);
//        info.gateway = InspurNetwork.stringToInt(gateway);
//        info.dns1 = InspurNetwork.stringToInt(dns);
//        TR069Log.say("setEthStaticNetwork ip:" + ip + " netmask:" + netmask + " gateway:" + gateway + " dns1:" + dns);
//        em.setEthernetMode("manual", info);
//
//        em.setEthernetEnabled(false);
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        em.setEthernetEnabled(true);
//    }

//    public static void setDhcpNetwork() {
//      EthernetManager em = (EthernetManager) GlobalContext.getGlobalContext().getSystemService(Context.ETHERNET_SERVICE);
//    	EthernetManager em = EthernetManager.getInstance();
//        em.setEthernetMode("dhcp", new DhcpInfo());
//        TR069Log.say("setDhcpNetwork");
//
//        em.setEthernetEnabled(false);
//        try {
//            Thread.sleep(1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        em.setEthernetEnabled(true);
//    }
//
}
