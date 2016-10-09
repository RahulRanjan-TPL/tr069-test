package com.inspur.work.traceRoute;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.tr069.ShellUtils.CommandResult;
import com.inspur.work.BaseWork;
import com.inspur.work.IShellCommondStateListen;
import com.inspur.work.TR069Utils;
import static com.inspur.work.TR069Utils.Device.LAN.TraceRouteDiagnostics.*;

public class TraceRouteWork extends BaseWork implements IShellCommondStateListen{

//	final public static String STATE_KEY = "Device.LAN.TraceRouteDiagnostics.DiagnosticsState";
//	final public static String STATE_STOP = "stop";
//	final public static String STATE_START = "Requested";
//	final public static String HOST_KEY = "Device.LAN.TraceRouteDiagnostics.Host";
//	final public static String MAXHOPCOUNT_KEY = "Device.LAN.TraceRouteDiagnostics.MaxHopCount";
//	final public static String TIMEOUT_KEY = "Device.LAN.TraceRouteDiagnostics.Timeout";
	
//	final public static String RESPONSETIME_KEY = "Device.LAN.TraceRouteDiagnostics.ResponseTime";
//	final public static String NUMBEROFROUTEHOPS_KEY =  "Device.LAN.TraceRouteDiagnostics.NumberOfRouteHops";
	
	public static void init(){
		TR069Log.sayInfo("TraceRouteWork register!");
		Tr069DataFile.setNotWrite(DiagnosticsState, StateStop);
		TR069Utils.registerWork(new TraceRouteWork());
	}
	
	@Override
	public boolean startWork() {
		
		if(!StateRequested.equals(InspurData.getInspurValue(DiagnosticsState)))
		{
			return false;
		}
		
		 String host = Tr069DataFile.get(Host);
		 String maxHopCount = Tr069DataFile.get(MaxHopCount);
//		 String Timeout = Tr069DataFile.get(TIMEOUT_KEY);
		
	     TraceRoute traceRoute = new TraceRoute(this);
	     traceRoute.setHost(host);
	     traceRoute.setMaxHopCount(maxHopCount);
	     
	     traceRoute.doCommondTask();
	     
		return true;
	}

	public void onFinishWork(CommandResult result) {
		
		TraceRouteDiagnostics routeDiagnostics = TraceRouteTool.parseResult(result.successMsg);
		
		String stateResponse = routeDiagnostics.getDiagnosticsState();
        int numHops = routeDiagnostics.getNumberOfRouteHops();
        String timeResponse = routeDiagnostics.getResponseTime();
        String[] hosts = routeDiagnostics.getRouteHops();

        Tr069DataFile.set(DiagnosticsState, stateResponse);
        Tr069DataFile.set(ResponseTime, "" + timeResponse);
        Tr069DataFile.set(NumberOfRouteHops, "" + numHops);
        for (int i = 0; i < hosts.length; i++) {
            String key = "Device.LAN.TraceRouteDiagnostics.RouteHops." + (i + 1) + ".HopHost";
            Tr069DataFile.set(key, hosts[i]);
        }
        
        TR069Log.sayInfo("tttttttttt Traceroute Success, now start infom acs !");
     
        HuaWeiRMS.ACSDoTraceRouteInform(TR069Utils.getManagementServer(), TR069Utils.getCommandKey(), "0");
        
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
		return bIsApend;
	}

}
