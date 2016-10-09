package com.inspur.tools;

import com.inspur.platform.Platform;

public class InspurNetworkInfo {
	private int NetworkType = Platform.NET_DHCP;
	private String IP = "0.0.0.0";
	private String Gatway = "0.0.0.0";
	private String Netmask = "0.0.0.0";
	private String DNS1 = "0.0.0.0";
	private String DNS2 = "0.0.0.0";
	public int getNetworkType() {
		return NetworkType;
	}
	public void setNetworkType(int networkType) {
		NetworkType = networkType;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getGatway() {
		return Gatway;
	}
	public void setGatway(String gatway) {
		Gatway = gatway;
	}
	public String getNetmask() {
		return Netmask;
	}
	public void setNetmask(String netmask) {
		Netmask = netmask;
	}
	public String getDNS1() {
		return DNS1;
	}
	public void setDNS1(String dNS1) {
		DNS1 = dNS1;
	}
	public String getDNS2() {
		return DNS2;
	}
	public void setDNS2(String dNS2) {
		DNS2 = dNS2;
	}
	
}
