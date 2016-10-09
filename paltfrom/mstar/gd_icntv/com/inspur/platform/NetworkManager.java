package com.inspur.platform;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.inspur.tools.InspurNetworkInfo;
import com.mstar.android.ethernet.EthernetManager;
import com.mstar.android.pppoe.PppoeManager;

public class NetworkManager {
	
	private PppoeManager mPppoeManager;
	private EthernetManager mEthernetManager;
	private WifiManager mWifiManager;
	
	public NetworkManager(Context context){
		mPppoeManager = PppoeManager.getInstance();
		mEthernetManager = EthernetManager.getInstance();
		
		if(context != null)
			mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
	}
	
	public InspurNetworkInfo getNetworkInfo(int iType){
		InspurNetworkInfo info = new InspurNetworkInfo();
		switch (iType) {
		case Platform.NET_DHCP:
		case Platform.NET_STATIC:
			info.setIP(mEthernetManager.getSavedConfig().getIpAddress());
			info.setGatway(mEthernetManager.getSavedConfig().getRouteAddr());
			info.setNetmask(mEthernetManager.getSavedConfig().getNetMask());
			info.setDNS1(mEthernetManager.getSavedConfig().getDnsAddr());
			info.setDNS2(mEthernetManager.getSavedConfig().getDns2Addr());
			break;
		case Platform.NET_WIFI :
			info.setIP(getStrIP(mWifiManager.getConnectionInfo().getIpAddress()));
			info.setGatway(getStrIP(mWifiManager.getDhcpInfo().gateway));
			info.setNetmask(getStrIP(mWifiManager.getDhcpInfo().netmask));
			info.setDNS1(getStrIP(mWifiManager.getDhcpInfo().dns1));
			info.setDNS2(getStrIP(mWifiManager.getDhcpInfo().dns2));
			break;
		case Platform.NET_PPOE:
			info.setIP(mPppoeManager.getIpaddr());
			info.setGatway(mPppoeManager.getRoute());
			info.setNetmask(mPppoeManager.getMask());
			info.setDNS1(mPppoeManager.getDns1());
			info.setDNS2(mPppoeManager.getDns2());
			break;
		}
		info.setNetworkType(iType);
		return info;
	}
	
	public void setNetworkInfo(InspurNetworkInfo info){
		switch (info.getNetworkType()) {
		case Platform.NET_DHCP:
			mEthernetManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP, new DhcpInfo());
			break;
		case Platform.NET_STATIC:
			DhcpInfo dhcpInfo = new DhcpInfo();
			dhcpInfo.ipAddress = stringToInt(info.getIP());
			dhcpInfo.gateway = stringToInt(info.getGatway());
			dhcpInfo.netmask = stringToInt(info.getNetmask());
			dhcpInfo.dns1 = stringToInt(info.getDNS1());
			mEthernetManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
			break;
		}
		
//		mEthernetManager.setEnabled(false)
	}
	
	public boolean isPppoeConnect(){
		
		if(mPppoeManager != null)
			return PppoeManager.PPPOE_STATE_CONNECT.equals(mPppoeManager.getPppoeStatus());
		
		return false;
	}
	
	public boolean isEthConnect(){
        
		if(mEthernetManager != null)
			return mEthernetManager.isNetworkConnected();
       return false;
	}
	
	public boolean isWifiConnect(){
		if(mWifiManager != null)
		{
			WifiInfo info = mWifiManager.getConnectionInfo();
			if(info != null && info.getIpAddress() != 0)
			return true;
		}
		return false;
	}
	
	public boolean isEthDHCP(){
		if(mEthernetManager != null)
			return	EthernetManager.ETHERNET_CONNECT_MODE_DHCP.equals(mEthernetManager.getEthernetMode());
		
		return false;
	}
	
	public String getActiveIp(int iType){
		switch (iType) {
		case Platform.NET_DHCP:
//			int iEthIp = mEthernetManager.getDhcpInfo().ipAddress;
//			return getStrIP(iEthIp);
		case Platform.NET_STATIC:
			return mEthernetManager.getSavedConfig().getIpAddress();
		case Platform.NET_WIFI :
			int iWifiIp = mWifiManager.getConnectionInfo().getIpAddress();
			return getStrIP(iWifiIp);
		case Platform.NET_PPOE:
			return mPppoeManager.getIpaddr();
		}
		
		return "";
	}
	
	public String getActiveNetmask(int iType){
		switch (iType) {
		case Platform.NET_DHCP:
//			int iEthIp = mEthernetManager.getDhcpInfo().ipAddress;
//			return getStrIP(iEthIp);
		case Platform.NET_STATIC:
			return mEthernetManager.getSavedConfig().getNetMask();
		case Platform.NET_WIFI :
			int iWifiNetmask = mWifiManager.getDhcpInfo().netmask;
			return getStrIP(iWifiNetmask);
		case Platform.NET_PPOE:
			return mPppoeManager.getMask();
		}
		
		return "";
	}
	
	public String getActiveGatway(int iType){
		switch (iType) {
		case Platform.NET_DHCP:
//			int iEthIp = mEthernetManager.getDhcpInfo().ipAddress;
//			return getStrIP(iEthIp);
		case Platform.NET_STATIC:
			return mEthernetManager.getSavedConfig().getRouteAddr();
		case Platform.NET_WIFI :
			int iWifGatway = mWifiManager.getDhcpInfo().gateway;
			return getStrIP(iWifGatway);
		case Platform.NET_PPOE:
			return mPppoeManager.getRoute();
		}
		
		return "";
	}
	
	public String getActiveDNS(int iType){
		switch (iType) {
		case Platform.NET_DHCP:
//			int iEthIp = mEthernetManager.getDhcpInfo().ipAddress;
//			return getStrIP(iEthIp);
		case Platform.NET_STATIC:
			return mEthernetManager.getSavedConfig().getDnsAddr();
		case Platform.NET_WIFI :
			int iWifDns = mWifiManager.getDhcpInfo().dns1;
			return getStrIP(iWifDns);
		case Platform.NET_PPOE:
			return mPppoeManager.getDns1();
		}
		
		return "";
	}
	
	private String getStrIP(int IP) {
		return ((IP >> 24) & 0xFF) + "." + ((IP >> 16) & 0xFF) + "."
				+ ((IP >> 8) & 0xFF) + "." + (IP & 0xFF);
	}
	
	 private int stringToInt(String addrString) {
	        try {
	            if (addrString == null)
	                return 0;
	            String[] parts = addrString.split("\\.");
	            if (parts.length != 4) {
	                return 0;
	            }

	            int a = Integer.parseInt(parts[0]);
	            int b = Integer.parseInt(parts[1]) << 8;
	            int c = Integer.parseInt(parts[2]) << 16;
	            int d = Integer.parseInt(parts[3]) << 24;

	            return a | b | c | d;
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return 0;
	    }
}
