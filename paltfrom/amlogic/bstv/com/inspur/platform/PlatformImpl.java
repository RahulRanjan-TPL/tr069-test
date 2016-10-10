package com.inspur.platform;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemProperties;

import com.inspur.config.CPEDevice;
import com.inspur.config.HuaWeiData;
import com.inspur.platform.Platform;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.InspurNetworkInfo;
import com.inspur.tools.TR069Log;
import com.inspur.work.TR069Utils;

public class PlatformImpl extends Platform{

//	private Context mContext;
	private NetworkManager mNetworkManager;
	
	public PlatformImpl(Context context){
//		mContext = context;
		mNetworkManager = new NetworkManager(context);
	}
	
	@Override
	public String getShellCmd(int type) {
		switch (type) {
		case LOGCAT_CMD:
			return "logcat -d";
		case TCPDUMP_CMD:
			return "tcpdump";
		case PING_CMD:
			return "ping";
		case TRACEROUTE_CMD:
			return "busybox traceroute";
		case KILLALL_CMD:
			return "busybox killall";
		case NETCFG_CMD:
			return "netcfg";
		case IFCONFIG_CMD:
			return "busybox ifconfig";
		default:
			break;
		}
		return "";
	}
	
	@Override
	public InspurNetworkInfo getNetworkInfo(){
		return mNetworkManager.getNetworkInfo(getNetWorkType());
	}
	
	@Override
	public void setNetworkInfo(InspurNetworkInfo info) {
		mNetworkManager.setNetworkInfo(info);
	}
	
//	@Override
	private int getNetWorkType() {
		
		if(mNetworkManager.isPppoeConnect())
			return NET_PPOE;
		
		if(mNetworkManager.isEthConnect())
		{
			if(mNetworkManager.isEthDHCP())
				return NET_DHCP;
			return NET_STATIC;
		}
		if(mNetworkManager.isWifiConnect())
			return NET_WIFI;
		
		return -1;
	}

	@Override
	public Map<String, String> getTr069DefaultValue() {
		Map<String, String> maps = new HashMap<String, String>();
		maps.put(TR069Utils.Device.DeviceSummary, "inspur ota base on mbox");
		maps.put(TR069Utils.Device.STBService.DownloadTransportProtocols, "http");
		maps.put(TR069Utils.Device.STBService.StreamingTransportControlProtocols, "hls");
		maps.put(TR069Utils.Device.STBService.VideoStandards, "h264");
		maps.put(TR069Utils.Device.STBService.MultiplexTypes, "hls");
		maps.put(TR069Utils.Device.STBService.AudioStandards, "acc");
		maps.put(TR069Utils.Device.STBService.StreamingControlProtocols, "TCP");
		maps.put(TR069Utils.Device.STBService.StreamingTransportProtocols, "http");
		maps.put(TR069Utils.Device.STBService.MaxDejitteringBufferSize, "2048");
		maps.put(TR069Utils.Device.UserInterface.AvailableLanguages, "cn,eg");
		maps.put(TR069Utils.Device.UserInterface.CurrentLanguage, "cn");
		maps.put(TR069Utils.Device.Time.LocalTimeZone, "CMT +8");
		maps.put(TR069Utils.Device.DeviceInfo.HardwareVersion, "1.0.0");
		maps.put(TR069Utils.Device.DeviceInfo.SoftwareVersion, android.os.Build.VERSION.INCREMENTAL);
		maps.put(TR069Utils.Device.DeviceInfo.AdditionalHardwareVersion, android.os.Build.MANUFACTURER);
		maps.put(TR069Utils.Device.DeviceInfo.AdditionalSoftwareVersion, android.os.Build.FINGERPRINT);
		maps.put(TR069Utils.Device.DeviceInfo.ModelName, android.os.Build.MODEL);
		maps.put(TR069Utils.Device.DeviceInfo.ModelID, android.os.Build.MODEL);
		maps.put(TR069Utils.Device.DeviceInfo.ProductClass, android.os.Build.MODEL);
		maps.put(TR069Utils.Device.DeviceInfo.Manufacturer, "Inspur");
		maps.put(TR069Utils.Device.DeviceInfo.ManufacturerOUI, "F84EAC");
		maps.put(TR069Utils.Device.DeviceInfo.Description, "inspur IPBS8400 ott box");
		maps.put(TR069Utils.Device.DeviceInfo.SerialNumber, android.os.Build.SERIAL);
		maps.put(TR069Utils.Device.DeviceInfo.UpTime, CPEDevice.getUpTime());
		maps.put(TR069Utils.Device.ManagementServer.URL, HuaWeiData.ManagementServerURL);
		maps.put(TR069Utils.Device.ManagementServer.NATDetected, "true");
		maps.put(TR069Utils.Device.ManagementServer.ParameterKey, "12345");
		maps.put(TR069Utils.Device.ManagementServer.ConnectionRequestUsername, "admin");
		maps.put(TR069Utils.Device.ManagementServer.ConnectionRequestPassword, "admin");
		maps.put(TR069Utils.Device.ManagementServer.Username, HuaWeiData.getUsername(""));
		maps.put(TR069Utils.Device.ManagementServer.Password, HuaWeiData.getPassword(""));
		maps.put(TR069Utils.Device.LAN.MACAddress, SystemProperties.get("ro.mac", "00:00:00:00:00:00"));
		maps.put(TR069Utils.Device.X_00E0FC.ServiceInfo.AuthURL, "http://223.99.188.6:8082/EDS/jsp/AuthenticationURL,http://223.99.188.6:8082/EDS/jsp/AuthenticationURL");
		maps.put(TR069Utils.Device.X_00E0FC.STBID, android.os.Build.SERIAL);
		maps.put(TR069Utils.Device.X_CU_STB.STBInfo.STBID, SystemProperties.get("ro.boot.deviceid", "123456"));
		
		
		return maps;
	}

