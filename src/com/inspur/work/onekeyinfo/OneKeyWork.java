package com.inspur.work.onekeyinfo;

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
import com.inspur.work.logcapture.LogCapture;
import com.inspur.work.networkcapture.PacketCapture;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.DebugInfo.*;

public class OneKeyWork extends BaseWork implements IShellCommondStateListen{

//	final public static String ACTION_KEY = "Device.X_00E0FC.DebugInfo.Action";
//	final public static String ACTION_STOP = "0";
//	final public static String ACTION_START = "1";
//	final public static  String UPLOADURL_KEY = "Device.X_00E0FC.DebugInfo.UploadURL";
//	final public static  String CAPCOMMAND_KEY = "Device.X_00E0FC.DebugInfo.CapCommand";
//	final public static  String USERNAME_KEY = "Device.X_00E0FC.DebugInfo.Username";
//	final public static  String PASSWORD_KEY = "Device.X_00E0FC.DebugInfo.Password";
//	final public static  String STATE_KEY = "Device.X_00E0FC.DebugInfo.State";
//	final public static  String STATE_STOP = "0";
//	final public static  String STATE_START = "1";
//	final public static  String STATE_UPLOADING = "2";
//	final public static  String STATE_UPLOADED = "3";
//	final public static  String STATE_UPLOADED_ERROR = "4";
	
	final private String TAR_LOACL_PATH = "/tmp/";
	final private String LOACL_FILE_PATH = "/tmp/onekey/";
	private String LOACL_PACP_FILE_PATH = LOACL_FILE_PATH + "onekey.pacp";
	private String LOACL_LOG_FILE_PATH = LOACL_FILE_PATH + "onekey.log";
	private String LOACL_NETCFG_FILE_PATH = LOACL_FILE_PATH + "onekey.netcfg";
	
	private String mstrFilePath;
	private int miTaskTuner = 0;
	
	public static void init(){
		TR069Log.sayInfo("OneKeyWork register!");
		Tr069DataFile.setNotWrite(Action, ActionStop);
		Tr069DataFile.setNotWrite(State, StateStop);
		TR069Utils.registerWork(new OneKeyWork());
	}
	
	{
		String[] cmdStart = new String[]{
			"rm -r " + LOACL_FILE_PATH,
			"mkdir -p " + LOACL_FILE_PATH,
			"chmod -R 777 " + LOACL_FILE_PATH,
		};
		ShellUtils.execCommand(cmdStart, true);
	}
	
	@Override
	public boolean startWork() {
		
		if(!ActionStart.equals(InspurData.getInspurValue(Action)))
		{
			return false;
		}
		
		
		 String fileName = TR069Tools.getUploadName("");
         if(fileName == null)
         	return false;

         mstrFilePath = TAR_LOACL_PATH + "STBDebugInfo" + fileName + ".tar.gz";
		
         InspurData.setInspurValue(State, StateStart);
         TR069Log.sayInfo(TAG + " state = 1");
         startWorkBase();
         
         String capCommand = Tr069DataFile.get(CapCommand);
         if(capCommand != null && !"".equals(capCommand))
         { 
        	 PacketCapture packetCapture = new PacketCapture(this);
        	 packetCapture.setFilePath(LOACL_PACP_FILE_PATH);
        	 packetCapture.setCommond(capCommand);
        	 packetCapture.setNeedStopThread();
        	 packetCapture.doCommondTask();
         }
         
        LogCapture logCapture = new LogCapture(this);
 		logCapture.setApend(true);
 	    logCapture.setFilePath(LOACL_LOG_FILE_PATH);
 	    logCapture.setLogType(LogCapture.LOG_ALL);
 	    logCapture.doCommondTask();
        
 	    Netcfg netcfg = new Netcfg(this);
 	    netcfg.setApend(true);
 	    netcfg.setFilePath(LOACL_NETCFG_FILE_PATH);
 	    netcfg.doCommondTask();
 	    
		return true;
	}

	@Override
	public void onFinishWork() {
		if(miTaskTuner == 0)
		{
			TR069Log.sayInfo(TAG + " all task is finish!");
			String[] cmds = {
					"chmod -R 777 " + LOACL_FILE_PATH,
					"busybox tar zcf " + mstrFilePath + " " + LOACL_FILE_PATH,
					"chmod 777 " + mstrFilePath};
			ShellUtils.execCommand(cmds, true);
			
			TR069Log.sayInfo(TAG + " state = 2");
			InspurData.setInspurValue(State, StateUploading);
			
			String uploadURL = Tr069DataFile.get(UploadURL);
	        String username = Tr069DataFile.get(Username);
	        String password = Tr069DataFile.get(Password);
			
			if(Upload.doUploadFile(Upload.FTP, uploadURL, mstrFilePath, username, password))
			{
				TR069Log.sayInfo(TAG + " state = 3");
				InspurData.setInspurValue(State, StateUploaded);
			}else
			{
				TR069Log.sayInfo(TAG + " state = 4");
				InspurData.setInspurValue(State, StateUploadError);
			}
			
			super.onFinishWork();
		}
	}



	@Override
	public void onErrorWork() {
		TR069Log.sayInfo(TAG + " state = 0");
		InspurData.setInspurValue(State, StateStop);
		super.onErrorWork();
	}
	
	@Override
	public void onStartState() {
		miTaskTuner ++;
		
	}

	@Override
	public void onFinishState(CommandResult result) {
		miTaskTuner--;
		onFinishWork();
	}

	@Override
	public void onStartOneTime() {
	}

	@Override
	public void onFinishOneTime() {
	}

	@Override
	public void onErrorState(int errorCode) {
		 onErrorWork();
	}

	@Override
	public boolean onCheckStop(boolean isApend) {
		if(InspurData.getInspurValue(Action) == null || InspurData.getInspurValue(Action).equals(StateStop))
			return false;
		
		return true;
	}

	
}
