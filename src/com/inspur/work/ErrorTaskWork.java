package com.inspur.work;

import com.inspur.config.InspurData;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.ErrorCodeSwitch;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.ErrorCodeInterval;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.ErrorSwitchOn;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.ErrorSwitchOff;

public class ErrorTaskWork extends BaseTaskWork{

//	final public static String SWITCH_KEY = "Device.X_00E0FC.ErrorCodeSwitch";
//	final public static String SWITCH_ON = "1";
//	final public static String SWITCH_OFF = "0";
//	final public static String INTERVAL_KEY = "Device.X_00E0FC.ErrorCodeInterval";
	
	 private String mInterval = "";
	 
	 public static void init(){
	 	TR069Log.sayInfo("ErrorTaskWork register!");
		TR069Utils.registerWork(new ErrorTaskWork());
	 }
	 
	@Override
	public boolean startWork() {

		if (!ErrorSwitchOn.equals(InspurData.getInspurValue(ErrorCodeSwitch))) {
			return false;
		}

		mInterval = InspurData.getInspurValue(ErrorCodeInterval);
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
		TR069Log.sayInfo(TAG + " errortask thread...");
		
		String speriodic = InspurData.getInspurValue(ErrorCodeInterval);
	
		if (speriodic == null || speriodic.trim().equals("")) {
			Tr069DataFile.set(ErrorCodeInterval, mInterval);
		}else if(!speriodic.equals(mInterval)){
			setTaskSleepTime(Integer.parseInt(speriodic));
			mInterval = speriodic;
		}

		
        if (GlobalContext.allErrors.size() > 0) {
            TR069Log.sayInfo(TAG + " run...send some error!");
            HuaWeiRMS.ACSDoErrorCodeInform(TR069Utils.getManagementServer());
        }
	}
	
	@Override
	public void onFinishWork() {
		// TODO Auto-generated method stub
		super.onFinishWork();
	}

	@Override
	public void onErrorWork() {
		Tr069DataFile.set(ErrorCodeSwitch, ErrorSwitchOff);
		super.onErrorWork();
	}

	@Override
	protected boolean checkStopTask() {
		if(!ErrorSwitchOn.equals(InspurData.getInspurValue(ErrorCodeSwitch)))
			return false;
		
		return true;
	}
}
