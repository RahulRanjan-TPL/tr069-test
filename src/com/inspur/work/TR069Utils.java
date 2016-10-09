package com.inspur.work;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.RecoverySystem;

import com.inspur.config.HuaWeiData;
import com.inspur.config.InspurData;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.HttpDowmload;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.work.bandwidth.BandwidthWork;
import com.inspur.work.logcapture.LogCaptureWork;
import com.inspur.work.networkcapture.NetworkCaptureWork;
import com.inspur.work.onekeyinfo.OneKeyWork;
import com.inspur.work.ping.PingWork;
import com.inspur.work.playdiagnose.PlaydiagnoseWork;
import com.inspur.work.startupinfo.StartupInfoWork;
import com.inspur.work.traceRoute.TraceRouteWork;

public class TR069Utils {
	
	final public static String FactoryReset = "FactoryReset";
	final public static String FactoryResetTrue = "true";
	final public static String FactoryresetFalse = "false";
	final public static String Reboot = "Reboot";
	final public static String RebootTrue = "true";
	final public static String RebootFalse = "false";
//	final public static String STB_MAC_KEY = "Device.LAN.MACAddress";
//	final public static String STB_PRODUCT_KEY = "Device.DeviceInfo.ProductClass";
//	final public static String STB_SW_KEY = "Device.DeviceInfo.SoftwareVersion";
//	final public static String STB_AUTH_USERID = "Device.X_CU_STB.AuthServiceInfo.UserID";
	
	public static class Device{
		final public static String DeviceSummary = "Device.DeviceSummary";
		
		public static class DeviceInfo{
			final public static String HardwareVersion = "Device.DeviceInfo.HardwareVersion";
			final public static String SoftwareVersion = "Device.DeviceInfo.SoftwareVersion";
			final public static String AdditionalHardwareVersion = "Device.DeviceInfo.AdditionalHardwareVersion";
			final public static String AdditionalSoftwareVersion = "Device.DeviceInfo.AdditionalSoftwareVersion";
			final public static String ModelName = "Device.DeviceInfo.ModelName";
			final public static String ModelID = "Device.DeviceInfo.ModelID";
			final public static String Description = "Device.DeviceInfo.Description";
			final public static String FirstUseDate = "Device.DeviceInfo.FirstUseDate";
			final public static String UpTime = "Device.DeviceInfo.UpTime";
			final public static String SerialNumber = "Device.DeviceInfo.SerialNumber";
			final public static String ProductClass = "Device.DeviceInfo.ProductClass";
			final public static String ManufacturerOUI = "Device.DeviceInfo.ManufacturerOUI";
			final public static String Manufacturer = "Device.DeviceInfo.Manufacturer";
			
			public static class ProcessStatus{
				final public static String CPUUsage = "Device.DeviceInfo.ProcessStatus.CPUUsage";
				
			}
			
			public static class MemoryStatus{
				final public static String Free = "Device.DeviceInfo.MemoryStatus.Free";
				final public static String Total = "Device.DeviceInfo.MemoryStatus.Total";
				
			}
		}
		
		public static class UserInterface{
			final public static String AvailableLanguages = "Device.UserInterface.AvailableLanguages";
			final public static String CurrentLanguage = "Device.UserInterface.CurrentLanguage";
			
		}
		
		public static class Time{
			final public static String LocalTimeZone = "Device.Time.LocalTimeZone";
			final public static String CurrentLocalTime = "Device.Time.CurrentLocalTime";
			final public static String NTPServer1 = "Device.Time.NTPServer1";
			final public static String NTPServer2 = "Device.Time.NTPServer2";
			
		}
		
