package com.inspur.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.inspur.platform.MXDevice;
import com.inspur.platform.Platform;
import com.inspur.tools.InspurNetwork;
import com.inspur.tools.TR069DateManager;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.work.TR069Utils;

public class InspurData {

	public static final String Manufacturer = "Inspur";
    public static final String OUI = "F84EAC";
    public static final String ProductClass = android.os.Build.MODEL;
    public static final String SerialNumber = android.os.Build.SERIAL;
	
	public static boolean checkIsFirstStartup() {
        String is = TR069DateManager.getTr069Value("isfirststartup");
        if (is == null || is.trim().equals("") || is.equals("yes")) {
            setInspurValue("isfirststartup", "false");
            return true;
        } else {
            setInspurValue("isfirststartup", "false");
            return false;
        }
    }
	
	public static String getUTCTime(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return formatter.format(new Date(cal.getTimeInMillis()));
    }
	 
	public static String getInspurValue(String key) {
		String result = getRealTimeValue(key);
		if("".equals(result)){
			result = TR069DateManager.getTr069Value(key);
		}
		return result;
	}
	
	public static Map<String, String> getInspurValue(List<String> keys) {
//		String result = getRealTimeValue(key);
//		if("".equals(result)){
//			result = TR069DateManager.getTr069Value(key);
//		}
		
		Map<String, String> maps = TR069DateManager.getTr069Values(keys);
		if(!keys.isEmpty()){
			for(String key : keys){
//				TR069Log.say("getRealTimeValue key :" + key);
				maps.put(key, getRealTimeValue(key));
			}
		}
		
		return maps;
	}
	
	public static void setInspurValue(String key, String value) {
        // debug use
        if (key.trim().equalsIgnoreCase("CommandKey")) {
            TR069Log.say("ddddddddddd setInspurValue key:" + key + " value:" + value);
        }
        String old = TR069DateManager.getTr069Value(key);
//        Tr069DataFile.set(key, value);
        TR069DateManager.setTr069Value(key, value);
        if(!old.equals(value))
        {
        	TR069Log.say("old value :" + old + ",value change :" + value);
        	String tmuUrl = TR069Utils.getManagementServer();
        	if (tmuUrl != null) {
        		HuaWeiRMS.ACSInformValueChange(tmuUrl, key);
        	}
        }
    }
	
	public static void setInspurValue(Map<String, String> maps) {
		TR069DateManager.setTr069Values(maps);
		
		if(!maps.isEmpty()){
			Set<String> keySet = maps.keySet();
			ArrayList<String> keys = new ArrayList<String>(keySet);
//			keySet.toArray(keys);
			
			for(String key : keys){
				TR069Log.say("key:" + key + ",change value:" + maps.get(key));
			}
			
			String tmuUrl = TR069Utils.getManagementServer();
			if (tmuUrl != null) {
				HuaWeiRMS.ACSInformValueChange(tmuUrl, keys);
			}
		}
		
	}
	
    public static void updateValues(HashMap<String, String> map) {
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        boolean setNetFlag = false;
        while (iter.hasNext()) {
        	Entry<String, String> entry =  iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue(); 
            
            if (key.trim().equalsIgnoreCase(TR069Utils.Device.LAN.AddressingType)) {
                setNetFlag = true;
            }
           
            if(Platform.get().setTr069RealTimeValue(key, val)){
            	iter.remove();
            }
        }
        if (setNetFlag) {
        	String nettype = map.get(TR069Utils.Device.LAN.AddressingType);
        	if (nettype.trim().equalsIgnoreCase("static")) {
        		String ip = map.get(TR069Utils.Device.LAN.IPAddress);
        		map.remove(TR069Utils.Device.LAN.IPAddress);
        		String netmask = map.get(TR069Utils.Device.LAN.SubnetMask);
        		map.remove(TR069Utils.Device.LAN.SubnetMask);
        		String gateway = map.get(TR069Utils.Device.LAN.DefaultGateway);
        		map.remove(TR069Utils.Device.LAN.DefaultGateway);
        		String dns = map.get(TR069Utils.Device.LAN.DNSServers);
        		map.remove(TR069Utils.Device.LAN.DNSServers);
        		
        		InspurNetwork.setEthStaticNetwork(ip, netmask, gateway, dns);
        	} else if (nettype.trim().equalsIgnoreCase("dhcp")) {
        		InspurNetwork.setDhcpNetwork();
        	}
        	map.remove(TR069Utils.Device.LAN.AddressingType);
        }
        
        setInspurValue(map);

    }
	
    
    
