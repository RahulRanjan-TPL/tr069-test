package com.inspur.work.ping;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.tr069.ShellUtils.CommandResult;
import com.inspur.work.BaseWork;
import com.inspur.work.IShellCommondStateListen;
import com.inspur.work.TR069Utils;
import static com.inspur.work.TR069Utils.Device.LAN.IPPingDiagnostics.*;

public class PingWork extends BaseWork implements IShellCommondStateListen{

//	 final public static String COUNT_KEY = "Device.LAN.IPPingDiagnostics.NumberOfRepetitions";
//	 final public static String SIZE_KEY = "Device.LAN.IPPingDiagnostics.DataBlockSize";
//	 final public static String TIMEOUT_KEY = "Device.LAN.IPPingDiagnostics.Timeout";
//	 final public static String IP_KEY = "Device.LAN.IPPingDiagnostics.Host";
	 
//	 final public static String STATE_KEY = "Device.LAN.IPPingDiagnostics.DiagnosticsState";
//	 final public static String STATE_STOP = "STOP";
//	 final public static String STATE_REQUEST = "Requested";
//	 final public static String STATE_COMPLETE = "Complete";
//	 final public static String SUCCESSCOUNT_KEY = "Device.LAN.IPPingDiagnostics.SuccessCount";
//	 final public static String FAILURECOUNT_KEY = "Device.LAN.IPPingDiagnostics.FailureCount";
     // ec:I will get these value later
//	 final public static String AVERAGE_RESPONSE_TIME_KEY = "Device.LAN.IPPingDiagnostics.AverageResponseTime";
//	 final public static String MAX_RESPONSE_TIME_KEY = "Device.LAN.IPPingDiagnostics.MaximumResponseTime";
//	 final public static String MIN_RESPONSE_TIME_KEY = "Device.LAN.IPPingDiagnostics.MinimumResponseTime";
	
	 public static void init(){
			TR069Log.sayInfo("PingWork register!");
			Tr069DataFile.setNotWrite(DiagnosticsState, StateStop);
			TR069Utils.registerWork(new PingWork());
		}
	 
	@Override
	public boolean startWork() {
		
		if (!StateRequested.equals(InspurData.getInspurValue(DiagnosticsState))) {
			return false;
		}
		
//		TR069Log.say(InspurData.getInspurValue(DiagnosticsState));
		TR069Log.sayInfo(TAG + "ping work lail!!!!!!!!!!");
		
		 String count = Tr069DataFile.get(NumberOfRepetitions);
         String size = Tr069DataFile.get(DataBlockSize);
         String timeout = Tr069DataFile.get(Timeout);
         String ip = Tr069DataFile.get(Host);
		
         Ping ping = new Ping(this);
         ping.setCount(count);
         ping.setSize(size);
         ping.setTimeout(timeout);
         ping.setIp(ip);
         ping.doCommondTask();
         
		return true;
	}

	public void onFinishWork(CommandResult commandResult) {
		
		String result = commandResult.successMsg;
        TR069Log.sayInfo(TAG+ " ping output:" + result);
        
		String successCount = PingAnalyse.getSuccessCount(result);
        String failureCount = PingAnalyse.getFailureCount(result);
        String averageResponseTime = PingAnalyse.getAverageResponseTime(result);
        String maximumResponseTime = PingAnalyse.getMaximumResponseTime(result);
        String minimumResponseTime = PingAnalyse.getMinimumResponseTime(result);

        Tr069DataFile.set(DiagnosticsState, StateComplete);
        Tr069DataFile.set(SuccessCount, successCount);
        Tr069DataFile.set(FailureCount, failureCount);
        // ec:I will get these value later
        Tr069DataFile.set(AverageResponseTime, averageResponseTime);
        Tr069DataFile.set(MaximumResponseTime, maximumResponseTime);
        Tr069DataFile.set(MinimumResponseTime, minimumResponseTime);
       
        HuaWeiRMS.ACSDoPingInform(TR069Utils.getManagementServer(), TR069Utils.getCommandKey(), "0");
		
		super.onFinishWork();
	}

	@Override
	public void onErrorWork() {
		Tr069DataFile.set(DiagnosticsState, StateStop);
		super.onErrorWork();
	}

	@Override
	public void onStartState() {
		startWorkBase();
	}

	@Override
	public void onFinishState(CommandResult result) {
		onFinishWork(result);
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
	public boolean onCheckStop(boolean bIsApend) {
		return bIsApend;
	}

}