		public static class ManagementServer{
			final public static String URL = "Device.ManagementServer.URL";
			final public static String URLModifyFlag = "Device.ManagementServer.URLModifyFlag";
           // ParameterHelp(serializer, "Device.ManagementServer.URLBakup");
			final public static String ParameterKey = "Device.ManagementServer.ParameterKey";
			final public static String NATDetected = "Device.ManagementServer.NATDetected";
			final public static String UDPConnectionRequestAddress = "Device.ManagementServer.UDPConnectionRequestAddress";
			final public static String ConnectionRequestURL = "Device.ManagementServer.ConnectionRequestURL";
			final public static String ConnectionRequestUsername = "Device.ManagementServer.ConnectionRequestUsername";
			final public static String ConnectionRequestPassword = "Device.ManagementServer.ConnectionRequestPassword";
			
			final public static String PeriodicInformEnable = "Device.ManagementServer.PeriodicInformEnable";
			final public static String PeriodicEnable = "1";
			final public static String PeriodicDisenable = "0";
			final public static String PeriodicInformInterval = "Device.ManagementServer.PeriodicInformInterval";
			
			final public static String STUNEnable = "Device.ManagementServer.STUNEnable";
			final public static String STUNServerAddress = "Device.ManagementServer.STUNServerAddress";
			final public static String Undefine = "undefine";
			final public static String STUNServerPort = "Device.ManagementServer.STUNServerPort";
			
			final public static String Username = "Device.ManagementServer.Username";
			final public static String Password = "Device.ManagementServer.Password";
			
			
		}
		
		public static class LAN{
			final public static String IPAddress = "Device.LAN.IPAddress";
			final public static String MACAddress = "Device.LAN.MACAddress";
			final public static String DefaultGateway = "Device.LAN.DefaultGateway";
			final public static String SubnetMask = "Device.LAN.SubnetMask";
			final public static String DNSServers = "Device.LAN.DNSServers";
			final public static String AddressingType = "Device.LAN.AddressingType";
			
			public static class IPPingDiagnostics{
				final public static String NumberOfRepetitions = "Device.LAN.IPPingDiagnostics.NumberOfRepetitions";
				final public static String DataBlockSize = "Device.LAN.IPPingDiagnostics.DataBlockSize";
				final public static String Timeout = "Device.LAN.IPPingDiagnostics.Timeout";
				final public static String Host = "Device.LAN.IPPingDiagnostics.Host";
				final public static String DiagnosticsState = "Device.LAN.IPPingDiagnostics.DiagnosticsState";
				final public static String StateStop = "STOP";
				final public static String StateRequested = "Requested";
				final public static String StateComplete = "Complete";
				final public static String SuccessCount = "Device.LAN.IPPingDiagnostics.SuccessCount";
				final public static String FailureCount = "Device.LAN.IPPingDiagnostics.FailureCount";
				final public static String AverageResponseTime = "Device.LAN.IPPingDiagnostics.AverageResponseTime";
				final public static String MaximumResponseTime = "Device.LAN.IPPingDiagnostics.MaximumResponseTime";
				final public static String MinimumResponseTime = "Device.LAN.IPPingDiagnostics.MinimumResponseTime";
				
			}
			
			public static class TraceRouteDiagnostics{
				final public static String DiagnosticsState = "Device.LAN.TraceRouteDiagnostics.DiagnosticsState";
				final public static String StateStop = "stop";
				final public static String StateRequested = "Requested";
				final public static String Host = "Device.LAN.TraceRouteDiagnostics.Host";
				final public static String MaxHopCount = "Device.LAN.TraceRouteDiagnostics.MaxHopCount";
				final public static String Timeout = "Device.LAN.TraceRouteDiagnostics.Timeout";
				
				final public static String ResponseTime = "Device.LAN.TraceRouteDiagnostics.ResponseTime";
				final public static String NumberOfRouteHops =  "Device.LAN.TraceRouteDiagnostics.NumberOfRouteHops";
			}
			
			public static class Stats{
				final public static String TotalPacketsReceived =  "Device.LAN.Stats.TotalPacketsReceived";
				final public static String TotalBytesReceived =  "Device.LAN.Stats.TotalBytesReceived";
				final public static String TotalBytesSent =  "Device.LAN.Stats.TotalBytesSent";
				final public static String TotalPacketsSent =  "Device.LAN.Stats.TotalPacketsSent";
				
			}
		}
		
