package com.inspur.work.bandwidth;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.work.BaseWork;
import com.inspur.work.TR069Utils;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.BandwidthDiagnostics.*;

public class BandwidthWork extends BaseWork {

//	public final static String STATE_KEY = "Device.X_00E0FC.BandwidthDiagnostics.DiagnosticsState";
//	public final static String STATE_STOP = "1";
//	public final static String STATE_START = "2";
//	public final static String STATE_FINISH = "3";
//	public final static String STATE_ERROR = "4";
	
//	public final static String DOWNLOADURL_KEY = "Device.X_00E0FC.BandwidthDiagnostics.DownloadURL";
//	public final static String USERNAME_KEY = "Device.X_00E0FC.BandwidthDiagnostics.Username";
//	public final static String PASSWORD_KEY = "Device.X_00E0FC.BandwidthDiagnostics.Password";
//	public final static String ERROR_CODE_KEY = "Device.X_00E0FC.BandwidthDiagnostics.ErrorCode";
//	public final static String MAXSPEED_KEY = "Device.X_00E0FC.BandwidthDiagnostics.MaxSpeed";
//	public final static String MINSPEED_KEY = "Device.X_00E0FC.BandwidthDiagnostics.MinSpeed";
//	public final static String AVGSPEED_KEY = "Device.X_00E0FC.BandwidthDiagnostics.AvgSpeed";
	
	public static void init(){
		TR069Log.sayInfo("BandwidthWork register!");
		Tr069DataFile.setNotWrite(DiagnosticsState, StateStop);
		TR069Utils.registerWork(new BandwidthWork());
	}
	
	@Override
	public boolean startWork() {
		
		if(!StateStart.equals(InspurData.getInspurValue(DiagnosticsState))){
			return false;
		}
		
		
		String downloadURL = Tr069DataFile.get(DownloadURL);
		if(downloadURL == null || downloadURL.isEmpty())
		{
			Tr069DataFile.set(DiagnosticsState, StateStop);
			return false;
		}
		
    	String userName = Tr069DataFile.get(Username);
    	String passWord = Tr069DataFile.get(Password);
    	
		startWorkBase();
    	
    	DownloadThread thread = new DownloadThread(downloadURL, new BandwidthListener());
    	thread.setUserAndPassword(userName, passWord);
    	thread.start();
		return true;
	}

	@Override
	public void onFinishWork() {
		HuaWeiRMS.ACSDoPingInform(TR069Utils.getManagementServer(), TR069Utils.getCommandKey(), "0");
		super.onFinishWork();
	}
	
	@Override
	public void onErrorWork() {
		HuaWeiRMS.ACSDoPingInform(TR069Utils.getManagementServer(), TR069Utils.getCommandKey(), "0");
		super.onErrorWork();
	}
	
	class BandwidthListener implements CheckSpeedListener{

		@Override
		public void downloadFinish(NetWorkSpeedInfo netWorkSpeedInfo) {
			Tr069DataFile.set(DiagnosticsState, StateFinish);
			Tr069DataFile.set(ErrorCode, "");
			Tr069DataFile.set(MaxSpeed, "" + (netWorkSpeedInfo.maxSpeed * 8));
			Tr069DataFile.set(MinSpeed, "" + netWorkSpeedInfo.minSpeed * 8);
			Tr069DataFile.set(AvgSpeed, "" + netWorkSpeedInfo.speed * 8);
			
			onFinishWork();
		}

		@Override
		public void downloadError(String errorcode) {
			Tr069DataFile.set(DiagnosticsState, StateError);
			Tr069DataFile.set(ErrorCode, errorcode);
			onErrorWork();
		}
		
	}


}
