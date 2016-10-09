package com.inspur.platform;

import java.util.Map;

import com.inspur.tools.GlobalContext;
import com.inspur.tools.InspurNetworkInfo;

public abstract class Platform {

	final public static int NET_PPOE = 0;
	final public static int NET_DHCP = 1;
	final public static int NET_WIFI = 3;
	final public static int NET_STATIC = 4;
	
	final public static int LOGCAT_CMD = 0;
	final public static int TCPDUMP_CMD = 1;
	final public static int NETCFG_CMD = 2;
	final public static int KILLALL_CMD = 3;
	final public static int IFCONFIG_CMD = 4;
	final public static int PING_CMD = 5;
	final public static int TRACEROUTE_CMD = 6;
	
	private static Platform sInstance;
	private static Object sLock = new Object();
	
	public static Platform get(){
		synchronized(sLock){
			if(sInstance == null){
//				String platfrom = SystemProperties.get("ro.product.brand","");
//				if("mstar".equals(platfrom.toLowerCase().trim()))
				sInstance = new PlatformImpl(GlobalContext.getGlobalContext());
			}
		}
		return sInstance;
	}
	
	public abstract String getShellCmd(int type);
//	public abstract String getLogcatCmd();
//	public abstract String getNetCaptureCmd();
//	public abstract String getNetConfigCmd();
//	public abstract String getKillByNameCmd();
//	public abstract String getIfconfigCmd();
//	public abstract String getPingCmd();
//	public abstract String getTracerouteCmd();
	
	public abstract InspurNetworkInfo getNetworkInfo();
	public abstract void setNetworkInfo(InspurNetworkInfo info);
//	public abstract int    getNetWorkType();
//	public abstract String getActiveIP(int iType);
//	public abstract String getActiveNetmask(int iType);
//	public abstract String getActiveGatway(int iType);
//	public abstract String getActiveDNS(int iType);
	
	public abstract Map<String, String> getTr069DefaultValue();
	public abstract String getTr069RealTimeValue(String key);
	public abstract boolean setTr069RealTimeValue(String key, String value);
}