		public static class X_00E0FC{
			final public static String STBID = "Device.X_00E0FC.STBID";
			final public static String ConnectMode = "Device.X_00E0FC.ConnectMode";
			final public static String HDMIConnect = "Device.X_00E0FC.HDMIConnect";
			final public static String IsEncryptMark = "Device.X_00E0FC.IsEncryptMark";
			final public static String ErrorCodeSwitch = "Device.X_00E0FC.ErrorCodeSwitch";
			final public static String ErrorSwitchOn = "1";
			final public static String ErrorSwitchOff = "0";
			final public static String ErrorCodeInterval = "Device.X_00E0FC.ErrorCodeInterval";
			
			public static class LogParaConfiguration{
				final public static String LogOutPutType = "Device.X_00E0FC.LogParaConfiguration.LogOutPutType";
				final public static String SyslogServer = "Device.X_00E0FC.LogParaConfiguration.SyslogServer";
				final public static String LogLevel = "Device.X_00E0FC.LogParaConfiguration.LogLevel";
				final public static String CloseLog = "0";
				final public static String OpenLog = "2";
			}
			
			public static class BandwidthDiagnostics{
				public final static String DiagnosticsState = "Device.X_00E0FC.BandwidthDiagnostics.DiagnosticsState";
				public final static String StateStop = "1";
				public final static String StateStart = "2";
				public final static String StateFinish = "3";
				public final static String StateError = "4";
				
				public final static String DownloadURL = "Device.X_00E0FC.BandwidthDiagnostics.DownloadURL";
				public final static String Username = "Device.X_00E0FC.BandwidthDiagnostics.Username";
				public final static String Password = "Device.X_00E0FC.BandwidthDiagnostics.Password";
				public final static String ErrorCode = "Device.X_00E0FC.BandwidthDiagnostics.ErrorCode";
				public final static String MaxSpeed = "Device.X_00E0FC.BandwidthDiagnostics.MaxSpeed";
				public final static String MinSpeed = "Device.X_00E0FC.BandwidthDiagnostics.MinSpeed";
				public final static String AvgSpeed = "Device.X_00E0FC.BandwidthDiagnostics.AvgSpeed";
			}
			
			public static class PacketCapture{
				final public static String State = "Device.X_00E0FC.PacketCapture.State";
				final public static String StateStop = "0";
				final public static String StateStart = "2";
				final public static String StateDoning = "3";
				final public static String StateDoerror = "4";
				final public static String StateUploading = "5";
				final public static String StateUploadOk = "6";
				final public static String StateUploadError = "7";
				
				final public static String Port = "Device.X_00E0FC.PacketCapture.Port";
				final public static String IP = "Device.X_00E0FC.PacketCapture.IP";
				final public static String Duration = "Device.X_00E0FC.PacketCapture.Duration";
				
				final public static String UploadURL = "Device.X_00E0FC.PacketCapture.UploadURL";
				final public static String Username = "Device.X_00E0FC.PacketCapture.Username";
				final public static String Password = "Device.X_00E0FC.PacketCapture.Password";
			}
			
			public static class DebugInfo{
				final public static String Action = "Device.X_00E0FC.DebugInfo.Action";
				final public static String ActionStop = "0";
				final public static String ActionStart = "1";
				final public static  String UploadURL = "Device.X_00E0FC.DebugInfo.UploadURL";
				final public static  String CapCommand = "Device.X_00E0FC.DebugInfo.CapCommand";
				final public static  String Username = "Device.X_00E0FC.DebugInfo.Username";
				final public static  String Password = "Device.X_00E0FC.DebugInfo.Password";
				final public static  String State = "Device.X_00E0FC.DebugInfo.State";
				final public static  String StateStop = "0";
				final public static  String StateStart = "1";
				final public static  String StateUploading = "2";
				final public static  String StateUploaded = "3";
				final public static  String StateUploadError = "4";
			}
			
