package com.inspur.work.startupinfo;

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
import static com.inspur.work.TR069Utils.Device.X_00E0FC.StartupInfo.*;

public class StartupInfoWork extends BaseWork implements IShellCommondStateListen{

//	final public static String ACTION_KEY = "Device.X_00E0FC.StartupInfo.Action";
//	final public static String ACTION_ON = "1";
//	final public static String ACTION_OFF = "0";
//	final public static String UPLOADURL_KEY = "Device.X_00E0FC.StartupInfo.UploadURL";
//	final public static String MAXSIZE_KEY = "Device.X_00E0FC.StartupInfo.MaxSize";
//	final public static String USERNAME_KEY = "Device.X_00E0FC.StartupInfo.Username";
//	final public static String PASSWORD_KEY = "Device.X_00E0FC.StartupInfo.Password";
	
	final private String TAR_LOACL_PATH = "/tmp/";
	final private String LOACL_FILE_PATH = "/tmp/startup/";
	private String LOACL_LOG_FILE_PATH = LOACL_FILE_PATH + "startup.log";
	private String mstrFilePath;
	private int miTaskTuner = 0;
	
	public static void init(){
		TR069Log.sayInfo("StartupInfoWork register!");
		TR069Utils.registerWork(new StartupInfoWork());
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
		if(!ActionOn.equals(Tr069DataFile.get(Action)))
		{
			return false;
		}
		
		String fileName = TR069Tools.getUploadName("");
        if(fileName == null || fileName.trim().equals(""))
        {
        	return false;
        }
		
        mstrFilePath = TAR_LOACL_PATH + "STBStartupInfo" + fileName + ".tar.gz";
        startWorkBase();
        
        LogCapture logCapture = new LogCapture(this);
 		logCapture.setApend(true);
 	    logCapture.setFilePath(LOACL_LOG_FILE_PATH);
 	    logCapture.setLogType(LogCapture.LOG_ALL);
 	    logCapture.doCommondTask();
        
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
			
//			TR069Log.sayInfo(TAG + " state = 2");
//			Tr069DataFile.set(STATE_KEY, STATE_UPLOADING);
			
			String uploadURL = Tr069DataFile.get(UploadURL);
	        String username = Tr069DataFile.get(Username);
	        String password = Tr069DataFile.get(Password);
			
			if(Upload.doUploadFile(Upload.FTP, uploadURL, mstrFilePath, username, password))
			{
//				TR069Log.sayInfo(TAG + " state = 3");
//				Tr069DataFile.set(STATE_KEY, STATE_UPLOADED);
			}else
			{
//				TR069Log.sayInfo(TAG + " state = 4");
//				Tr069DataFile.set(STATE_KEY, STATE_UPLOADED_ERROR);
			}
			TR069Log.sayInfo(TAG + " finish work");
		}
	}

	@Override
	public void onErrorWork() {
		TR069Log.sayInfo(TAG + " error work");
		InspurData.setInspurValue(Action, ActionOff);
	}

	@Override
	public void onStartState() {
		miTaskTuner ++;
		
	}

	@Override
	public void onFinishState(CommandResult result) {
		miTaskTuner --;
		onFinishWork();
	}

	@Override
	public void onStartOneTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishOneTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onErrorState(int errorCode) {
		onErrorWork();
	}

	@Override
	public boolean onCheckStop(boolean bIsApend) {
//		if(InspurData.getInspurValue(ACTION_KEY) == null || InspurData.getInspurValue(ACTION_KEY).equals(ACTION_OFF))
//			return false;
		
		return false;
	}

}
