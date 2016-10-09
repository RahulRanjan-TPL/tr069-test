
package com.inspur.platform;

import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.net.SntpClient;

public class MXDevice {
    private static String ZhejiangNTPServer = "112.17.8.11";
    private static Timer ntptimer = null;
    private static TimerTask ntptask = null;

    public static void startNtpwork() {
        if (ntptimer != null) {
            ntptimer.cancel();
        }
        ntptimer = new Timer();
        ntptask = new TimerTask() {
            public void run() {
                String ntp = judgeOkNtpServer();
                if (ntp != null && !ntp.trim().equals("")) {
                    updateNtp(ntp);
                }
            }
        };
        // 每2个小时更新一次
        ntptimer.schedule(ntptask, 0, 2 * 60 * 60 * 1000);
    }

    public static String judgeOkNtpServer() {
        // 连续尝试三个NTP服务器
        String ntp;
        boolean isok = false;
        ntp = Tr069DataFile.get("Device.Time.NTPServer1");
        if (ntp != null && !ntp.equals("null") && !ntp.trim().equals("")) {
            isok = updateNtp(ntp);
        }
        if (isok)
            return ntp;
        ntp = Tr069DataFile.get("Device.Time.NTPServer2");
        if (ntp != null && !ntp.equals("null") && !ntp.trim().equals("")) {
            isok = updateNtp(ntp);
        }
        if (isok)
            return ntp;
        ntp = ZhejiangNTPServer;
        if (ntp != null && !ntp.equals("null") && !ntp.trim().equals("")) {
            isok = updateNtp(ntp);
        }
        if (isok) {
            // 更新主的ntp server地址,忽略掉错误的ntp 服务器设置。
            Tr069DataFile.set("Device.Time.NTPServer1", ntp);
            return ntp;
        }
        return null;
    }

    public static boolean updateNtp(String ntpServer) {
        SntpClient client = new SntpClient();
        int tryget = 0;
        boolean flag = false;
        while (!flag) {
            TR069Log.say("========update ntp:" + ntpServer + " try:" + tryget);
            flag = client.requestTime(ntpServer, 2000);
            if (tryget++ >= 3)
                break;
        }
        if (flag == false) {
            TR069Log.say("========update ntp:" + ntpServer + " fail,return");
            return false;
        }

        TR069Log.say("========update ntp:" + ntpServer + " ok,ready set time");
        long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
        SystemClock.setCurrentTimeMillis(now);
        return true;
    }

    public static String isHDMIplugin() {
//        String status = readSysfs("/sys/class/amhdmitx/amhdmitx0/hpd_state");
    	String status = SystemProperties.get("mstar.hdmiplugin.status", "0");
//        if ("1".equals(status)) {
//            TR069Log.say("isHDMIplugin true");
//            return "1";
//        } else {
//            TR069Log.say("isHDMIplugin false");
//            return "0";
//        }

        return status;
    }

//    private static String readSysfs(String path) {
//        if (!new File(path).exists()) {
//            TR069Log.say("fatal error,File not found: " + path);
//            return null;
//        }
//        String str = null;
//        StringBuilder value = new StringBuilder();
//        try {
//            FileReader fr = new FileReader(path);
//            BufferedReader br = new BufferedReader(fr);
//            while ((str = br.readLine()) != null) {
//                if (str != null)
//                    value.append(str);
//            }
//            fr.close();
//            br.close();
//            return value.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static String getCurrentLocalTime() {
        // TODO Auto-generated method stub
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");// 设置日期格式
        return df.format(new Date());
    }

    public static String getPhyMenSize()
    {
//    	ActivityManager am=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
    	ActivityManager am=(ActivityManager) GlobalContext.getGlobalContext().getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi=new MemoryInfo();  
        am.getMemoryInfo(mi);

        
        return getMemoryInteger(mi.totalMem) * 1024 * 1024 + "KB";
    }
    
    public static String getStorageSize()
    {
    	 StatFs statFsData =new StatFs("/data");
         StatFs statFsSystem =new StatFs("/system");
         StatFs statFsCache =new StatFs("/cache");
         StatFs statFsdev =new StatFs("/dev");
         
         
         
         
         return getMemoryInteger(statFsData.getTotalBytes() 
        		 + statFsSystem.getTotalBytes() 
        		 + statFsCache.getTotalBytes() 
        		 + statFsdev.getTotalBytes()) * 1024 * 1024 + "KB";
//         String[] total=fileSize(totalBlocks*blockSize);    
//         String[] available=fileSize(availableBlocks*blockSize);    
//             
//         rOMTextView.setText("ROM "+available[0]+available[1]+"/"+total[0]+total[1]);  
    }
    
//    public static boolean setIniInfo(String filePath, String key, String value, String split)
//    {
//    	File file = new File(filePath);
//    	
//    	if(file.exists())
//    	{
//    		BufferedReader bufferedReader = null;
//			try {
//				FileReader fileReader = new FileReader(file);
//				bufferedReader = new BufferedReader(fileReader);
//				
//				String line;
//				
//				while((line = bufferedReader.readLine()) != null)
//				{
//					if(line.contains(key))
//					{
//						String[] value = line.split(split);
//						result = value[1];
//						break;
//					}
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (Exception e)
//			{
//				e.printStackTrace();
//			}finally{
//				if(bufferedReader !=null)
//				{
//					try {
//						bufferedReader.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//    	}
//    }
    
    public static String getIniInfo(String filePath, String key, String split)
    {
    	String result = "";
//    	String sqmFilePath = "/tmp/sqm.ini";
    	File file = new File(filePath);
    	
//    	TR069Log.say("getIniInfo:" + filePath);
    	
    	if(file.exists())
    	{
    		BufferedReader bufferedReader = null;
			try {
				FileReader fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);
				
				String line;
				
				while((line = bufferedReader.readLine()) != null)
				{
					if(line.contains(key))
					{
						String[] value = line.split(split);
						result = value[1];
//						TR069Log.say("result:" + result);
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e)
			{
				e.printStackTrace();
			}finally{
				if(bufferedReader !=null)
				{
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
    	}
    	
    	return result;
    }
    
    //获取 以G为单位的内存大小
    public static int getMemoryInteger(long size)
    {
    	float sizeGb = (float)size / (float) (1024 * 1024 * 1024);
    	
    	return (int) Math.ceil(sizeGb);
    }
}