			public static class PlayDiagnostics{
				final public static String DiagnosticsState = "Device.X_00E0FC.PlayDiagnostics.DiagnosticsState";
				final public static String StateRequested = "Requested";
				final public static String StateComplete = "Complete";
				final public static String StateStop = "None";
				final public static String PlayURL = "Device.X_00E0FC.PlayDiagnostics.PlayURL";
				final public static String PlayState = "Device.X_00E0FC.PlayDiagnostics.PlayState";
				final public static String Playing = "1";
				final public static String Stoped = "0";
			}
			
			public static class StartupInfo{
				final public static String Action = "Device.X_00E0FC.StartupInfo.Action";
				final public static String ActionOn = "1";
				final public static String ActionOff = "0";
				final public static String UploadURL = "Device.X_00E0FC.StartupInfo.UploadURL";
				final public static String MaxSize = "Device.X_00E0FC.StartupInfo.MaxSize";
				final public static String Username = "Device.X_00E0FC.StartupInfo.Username";
				final public static String Password = "Device.X_00E0FC.StartupInfo.Password";
			}
			
			public static class ServiceInfo{
				final public static String UserID = "Device.X_00E0FC.ServiceInfo.UserID";
				final public static String AuthURL = "Device.X_00E0FC.ServiceInfo.AuthURL";
				final public static String PPPoEID = "Device.X_00E0FC.ServiceInfo.PPPoEID";
			}
		}
		
		public static class STBService{
			final public static String DownloadTransportProtocols = "Device.STBService.DownloadTransportProtocols";
			final public static String StreamingTransportControlProtocols = "Device.STBService.StreamingTransportControlProtocols";
			final public static String VideoStandards = "Device.STBService.VideoStandards";
			final public static String MultiplexTypes = "Device.STBService.MultiplexTypes";
			final public static String AudioStandards = "Device.STBService.AudioStandards";
			final public static String StreamingControlProtocols = "Device.STBService.StreamingControlProtocols";
			final public static String StreamingTransportProtocols = "Device.STBService.StreamingTransportProtocols";
			final public static String MaxDejitteringBufferSize = "Device.STBService.MaxDejitteringBufferSize";
		}
		
		public static class X_CU_STB{
			
			public static class STBInfo{
				final public static String STBID = "Device.X_CU_STB.STBInfo.STBID";
				final public static String PhyMemSize = "Device.X_CU_STB.STBInfo.PhyMemSize";
				final public static String StorageSize = "Device.X_CU_STB.STBInfo.StorageSize";
				final public static String BrowserURL1 = "Device.X_CU_STB.STBInfo.BrowserURL1";
				
			}
			
			public static class AuthServiceInfo{
				final public static String UserID = "Device.X_CU_STB.AuthServiceInfo.UserID";
				final public static String UserIDPassword = "Device.X_CU_STB.AuthServiceInfo.UserIDPassword";
				final public static String PPPOEID = "Device.X_CU_STB.AuthServiceInfo.PPPOEID";
				final public static String PPPOEPassword = "Device.X_CU_STB.AuthServiceInfo.PPPOEPassword";
				final public static String BrowserURL1 = "Device.X_CU_STB.AuthServiceInfo.BrowserURL1";
			}
		}
	}
	
	private static ArrayList<IWork> sWorkTasks;
	
