
package com.inspur.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemProperties;

import com.inspur.platform.MXDevice;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.InspurNetwork;
import com.inspur.tools.InspurSystemProperties;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;

public class InspurDataOld {
    public static final String Manufacturer = "Inspur";
    public static final String OUI = "F84EAC";
    public static final String ProductClass = android.os.Build.MODEL;
    public static final String SerialNumber = android.os.Build.SERIAL;
    public static final String SoftwareVersion = android.os.Build.VERSION.INCREMENTAL;
    public static final String HardwareVersion = "1.0.0";
    public static final String AdditionalHardwareVersion = android.os.Build.MANUFACTURER;
    public static final String AdditionalSoftwareVersion = android.os.Build.FINGERPRINT;
    public static String UserName = "Mr.Miyh";

    public static final String ErrorCode = "";

    public static void setUserName(String name) {
        UserName = name;
        TR069Log.say("Has been set username=[" + UserName + "]");
    }

    public static boolean checkIsFirstStartup() {
        String is = getInspurValue("isfirststartup");
        if (is == null || is.trim().equals("") || is.equals("yes")) {
            setInspurValue("isfirststartup", "false");
            return true;
        } else {
            setInspurValue("isfirststartup", "false");
            return false;
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        // calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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

        if (key.trim().equalsIgnoreCase("Device.DeviceSummary")) {
            String result = Tr069DataFile.get("Device.DeviceSummary");
            if (result == null || result.trim().equals("")) {
                // result = "Device:1.0[](DeviceSummary:1,DeviceInfo:1,ManagementServer:1,Time:1,LAN:1)";
                result = "inspur ota base on mbox";
                Tr069DataFile.set("Device.DeviceSummary", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.HardwareVersion")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.HardwareVersion");
            if (result == null || result.trim().equals("")) {
                result = HardwareVersion;
                Tr069DataFile.set("Device.DeviceInfo.HardwareVersion", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.SoftwareVersion")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.SoftwareVersion");
            if (result == null || result.trim().equals("")) {
                result = SoftwareVersion;
                Tr069DataFile.set("Device.DeviceInfo.SoftwareVersion", SoftwareVersion);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.AdditionalHardwareVersion")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.AdditionalHardwareVersion");
            if (result == null || result.trim().equals("")) {
                result = AdditionalHardwareVersion;
                Tr069DataFile.set("Device.DeviceInfo.AdditionalHardwareVersion", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.AdditionalSoftwareVersion")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.AdditionalSoftwareVersion");
            if (result == null || result.trim().equals("")) {
                result = AdditionalSoftwareVersion;
                Tr069DataFile.set("Device.DeviceInfo.AdditionalSoftwareVersion", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.ModelID")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.ModelID");
            if (result == null || result.trim().equals("")) {
                result = ProductClass;
                Tr069DataFile.set("Device.DeviceInfo.ModelID", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.ModelName")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.ModelName");
            if (result == null || result.trim().equals("")) {
                result = ProductClass;
                Tr069DataFile.set("Device.DeviceInfo.ModelName", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.ProductClass")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.ProductClass");
            if (result == null || result.trim().equals("")) {
                result = ProductClass;
                Tr069DataFile.set("Device.DeviceInfo.ProductClass", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.Manufacturer")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.Manufacturer");
            if (result == null || result.trim().equals("")) {
                result = Manufacturer;
                Tr069DataFile.set("Device.DeviceInfo.Manufacturer", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.Description")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.Description");
            if (result == null || result.trim().equals("")) {
                result = "inspur IPBS8400 ott box";
                Tr069DataFile.set("Device.DeviceInfo.Description", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.FirstUseDate")) {
            String result = Tr069DataFile.get("Device.DeviceInfo.FirstUseDate");
            if (result == null || result.trim().equals("")) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                result = df.format(new Date());
                Tr069DataFile.set("Device.DeviceInfo.FirstUseDate", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.UpTime") || key.trim().equalsIgnoreCase("Device.LAN.Stats.ConnectionUpTime")) {
            // SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss.Z", Locale.getDefault());
            // String result = df.format(new Date());
            // Tr069DataFile.set("Device.DeviceInfo.UpTime", result);

            return CPEDevice.getUpTime();
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.SerialNumber")) {
            String result = SerialNumber;
            Tr069DataFile.set("Device.DeviceInfo.SerialNumbe", result);

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.ParameterKey")) {
            String result = Tr069DataFile.get("Device.ManagementServer.ParameterKey");
            if (result == null || result.trim().equals("")) {
                result = "12345";
                Tr069DataFile.set("Device.ManagementServer.ParameterKey", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.ConnectionRequestURL")) {
            String result = Tr069DataFile.get("Device.ManagementServer.ConnectionRequestURL");
            // we do not realize it,return null
             if (result == null || result.trim().equals("")) {
             String ip = InspurNetwork.getActiveLocalIp();
             if (ip == null)
             ip = "localhost";
             result = "http://" + ip + ":8080/";
             Tr069DataFile.set("Device.ManagementServer.ConnectionRequestURL", result);
             }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.ConnectionRequestUsername")) {
            String result = Tr069DataFile.get("Device.ManagementServer.ConnectionRequestUsername");
             //we do not realize it,return null
             if (result == null || result.trim().equals("")) {
             result = "admin";
             Tr069DataFile.set("Device.ManagementServer.ConnectionRequestUsername", result);
             }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.ConnectionRequestPassword")) {
            String result = Tr069DataFile.get("Device.ManagementServer.ConnectionRequestPassword");
            // we do not realize it,return null
             if (result == null || result.trim().equals("")) {
             result = "admin";
             Tr069DataFile.set("Device.ManagementServer.ConnectionRequestPassword", result);
             }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.Username")) {
            
            return HuaWeiData.getUsername("");
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.Password")) {

            return HuaWeiData.getPassword("");
        }
        
        if (key.trim().equalsIgnoreCase("Device.TIME.NTPServer")) {
            String result = Tr069DataFile.get("Device.TIME.NTPServer");
            if (result == null || result.trim().equals("")) {
                result = "";
                Tr069DataFile.set("Device.TIME.NTPServer", result);
            }

            // return result;
        }

        // network config
        if (key.trim().equalsIgnoreCase("Device.LAN.DefaultGateway")) {
            return InspurNetwork.getDefaultGateway();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.SubnetMask")) {
            return InspurNetwork.getSubnetMask();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.DNSServers")) {
            return InspurNetwork.getDNSServers();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.AddressingType")) {
            String result = InspurNetwork.getAddressingType();
            return result;
        }
        if (key.trim().equalsIgnoreCase("Device.X_00E0FC.ConnectMode")) {
            String result = InspurNetwork.getConnectMode();
            if (result == null || result.trim().equals("")) {
                // default dhcp
                result = "1";
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.LAN.IPAddress")) {
            String ip = InspurNetwork.getActiveLocalIp();
            if (ip == null) {
                return "";
            } else {
                return ip;
            }
        }

        if (key.trim().equalsIgnoreCase("Device.LAN.MACAddress")) {
            // ip mac
			/*
            String mac = null;
            if (ProductClass.trim().equals("IPBS7200")) {
                mac = InspurSystemProperties.get("ro.mac");
                TR069Log.say("eeeeeeeeee 7200 return mac:" + mac);
                return mac;
            }
            String[] hostIPMac = new String[2];
            hostIPMac[0] = null;
            hostIPMac[1] = null;
            // 只能上报有线mac 否则终端会把他当成两个设备
            InspurNetwork.GetInterfaceIpMac("eth", hostIPMac);
            // TR069Log.say("###EC:host ip = " + hostIPMac[0]);
            // TR069Log.say("###EC:host mac = " + hostIPMac[1]);
            mac = hostIPMac[1];
            TR069Log.say("eeeeeeeeee  return mac:" + mac);
            return hostIPMac[1];*/
			return InspurSystemProperties.get("ro.mac");
        }

        if (key.trim().equalsIgnoreCase("Device.X_00E0FC.STBID")) {
            return android.os.Build.SERIAL;
        }

        if (key.trim().equalsIgnoreCase("Device.X_00E0FC.ServiceInfo.UserID")) {
            return UserName;
        }

        if (key.trim().equalsIgnoreCase("Device.X_00E0FC.ServiceInfo.AuthURL")) {
            String result = Tr069DataFile.get("Device.X_00E0FC.ServiceInfo.AuthURL");
            if (result == null || result.trim().equals("")) {
                result = "http://223.99.188.6:8082/EDS/jsp/AuthenticationURL,http://223.99.188.6:8082/EDS/jsp/AuthenticationURL";
                Tr069DataFile.set("Device.X_00E0FC.ServiceInfo.AuthURL", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CM_OTV.STBInfo.STBID") || key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.STBInfo.STBID")) {
            return android.os.Build.SERIAL;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CM_OTV.ServiceInfo.UserID")) {
			/*
            String result = Tr069DataFile.get("Device.X_CM_OTV.ServiceInfo.UserID");
            if (result == null) {
                result = android.os.Build.SERIAL;
            }*/
            return UserName;
        }
        if (key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.ServiceInfo.UserID")) {
			/*
            String result = Tr069DataFile.get("Device.X_CMCC_OTV.ServiceInfo.UserID");
            if (result == null) {
                result = android.os.Build.SERIAL;
            }*/
            return UserName;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CM_OTV.ServiceInfo.Password") || key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.ServiceInfo.Password")) {
            String result = "666666";
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CM_OTV.ServiceInfo.PPPoEID") || key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.ServiceInfo.PPPoEID")) {
            String result = Tr069DataFile.get("Device.X_CMCC_OTV.ServiceInfo.PPPoEID");
            if (result == null || result.trim().equals("")) {
                result = "undefine";
                Tr069DataFile.set("Device.X_CMCC_OTV.ServiceInfo.PPPoEID", result);
            }
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CM_OTV.ServiceInfo.AuthURL") || key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.ServiceInfo.AuthURL")) {
            String result = Tr069DataFile.get("Device.X_CMCC_OTV.ServiceInfo.AuthURL");
            if (result == null || result.trim().equals("")) {
                result = "http://120.197.230.162:8082/EDS/itv/launcherLogin/" + ProductClass;
                Tr069DataFile.set("Device.X_CMCC_OTV.ServiceInfo.AuthURL", result);
            }

            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.STBInfo.StorageSize") || key.trim().equalsIgnoreCase("Device.X_00E0FC.StorageSize")) {
            String result = "4096M";
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.X_CMCC_OTV.STBInfo.PhyMemSize") || key.trim().equalsIgnoreCase("Device.X_00E0FC.PhyMemSize")) {
            String result = "1024M";
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.NATDetected")) {
            // fix me always true
            return "true";
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.URL") || key.trim().equalsIgnoreCase("Device.ManagementServer.URLBakup")) {
            // TMU is very important! get it or stop forever!
            String result = Tr069DataFile.get("Device.ManagementServer.URL");
			if(result==null){
				result=HuaWeiData.ManagementServerURL;
			}
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.STUNServerAddress")) {
            String result = Tr069DataFile.get("Device.ManagementServer.STUNServerAddress");
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.ManagementServer.STUNServerPort")) {
            String result = Tr069DataFile.get("Device.ManagementServer.STUNServerPort");
            return result;
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.ProcessStatus.CPUUsage")) {
            return CPEDevice.getCPUUsage();
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.MemoryStatus.Free")) {
            return CPEDevice.getFreeMemory();
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.MemoryStatus.Total")) {
            return CPEDevice.getTotalMemory();
        }

        if (key.trim().equalsIgnoreCase("Device.LAN.Stats.TotalPacketsReceived")) {
            return InspurNetwork.getTotalPacketsReceived();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.Stats.TotalBytesReceived")) {
            return InspurNetwork.getTotalBytesReceived();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.Stats.TotalBytesSent")) {
            return InspurNetwork.getTotalBytesSent();
        }
        if (key.trim().equalsIgnoreCase("Device.LAN.Stats.TotalPacketsSent")) {
            return InspurNetwork.getTotalPacketsSent();
        }

        //
        if (key.trim().equalsIgnoreCase("Device.STBService.DownloadTransportProtocols")) {
            return "http";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.StreamingTransportControlProtocols")) {
            return "hls";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.VideoStandards")) {
            return "h264";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.MultiplexTypes")) {
            return "hls";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.AudioStandards")) {
            return "aac";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.StreamingControlProtocols")) {
            return "tcp";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.StreamingTransportProtocols")) {
            return "http";
        }
        if (key.trim().equalsIgnoreCase("Device.STBService.MaxDejitteringBufferSize")) {
            return "2048";
        }

        if (key.trim().equalsIgnoreCase("Device.DeviceInfo.ManufacturerOUI")) {
            return OUI;
        }
        if (key.trim().equalsIgnoreCase("Device.UserInterface.AvailableLanguages")) {
            return "cn,eg";
        }
        if (key.trim().equalsIgnoreCase("Device.UserInterface.CurrentLanguage")) {
            return "cn";
        }//
        if (key.trim().equalsIgnoreCase("Device.X_00E0FC.HDMIConnect")) {
            return MXDevice.isHDMIplugin();
        }
        
        //
        if (key.trim().equalsIgnoreCase("Device.Time.LocalTimeZone")) {
            String result = Tr069DataFile.get("Device.Time.LocalTimeZone");
            if (result == null) {
                result = "GMT +8";
            }
            return result;
        }//
        if (key.trim().equalsIgnoreCase("Device.Time.NTPServer1")) {
            String result = MXDevice.judgeOkNtpServer();
            return result;
        }
        if (key.trim().equalsIgnoreCase("Device.Time.NTPServer2")) {
            String result = Tr069DataFile.get("Device.Time.NTPServer2");
            return result;
        }
        if (key.trim().equalsIgnoreCase("Device.Time.CurrentLocalTime")) {
            String result = MXDevice.getCurrentLocalTime();
            return result;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.PhyMemSize")) {
            String result = MXDevice.getPhyMenSize();
            return result;
//            return 1024 * 1024 + "";
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.StorageSize")) {
            String result = MXDevice.getStorageSize();
            return result;
        }
        
//        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserID")) {
//            String result = MXDevice.getIniInfo("/tmp/sqm.ini", "UserId", "= ");
//            return result;
//        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserID")) {
            
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
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserIDPassword")) {
            
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
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.BrowserURL1")) {
            
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
        
//        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.BrowserURL1")) {
//        	String result = MXDevice.getIniInfo("/tmp/sqm.ini" ,"EpgIP", "= ");
//            return result;
//        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.STBID")) {
        	String result = SystemProperties.get("ro.boot.deviceid", "");
            return result;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEID")) {
        	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "user_name", "=");
//        	TR069Log.say("results:" + result);
            return result;
        }
        
        if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEPassword")) {
        	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "pass_word", "=");
            return result;
        }
        
        
        return Tr069DataFile.get(key);
    }

    public static void setInspurValue(String key, String value) {
        // debug use
        if (key.trim().equalsIgnoreCase("CommandKey")) {
            TR069Log.say("ddddddddddd setInspurValue key:" + key + " value:" + value);
        }
        String old = getInspurValue(key);
        Tr069DataFile.set(key, value);
        if (old == null || (old != null && !old.equals(value))) {
            TR069Log.say("vvvvvvvvvvvv value change,key:" + key + " oldvalue:" + old + ",new value:" + value);
//            ArrayList<String> list = new ArrayList<String>();
//            list.add(key);
            String tmuUrl = InspurData.getInspurValue("Device.ManagementServer.URL");
            if (tmuUrl != null) {
                HuaWeiRMS.ACSInformValueChange(tmuUrl, key);
            }
        }

    }

    public static void updateValues(HashMap<String, String> map) {
        Iterator<Entry<String, String>> iter = map.entrySet().iterator();
        boolean setNetFlag = false;
//        boolean setHeart = false;
//        boolean isUpdateError = false;
        while (iter.hasNext()) {
        	Entry<String, String> entry =  iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue(); 
            
            if (key.trim().equalsIgnoreCase("Device.LAN.AddressingType")) {
                setNetFlag = true;
            }
//            if (key.trim().equalsIgnoreCase("Device.ManagementServer.PeriodicInformEnable")) {
//                setHeart = true;
//            }
//            if (key.trim().equalsIgnoreCase("Device.X_00E0FC.ErrorCodeSwitch")) {
//                isUpdateError = true;
//            }
            
            if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEID")) {
//            	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "user_name", "=");
               
            }
            
            if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.PPPOEPassword")) {
//            	String result = MXDevice.getIniInfo("/data/misc/ppp/pppoe.data", "pass_word", "=");
                
            }
            
            if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserID")) {
                
            	Uri uri = Uri.parse("content://stbconfig/authentication/username");
            	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();

//            	Uri uri = Uri.parse(AuthProvider.PASSWORD_URI);
        		ContentValues values = new ContentValues();
        		values.put("UserID", val);
        		Resolver.insert(uri, values);

            }
            
            if (key.trim().equalsIgnoreCase("Device.X_CU_STB.AuthServiceInfo.UserIDPassword")) {
                
            	Uri uri = Uri.parse("content://stbconfig/authentication/password");
            	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();

//            	Uri uri = Uri.parse(AuthProvider.PASSWORD_URI);
        		ContentValues values = new ContentValues();
        		TR069Log.say("Device.X_CU_STB.AuthServiceInfo.UserIDPassword : valus = " + val);
        		values.put("password", val);
        		Resolver.insert(uri, values);

            }
            
            if (key.trim().equalsIgnoreCase("Device.X_CU_STB.STBInfo.BrowserURL1")) {
                
            	Uri uri = Uri.parse("content://stbconfig/authentication/epg");
            	ContentResolver Resolver = GlobalContext.getGlobalContext().getContentResolver();

//            	Uri uri = Uri.parse(AuthProvider.PASSWORD_URI);
        		ContentValues values = new ContentValues();
        		TR069Log.say("Device.X_CU_STB.STBInfo.BrowserURL1 : valus = " + val);
        		values.put("epg", val);
        		Resolver.insert(uri, values);

            }
           
            setInspurValue(key, val);
        }

        if (setNetFlag) {
            String nettype = Tr069DataFile.get("Device.LAN.AddressingType");
            if (nettype.trim().equalsIgnoreCase("static")) {
                String ip = Tr069DataFile.get("Device.LAN.IPAddress");
                String netmask = Tr069DataFile.get("Device.LAN.SubnetMask");
                String gateway = Tr069DataFile.get("Device.LAN.DefaultGateway");
                String dns = Tr069DataFile.get("Device.LAN.DNSServers");

                InspurNetwork.setEthStaticNetwork(ip, netmask, gateway, dns);
            } else if (nettype.trim().equalsIgnoreCase("dhcp")) {
            	InspurNetwork.setDhcpNetwork();
            }
        }

//        if (setHeart) {
//            InspurTr069Service.heartTask();
//        }
//        if (isUpdateError) {
//            InspurTr069Service.doErrorTask();
//        }
    }
	
	public static String getShandongPlatform(){
		return InspurSystemProperties.get("ro.inspur.platform");
	}
}
