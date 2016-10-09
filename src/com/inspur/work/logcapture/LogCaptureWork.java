package com.inspur.work.logcapture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tools.upload.Upload;
import com.inspur.tr069.ShellUtils;
import com.inspur.tr069.ShellUtils.CommandResult;
import com.inspur.work.BaseWork;
import com.inspur.work.IShellCommondStateListen;
import com.inspur.work.TR069Tools;
import com.inspur.work.TR069Utils;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.LogParaConfiguration.*;
 
public class LogCaptureWork extends BaseWork{

//	final public static String LOG_TYPE_KEY = "Device.X_00E0FC.LogParaConfiguration.LogOutPutType";
//	final public static String LOG_CLOSE = "0";
//	final public static String LOG_OPEN = "2";
//	
//	
//	final public static String LOG_SERVER_KEY = "Device.X_00E0FC.LogParaConfiguration.SyslogServer";
//	final public static String LOG_LEVEL_KEY = "Device.X_00E0FC.LogParaConfiguration.LogLevel";
	
	
	public static void init(){
		TR069Log.sayInfo("LogCaptureWork register!");
		TR069Utils.registerWork(new LogCaptureWork());
	}
	
	final private String SAVELOCAL_PATH = "/tmp/logcapture/";
	final private String SAVEFILE_PATH = "/tmp/logcapture/log_tmp.txt";
	
	private String mServerURL;
	private String mContent;
	
	{
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = formatter.format(new Date());
        
        String[] date = time.split("T");
        String[] ymd = date[0].split("-");
        String mon = ymd[1];
        String day = ymd[2];
        String times = date[1];
        String mac = InspurData.getInspurValue(TR069Utils.Device.LAN.MACAddress);
        String Type = InspurData.getInspurValue(TR069Utils.Device.DeviceInfo.ProductClass);
        String version = InspurData.getInspurValue(TR069Utils.Device.DeviceInfo.SoftwareVersion);
		
		String PRI = "<143>";
        String TIMESTAMP = TR069Tools.getMonStr(mon) + " " + day + " " + times;
        String HOSTNAME = mac;
        String HEADER = TIMESTAMP + " " + HOSTNAME + " ";
        String TAG = Type + version + "  \n";
        mContent = PRI + HEADER + TAG;
        
        String[] cmdStart = new String[]{
			"rm -r " + SAVELOCAL_PATH,
			"mkdir -p " + SAVELOCAL_PATH,
			"chmod -R 777 " + SAVELOCAL_PATH,
		};
		ShellUtils.execCommand(cmdStart, true);
	}
	
	@Override
	public void onFinishWork() {
		Tr069DataFile.set(LogOutPutType, CloseLog);
		super.onFinishWork();
	}
	
	@Override
	public void onErrorWork() {
		Tr069DataFile.set(LogOutPutType, CloseLog);
		super.onErrorWork();
	}
	
	public void onErrorWork(int errorcode)
	{
		
		onErrorWork();
	}
	
	@Override
	public boolean startWork() {
		
		if(CloseLog.equals(InspurData.getInspurValue(LogOutPutType))){
//			TR069Log.sayInfo(TAG + ":" + LOG_TYPE_KEY + " is null");
			return false;
		}
		
		mServerURL = Tr069DataFile.get(SyslogServer);
		if(mServerURL == null || mServerURL.isEmpty())
		{
			TR069Log.sayError(TAG + ":" + SyslogServer + " is null");
			Tr069DataFile.set(LogOutPutType, CloseLog);
			return false;
		}

//		String logLevel = InspurData.getInspurValue("Device.X_00E0FC.LogParaConfiguration.LogLevel");
//        if (mac == null || Type == null || version == null || url == null || logLevel == null) {
//            TR069Log.sayInfo("error get param in sendLog,return");
//            return;
//        }
		
        String logLevel = Tr069DataFile.get(LogLevel);
		if(logLevel == null || logLevel.isEmpty())
		{
			TR069Log.sayError(TAG + ":" + LogLevel + " is null");
			Tr069DataFile.set(LogOutPutType, CloseLog);
			return false;
		}
        
		int iLogType =  logLevel.equals("6") ? LogCaptureStatic.LOG_INFO  : 
 			logLevel.equals("7") ? LogCaptureStatic.LOG_DEBUG :
 			logLevel.equals("3") ? LogCaptureStatic.LOG_ERROR :
						LogCaptureStatic.LOG_ALL;
		
		LogCapture logCapture = new LogCapture(new ShellListen());
		logCapture.setApend(false);
	    logCapture.setFilePath(SAVEFILE_PATH);
	    logCapture.setLogType(iLogType);
	    logCapture.doCommondTask();
		return true;
	}
	
	class ShellListen implements IShellCommondStateListen
	{

		@Override
		public void onStartState() {
			startWorkBase();
		}

		@Override
		public void onFinishState(CommandResult result) {
			onFinishWork();
		}
 
		@Override
		public void onStartOneTime() {
			
		}

		@Override
		public void onFinishOneTime() {
			String[] cmdFinish = new String[]{
					"chmod 777 " + SAVEFILE_PATH,
			};
			
			ShellUtils.execCommand(cmdFinish, true);
			
			Upload.doUploadFile(Upload.UDP, mServerURL, SAVEFILE_PATH, null, null, mContent);
		}

		@Override
		public void onErrorState(int errorCode) {
			onErrorWork(errorCode);
		}

		@Override
		public boolean onCheckStop(boolean isApend) {
			
			String logOutPutType = InspurData.getInspurValue(LogOutPutType);
			if(logOutPutType == null|| logOutPutType.isEmpty() || "0".equals(logOutPutType))
			{
				Tr069DataFile.set(LogOutPutType, CloseLog);
				return false;
			}
			return true;
		}
		
	}

}