	public static void init(Context context){
		sWorkTasks = new ArrayList<IWork>();
	
		BandwidthWork.init();
		LogCaptureWork.init();
		NetworkCaptureWork.init();
		OneKeyWork.init();
		PingWork.init();
		TraceRouteWork.init();
		PlaydiagnoseWork.init();
		RebootWork.init();
		FactoryResetWork.init();
		ErrorTaskWork.init();
		HeartTaskWork.init();
		NatTaskWork.init();
		StartupInfoWork.init();
		
//		try {
//			Class.forName(BandwidthWork.class.getName());
//			Class.forName(LogCaptureWork.class.getName());
//			Class.forName(NetworkCaptureWork.class.getName());
//			Class.forName(OneKeyWork.class.getName());
//			Class.forName(PingWork.class.getName());
//			Class.forName(TraceRouteWork.class.getName());
//			Class.forName(PlaydiagnoseWork.class.getName());
//			Class.forName(RebootWork.class.getName());
//			Class.forName(FactoryResetWork.class.getName());
//			Class.forName(ErrorTaskWork.class.getName());
//			Class.forName(HeartTaskWork.class.getName());
//			Class.forName(NatTaskWork.class.getName());
//			Class.forName(StartupInfoWork.class.getName());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void registerWork(IWork work)
	{
		if(!sWorkTasks.contains(work))
			sWorkTasks.add(work);
	}
	
	public static void doWorkTask()
	{
		TR069Log.say("work num=" + sWorkTasks.size());
		for(IWork work : sWorkTasks)
		{
			work.doWork();
		}
	}
	
	public static String getManagementServer()
	{
		return InspurData.getInspurValue("Device.ManagementServer.URL");
	}
	
	public static String getCommandKey()
	{
		String key = InspurData.getInspurValue("CommandKey");
		if(key == null || key.equals(""))
		{
			TR069Log.sayError(" error get commandkey,use random key.");
			key = String.valueOf(new Random().nextInt());
		}
		
		return key;
	}
	
	public static void firstOrTMUnull() {
        TR069Log.say("TMU: first exe");
        TR069Log.say("TMU: ManagementServerURL = " + HuaWeiData.ManagementServerURL);
        
        // 和TMC的交互非常重要，要一直重试到成功。
        while (HuaWeiRMS.ACSDoRegisterOnStartup(HuaWeiData.ManagementServerURL) != 0 
        		|| InspurData.getInspurValue("Device.ManagementServer.URL") == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	
	 public static void sendStartupToTmu() {
	        TR069Log.say("TMU : try tmu");
	        String tmuUrl = getManagementServer();
	        TR069Log.say("TMU : !!!!!!!!! tmu url = " + tmuUrl);
	        if (tmuUrl != null && !tmuUrl.trim().equals("")) {
	            while (HuaWeiRMS.ACSDoRegisterOnStartup(tmuUrl) != 0) {
	                try {
	                    
	                    Thread.sleep(1000);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        } else {
	            TR069Log.sayError("TMU : fatal,TMU url get error!");
	            return;
	        }
	        HuaWeiRMS.ACSInformValueChange(getManagementServer(), TR069Utils.Device.ManagementServer.URL);
	    }
	 
	    public static void fileChannelCopy(File s, File t) {
	        FileInputStream fi = null;
	        FileOutputStream fo = null;
	        FileChannel in = null;
	        FileChannel out = null;
	        try {
	            fi = new FileInputStream(s);
	            fo = new FileOutputStream(t);
	            in = fi.getChannel();// 得到对应的文件通道
	            out = fo.getChannel();// 得到对应的文件通道
	            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                fi.close();
	                in.close();
	                fo.close();
	                out.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    public static void doUpgrade(String url) {
	        Intent intent = new Intent();
	        intent.setAction("android.intent.action.SYSTEM_UPGRADE");
	        TR069Log.say(" downloadUrl url:" + url);
	        if (url.contains(" ")) {
	            url = url.replace(" ", "%20");
	            TR069Log.say("dddddddddddddd downloadUrl urlencode to:" + url);
	        }
	        intent.putExtra("downloadUrl", url);
	        if (GlobalContext.getGlobalContext() != null) {
	            TR069Log.say("dddddddddddddd download apk start!");
	            InspurData.setInspurValue("needupgrade", "true");
	            GlobalContext.getGlobalContext().sendBroadcast(intent);
	        }
	        
	        if(HttpDowmload.startDownload(url, "/cache/updata.zip"))
	        {
	        	try {
//					RecoverySystem.installPackage(GlobalContext.getGlobalContext(),new File("/cache/updata.zip"), false);
					RecoverySystem.installPackage(GlobalContext.getGlobalContext(),new File("/cache/updata.zip"));
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
}
