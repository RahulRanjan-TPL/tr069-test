package com.inspur.work;

import com.inspur.config.InspurData;
import com.inspur.tools.EventStructCode;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import static com.inspur.work.TR069Utils.Device.ManagementServer.*;

public class HeartTaskWork extends BaseTaskWork{

//	final public static String PERIODICINFORMENABLE_KEY = "Device.ManagementServer.PeriodicInformEnable";
//	final public static String SWITCH_ON = "1";
//	final public static String SWITCH_OFF = "0";
//	final public static String PERIODICINFORMINTERVAL_KEY = "Device.ManagementServer.PeriodicInformInterval";
	
	 private String mInterval = "";
	 
	 public static void init(){
		TR069Log.sayInfo("HeartTaskWork register!");
		TR069Utils.registerWork(new HeartTaskWork());
	 }

	@Override
	public boolean startWork() {
		
		if (!PeriodicEnable.equals(InspurData.getInspurValue(PeriodicInformEnable))) {
			return false;
		}

		mInterval = InspurData.getInspurValue(PeriodicInformInterval);
		int iPeriodic = 10 * 60;
		if (mInterval != null && !mInterval.trim().equals("")) {
			try {
				iPeriodic = Integer.parseInt(mInterval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		setTaskSleepTime(iPeriodic);
		startTask();
		
		return true;
	}
	 
	@Override
	protected void doTaskWork() {
		
		String speriodic = InspurData.getInspurValue(PeriodicInformInterval);
		
		if (speriodic == null || speriodic.trim().equals("")) {
			Tr069DataFile.set(PeriodicInformInterval, mInterval);
		}else if(!speriodic.equals(mInterval)){
			setTaskSleepTime(Integer.parseInt(speriodic));
			mInterval = speriodic;
		}
		
		TR069Log.sayInfo(TAG + " theard run...");
		HuaWeiRMS.CPEInformACSToTMU(EventStructCode.PERIODIC);
	}
	
	@Override
	public void onFinishWork() {
		// TODO Auto-generated method stub
		super.onFinishWork();
	}

	@Override
	public void onErrorWork() {
		Tr069DataFile.set(PeriodicInformEnable, PeriodicDisenable);
		super.onErrorWork();
	}

	@Override
	protected boolean checkStopTask() {
		if(!PeriodicEnable.equals(InspurData.getInspurValue(PeriodicInformEnable)))
			return false;
		
		return true;

	}
}
