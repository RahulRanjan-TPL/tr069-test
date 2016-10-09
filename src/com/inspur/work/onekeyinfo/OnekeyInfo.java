package com.inspur.work.onekeyinfo;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;
import com.inspur.tr069.ShellUtils.CommandResult;
import com.inspur.work.IShellCommondStateListen;
import com.inspur.work.logcapture.LogCapture;
import com.inspur.work.networkcapture.PacketCaptureStatic;
import com.inspur.work.networkcapture.PacketCaptureStatic.PacketCaptureStateChangeListen;

public class OnekeyInfo {
	
	private final static String ONEKEY_PATH = "/tmp/onekey/";
	private final static String ONEKEY_LOG = "onekey.log";
	private final static String ONEKEY_PCAP = "onekey.pcap";
	
	private static int miAllReqult = 0;
	
	private static OnekeyInfoStateListen mlisten;
	private static String mFileName;
	
	public interface OnekeyInfoStateListen
	{
		public void onErrorState();
		public void onStartState();
		public void onFinishState();
	}
	
	public static void startInfo(String fileName, String CapCmd, OnekeyInfoStateListen listen)
	{
		
		String[] cmds = {"rm -r " + ONEKEY_PATH,
				"mkdir -p " + ONEKEY_PATH};
		ShellUtils.execCommand(cmds, true);
		
		startLog();
		startPkgCapture(CapCmd, -1);
		
		startIfConfig();
		
		mlisten = listen;
		mFileName = fileName;
	}
	
	private static void doPkgTar()
	{
//		 String fileName = TR069Tools.getUploadName("");
//         if(fileName == null)
//         	return;
//		
//         fileName = "/tmp/" + "STBDebugInfo" + fileName + ".tar.gz";
         
		if(miAllReqult == 0)
		{
			TR069Log.sayInfo("doPkgTar all task is finish!");
			String[] cmds = {"busybox tar zcf " + mFileName + " " + ONEKEY_PATH,
					"chmod 777 " + mFileName};
			ShellUtils.execCommand(cmds, true);
			mlisten.onFinishState();
		}
		
	}
	
	private static void startLog()
	{
		miAllReqult --;
		LogCapture logCapture = new LogCapture(new IShellCommondStateListen() {
			@Override
			public void onStartState() {
				
			}
			
			@Override
			public void onStartOneTime() {
				
			}
			
			@Override
			public void onFinishState(CommandResult result) {
				miAllReqult ++;
				doPkgTar();
			}
			
			@Override
			public void onFinishOneTime() {
				
			}
			
			@Override
			public void onErrorState(int errorCode) {
				
			}
			
			@Override
			public boolean onCheckStop(boolean isApend) {
				if(InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action") == null || InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action").equals("0"))
				return false;
				
				return true;
			}
		});
        logCapture.setApend(false);
        logCapture.setFilePath(ONEKEY_PATH + ONEKEY_LOG);
        logCapture.setLogType(LogCapture.LOG_ALL);
        logCapture.doCommondTask();
	}
	
	private static void startPkgCapture(String cmd, int time)
	{
		if(cmd == null || cmd.isEmpty())
			return;
		
		miAllReqult-- ;
		if(!cmd.contains("-w"))
		{
			cmd = cmd + " -w " + ONEKEY_PATH + ONEKEY_PCAP;
		}
		
		TR069Log.sayInfo("startPkgCapture:" + cmd);
		
		PacketCaptureStatic.doPkgCapture(ONEKEY_PATH + ONEKEY_PCAP, cmd, time, new PacketCaptureStateChangeListen() {
			
			@Override
			public void onStartCapture() {
				
			}
			
			@Override
			public void onFinishCapture() {
				miAllReqult ++;
				doPkgTar();
			}
			
			@Override
			public void onErrorCapture() {
				TR069Log.sayInfo("stop onErrorCapture!");
			}

			@Override
			public boolean onCheckStop() {
				if(InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action") == null || InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action").equals("0"))
				{
					TR069Log.sayInfo("stop PkgCapture!");
					return false;
				}
					
					return true;
			}
		});
	}
	
	private static void startIfConfig()
	{
		miAllReqult --;
		IfConfigCommond commond = new IfConfigCommond(new IShellCommondStateListen() {
			
			@Override
			public void onStartState() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartOneTime() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinishState(CommandResult result) {
				// TODO Auto-generated method stub
				miAllReqult ++;
				doPkgTar();
			}
			
			@Override
			public void onFinishOneTime() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onErrorState(int errorCode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onCheckStop(boolean isApend) {
				if(InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action") == null || InspurData.getInspurValue("Device.X_00E0FC.DebugInfo.Action").equals("0"))
					return false;
					
					return true;
			}
		});
		
		commond.setApend(true);
		commond.setFilePath(ONEKEY_PATH + "ifconfig.log");
		commond.doCommondTask();
	}
}
