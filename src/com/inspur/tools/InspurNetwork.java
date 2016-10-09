
package com.inspur.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.inspur.platform.Platform;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.tr069.ShellUtils;
import com.inspur.work.TR069Utils;

public class InspurNetwork {

	private static InspurNetworkInfo sNetworkInfo;

    public static void onNetworkChange(){
    	if(!isConnectInternet())
    		return;
    	sNetworkInfo = Platform.get().getNetworkInfo();
    	new Thread(new Runnable() {
			@Override
			public void run() {
				HuaWeiRMS.ACSInformValueChange(TR069Utils.getManagementServer(), TR069Utils.Device.LAN.IPAddress);
			}
		}).start();
    }
    
    public static String getAddressingType() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	switch (sNetworkInfo.getNetworkType()) {
		case Platform.NET_DHCP:
			return "DHCP";
		case Platform.NET_STATIC:
			return "Static";
		case Platform.NET_WIFI :
			return "WIFI";
		case Platform.NET_PPOE :
			return "PPPoE";
		}
    	
        TR069Log.say("eeeeeeeeeeee nothing connect");
        return null;
    }

    public static String getConnectMode() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
       switch (sNetworkInfo.getNetworkType()) {
       case Platform.NET_PPOE:
    	   return "0";
       case Platform.NET_DHCP:
       case Platform.NET_STATIC:
    	   return "1";
       case Platform.NET_WIFI:
    	   return "2";
       }
        return "1";
    }

    public static String getActiveLocalIp() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	
    	return sNetworkInfo.getIP();
    }
    
    public static String getDefaultGateway() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	return sNetworkInfo.getGatway();
    }
    
    public static String getSubnetMask() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	return sNetworkInfo.getNetmask();
    }
    
    public static String getDNSServers() {
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	return sNetworkInfo.getDNS1();
    } 
    
    public static void setEthStaticNetwork(String ip, String netmask, String gateway, String dns) {
    	InspurNetworkInfo info = new InspurNetworkInfo();
    	info.setNetworkType(Platform.NET_STATIC);
    	info.setIP(ip);
    	info.setNetmask(netmask);
    	info.setGatway(gateway);
    	info.setDNS1(dns);
    	
    	Platform.get().setNetworkInfo(info);
    }

    public static void setDhcpNetwork() {
    	InspurNetworkInfo info = new InspurNetworkInfo();
    	info.setNetworkType(Platform.NET_DHCP);
    	Platform.get().setNetworkInfo(info);
    }

    public static boolean isConnectInternet() {
        final ConnectivityManager conManager = (ConnectivityManager) GlobalContext.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    private static String getCmdResult(){
    	
    	
    	if(sNetworkInfo == null)
    		sNetworkInfo = Platform.get().getNetworkInfo();
    	
    	String cmd = Platform.get().getShellCmd(Platform.IFCONFIG_CMD);
    	
    	switch (sNetworkInfo.getNetworkType()) {
		case Platform.NET_DHCP:
		case Platform.NET_STATIC:
			cmd += " eth0";
			break;
		case Platform.NET_PPOE:
			cmd += " ppp0";
			break;
		case Platform.NET_WIFI:
			cmd += " wlan0";
			break;
		}
    	
    	return ShellUtils.execCommand(cmd, true).successMsg;
    }
    
    public static String getTotalPacketsReceived() {
        String key = "RX packets:";
       
//        if (isPppoeConn()) {
//            String cmd = "busybox ifconfig ppp0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkEthernetConn()) {
//            String cmd = "busybox ifconfig eth0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkWifiConn()) {
//            String cmd = "busybox ifconfig wlan0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }

        String result = getCmdResult();
        
        if (result != null && result.contains(key)) {
            int start = result.indexOf(key) + key.length();
            int end = start;
            while (result.charAt(end) != ' ') {
                end++;
            }
            result = result.substring(start, end);
            TR069Log.say("getTotalPacketsReceived:" + result);
        }
        return result;
    }

    public static String getTotalPacketsSent() {
        String key = "TX packets:";
//        if (isPppoeConn()) {
//            String cmd = "busybox ifconfig ppp0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkEthernetConn()) {
//            String cmd = "busybox ifconfig eth0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkWifiConn()) {
//            String cmd = "busybox ifconfig wlan0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }

        String result = getCmdResult();
        
        if (result != null && result.contains(key)) {
            int start = result.indexOf(key) + key.length();
            int end = start;
            while (result.charAt(end) != ' ') {
                end++;
            }
            result = result.substring(start, end);
            TR069Log.say("getTotalPacketsSent:" + result);
        }
        return result;
    }

    public static String getTotalBytesReceived() {
        String key = "RX bytes:";
//        if (isPppoeConn()) {
//            String cmd = "busybox ifconfig ppp0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkEthernetConn()) {
//            String cmd = "busybox ifconfig eth0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkWifiConn()) {
//            String cmd = "busybox ifconfig wlan0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
        
        String result = getCmdResult();

        if (result != null && result.contains(key)) {
            int start = result.indexOf(key) + key.length();
            int end = start;
            while (result.charAt(end) != ' ') {
                end++;
            }
            result = result.substring(start, end);
            TR069Log.say("getTotalBytesReceived:" + result);
        }
        return result;
    }

    public static String getTotalBytesSent() {
        String key = "TX bytes:";
//        if (isPppoeConn()) {
//            String cmd = "busybox ifconfig ppp0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkEthernetConn()) {
//            String cmd = "busybox ifconfig eth0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
//        if (checkWifiConn()) {
//            String cmd = "busybox ifconfig wlan0";
//            result = ShellUtils.execCommand(cmd, true).successMsg;
//        }
        
        String result = getCmdResult();

        if (result != null && result.contains(key)) {
            int start = result.indexOf(key) + key.length();
            int end = start;
            while (result.charAt(end) != ' ') {
                end++;
            }
            result = result.substring(start, end);
            TR069Log.say("getTotalBytesSent:" + result);
        }
        return result;
    }

}