	private static String getRealTimeValue(String key){
		
		if(TR069Utils.Device.ManagementServer.ConnectionRequestURL.equals(key)){
			String ip = InspurNetwork.getActiveLocalIp();
			if(ip.equals(""))
				ip = "localhost";
			return "http://" + ip + ":8080/";
		}
		
		if (TR069Utils.Device.LAN.DefaultGateway.equalsIgnoreCase(key)) {
            return InspurNetwork.getDefaultGateway();
        }
        if (TR069Utils.Device.LAN.SubnetMask.equalsIgnoreCase(key)) {
            return InspurNetwork.getSubnetMask();
        }
        if (TR069Utils.Device.LAN.DNSServers.equalsIgnoreCase(key)) {
            return InspurNetwork.getDNSServers();
        }
        if (TR069Utils.Device.LAN.AddressingType.equalsIgnoreCase(key)) {
            String result = InspurNetwork.getAddressingType();
            return result;
        }
        if (TR069Utils.Device.X_00E0FC.ConnectMode.equalsIgnoreCase(key)) {
            String result = InspurNetwork.getConnectMode();
            return result;
        }
        if (TR069Utils.Device.LAN.IPAddress.equalsIgnoreCase(key)) {
            String ip = InspurNetwork.getActiveLocalIp();
            return ip;
        }
        if (TR069Utils.Device.DeviceInfo.ProcessStatus.CPUUsage.equalsIgnoreCase(key)) {
            return CPEDevice.getCPUUsage();
        }
        if (TR069Utils.Device.DeviceInfo.MemoryStatus.Free.equalsIgnoreCase(key)) {
            return CPEDevice.getFreeMemory();
        }
        if (TR069Utils.Device.DeviceInfo.MemoryStatus.Total.equalsIgnoreCase(key)) {
            return CPEDevice.getTotalMemory();
        }
        if (TR069Utils.Device.LAN.Stats.TotalPacketsReceived.equalsIgnoreCase(key)) {
            return InspurNetwork.getTotalPacketsReceived();
        }
        if (TR069Utils.Device.LAN.Stats.TotalBytesReceived.equalsIgnoreCase(key)) {
            return InspurNetwork.getTotalBytesReceived();
        }
        if (TR069Utils.Device.LAN.Stats.TotalBytesSent.equalsIgnoreCase(key)) {
            return InspurNetwork.getTotalBytesSent();
        }
        if (TR069Utils.Device.LAN.Stats.TotalPacketsSent.equalsIgnoreCase(key)) {
            return InspurNetwork.getTotalPacketsSent();
        }
       
//        TODO
        if (key.trim().equalsIgnoreCase("Device.Time.NTPServer1")) {
            String result = MXDevice.judgeOkNtpServer();
            return result;
        }
//        if (key.trim().equalsIgnoreCase("Device.Time.NTPServer2")) {
//            String result = Tr069DataFile.get("Device.Time.NTPServer2");
//            return result;
//        }
        if (TR069Utils.Device.Time.CurrentLocalTime.equalsIgnoreCase(key)) {
            String result = MXDevice.getCurrentLocalTime();
            return result;
        }
        if (TR069Utils.Device.X_CU_STB.STBInfo.PhyMemSize.equalsIgnoreCase(key)) {
            String result = MXDevice.getPhyMenSize();
            return result;
        }
        if (TR069Utils.Device.X_CU_STB.STBInfo.StorageSize.equalsIgnoreCase(key)) {
            String result = MXDevice.getStorageSize();
            return result;
        }
       
        
        
		return Platform.get().getTr069RealTimeValue(key);
	}
}