	@Override
	public String getTr069RealTimeValue(String key) {
		 if (TR069Utils.Device.X_CU_STB.AuthServiceInfo.UserID.equalsIgnoreCase(key)) {
        	String username = "";
        	Uri uri = Uri.parse("content://stbconfig/authentication/username");
			Cursor cursor = GlobalContext.getGlobalContext().getContentResolver().query(uri, null, null, null,null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					username = cursor.getString(0);
				}
				cursor.close();
			}
            return username;
        }
        if (TR069Utils.Device.X_CU_STB.AuthServiceInfo.UserIDPassword.equalsIgnoreCase(key)) {
        	String password = "";
        	Uri uri = Uri.parse("content://stbconfig/authentication/password");
			Cursor cursor = GlobalContext.getGlobalContext().getContentResolver().query(uri, null, null, null,null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					password = cursor.getString(0);
				}
				cursor.close();
			}
            return password;
        }
        if (TR069Utils.Device.X_CU_STB.STBInfo.BrowserURL1.equalsIgnoreCase(key)) {
        	String epgUrl = "";
        	Uri uri = Uri.parse("content://stbconfig/authentication/epg");
			Cursor cursor = GlobalContext.getGlobalContext().getContentResolver().query(uri, null, null, null,null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					epgUrl = cursor.getString(0);
					String[] m_tmp =epgUrl.split(":");
					epgUrl=m_tmp[1].substring(2, m_tmp[1].length());
					epgUrl = cursor.getString(0);
				}
				cursor.close();
			}
            return epgUrl;
        }
        if (TR069Utils.Device.X_CU_STB.AuthServiceInfo.PPPOEID.equalsIgnoreCase(key)) {
        	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "user_name", "=");
            return result;
        }
        if (TR069Utils.Device.X_CU_STB.AuthServiceInfo.PPPOEPassword.equalsIgnoreCase(key)) {
        	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "pass_word", "=");
            return result;
        }
        
        if (TR069Utils.Device.X_00E0FC.HDMIConnect.equalsIgnoreCase(key)) {
            return SystemProperties.get("mstar.hdmiplugin.status", "0");
        }
        
		return "";
	}

	@Override
	public boolean setTr069RealTimeValue(String key, String value) {
		
		if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEID")) {
            return true;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEPassword")) {
        	return true;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserID")) {
        	Uri uri = Uri.parse("content://stbconfig/authentication/username");
        	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();
    		ContentValues values = new ContentValues();
    		values.put("UserID", value);
    		Resolver.insert(uri, values);
    		return true;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserIDPassword")) {
        	Uri uri = Uri.parse("content://stbconfig/authentication/password");
        	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();
    		ContentValues values = new ContentValues();
    		TR069Log.say("Device.X_CU_STB.AuthServiceInfo.UserIDPassword : valus = " + value);
    		values.put("password", value);
    		Resolver.insert(uri, values);
    		return true;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.BrowserURL1")) {
        	Uri uri = Uri.parse("content://stbconfig/authentication/epg");
        	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();
    		ContentValues values = new ContentValues();
    		TR069Log.say("Device.X_CU_STB.STBInfo.BrowserURL1 : valus = " + value);
    		values.put("epg", value);
    		Resolver.insert(uri, values);
    		return true;
        }
		
		return false;
	}


	
}
