package com.inspur.work.networkcapture;

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
import static com.inspur.work.TR069Utils.Device.X_00E0FC.PacketCapture.*;

public class NetworkCaptureWork extends BaseWork{

//	final public static String STATE_KEY = "Device.X_00E0FC.PacketCapture.State";
//	final public static String STATE_STOP = "0";
//	final public static String STATE_START = "2";
//	final public static String STATE_DONING = "3";
//	final public static String STATE_DOERROR = "4";
//	final public static String STATE_UPLOADING = "5";
//	final public static String STATE_UPLOAD_OK = "6";
//	final public static String STATE_UPLOAD_ERROR = "7";
	
//	final public static String PORT_KEY = "Device.X_00E0FC.PacketCapture.Port";
//	final public static String IP_KEY = "Device.X_00E0FC.PacketCapture.IP";
//	final public static String TIME_KEY = "Device.X_00E0FC.PacketCapture.Duration";
	
//	final public static String UPLOADURL_KEY = "Device.X_00E0FC.PacketCapture.UploadURL";
//	final public static String USERNAME_KEY = "Device.X_00E0FC.PacketCapture.Username";
//	final public static String PASSWORD_KEY = "Device.X_00E0FC.PacketCapture.Password";
	
	public static void init(){
		TR069Log.sayInfo("NetworkCaptureWork register!");
		Tr069DataFile.setNotWrite(State, StateStop);
		TR069Utils.registerWork(new NetworkCaptureWork());
	}

	final private String SAVELOCAL_PATH = "/tmp/capture/";
	private String mstrFile;
	
	public NetworkCaptureWork(){
		String[] cmdStart = new String[]{
			"rm -r " + SAVELOCAL_PATH,
			"mkdir -p " + SAVELOCAL_PATH,
			"chmod -R 777 " + SAVELOCAL_PATH,
		};
		ShellUtils.execCommand(cmdStart, true);
	}
	
	@Override
	public boolean startWork() {
		
		if(!StateStart.equals(InspurData.getInspurValue(State))){
			return false;
		}
		
//		PacketCapture.stopCommond();
		
		 String fileName = TR069Tools.getUploadName("_");
         if(fileName == null)
         	return false;
		
		mstrFile = SAVELOCAL_PATH + fileName + ".pcap";
		
		String ip = InspurData.getInspurValue(IP);
		String port = InspurData.getInspurValue(Port);
		String time = InspurData.getInspurValue(Duration);
		
		PacketCapture packetCapture = new PacketCapture(new NetworkCaptureListen());
		packetCapture.setFilePath(mstrFile);
		packetCapture.setIp(ip);
		packetCapture.setPort(port);
		
//		if(mbIsTimeToStop)
//		{
			packetCapture.setCommondTime(Integer.parseInt(time));
//		}
		
		packetCapture.doCommondTask();
		
		return true;
	}
 
	@Override
	public void onFinishWork() {
		InspurData.setInspurValue(State, StateUploading);
		TR069Log.sayInfo(TAG + " State = 5");
		
		String[] cmdFinish = new String[]{
				"chmod 777 " + mstrFile,
		};
		
		ShellUtils.execCommand(cmdFinish, true);
		
		String url = Tr069DataFile.get(UploadURL);
        String name = Tr069DataFile.get(Username);
        String password = Tr069DataFile.get(Password);
        
        if(Upload.doUploadFile(Upload.FTP, url, mstrFile, name, password)){
        	InspurData.setInspurValue(State, StateUploadOk);
        	TR069Log.sayInfo(TAG + " State = 6");
        }else{
        	InspurData.setInspurValue(State, StateUploadError);
        	TR069Log.sayInfo(TAG + " State = 7");
//        	addError("9011");
        }
        
//        File file = new File(saveCaptrueFile);
//        file.delete();
		
		super.onFinishWork();
	}
	
	@Override
	public void onErrorWork() {
		InspurData.setInspurValue(State, StateDoerror);
		
		super.onErrorWork();
	}
	
	class NetworkCaptureListen implements IShellCommondStateListen
	{

		@Override
		public void onStartState() {
			startWorkBase();
			TR069Log.sayInfo(TAG + " State = 3");
			InspurData.setInspurValue(State, StateDoning);
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
		}

		@Override
		public void onErrorState(int errorCode) {
			onErrorWork();
		}

		@Override
		public boolean onCheckStop(boolean isApend) {
			return isApend;
		}
		
	}
}
