package com.inspur.work.networkcapture;

import com.inspur.platform.Platform;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;
import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class PacketCapture extends BaseShellCommond{
	
//	private static TimerTask sCaptureTask;
//	private static Timer sCaptureTimer;
//	
//	static{
//		sCaptureTimer = new Timer();
//	}
	
	private String mIp;
	private String mPort;
	
	private String mCmd;
	
	public void setIp(String ip)
	{
		mIp = ip;
	}
	
	public void setPort(String port)
	{
		mPort = port;
	}
	
	public void setCommond(String cmd)
	{
		mCmd = cmd;
	}
	
	public PacketCapture(IShellCommondStateListen listen)
	{
		super(listen);
	}
	
	@Override
	protected String[] getCommond() {
		
		String cmd = null;
		if(mCmd != null && !mCmd.isEmpty())
		{
			 if(!mCmd.contains("-w"))
        	 {
				 cmd = mCmd + " -w " + mstrFilePath;
        	 }
			
		}else
		{
			cmd = Platform.get().getShellCmd(Platform.TCPDUMP_CMD) + " -p -vv -i any -s 0 -w " + mstrFilePath;
			
			if(mIp != null && !mIp.trim().equals(""))
			{
				cmd += " \'host " + mIp + "\'";
			}
			if(mPort != null && !mPort.trim().equals(""))
			{
				cmd = cmd.substring(0, cmd.length() - 1) +   " and  port " + mPort + "\'";
			}
		}

         TR069Log.sayInfo("cccccccccc start capture: " + cmd);
		
		return new String[]{cmd};
	}
	
	@Override
	protected void doStopCommond() {
		stopCommond();
	}
	
	public static void stopCommond()
	{
		TR069Log.sayInfo("kill tcpdump");
		ShellUtils.execCommand(Platform.get().getShellCmd(Platform.KILLALL_CMD) + " tcpdump", true);
	}
	
//	public interface PacketCaptureStateChangeListen {
//		public void onStartCapture();
//
//		public void onErrorCapture();
//
//		public boolean onCheckStop();
//		
//		public void onFinishCapture();
//		
//	}
//
//	public static void doPkgCapture(final String filePath, final String cmd, int iTime, final PacketCaptureStateChangeListen listen) {
//		
//		if(filePath == null || cmd == null || filePath.isEmpty() || cmd.isEmpty())
//		{
//			listen.onErrorCapture();
//			return;
//		}
//		
//		Thread captureThread = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				listen.onStartCapture();
//				CommandResult result = ShellUtils.execCommand(cmd, true);
//				if (result.result != 0) {
//					listen.onErrorCapture();
//				}
//				
//				  File captrueFile = new File(filePath);
//		            if (!captrueFile.exists()) {
//		           	 listen.onErrorCapture();
//		           	 return;
//		            }
//				
//				listen.onFinishCapture();
//			}
//		});
//		captureThread.start();
//		
//		if(iTime == -1)
//		{
//			CapTureStopThread thread = new CapTureStopThread(listen);
//			thread.start();
//		}else{
//			startTimer(iTime, filePath, listen);
//		}
//	}
//	
//	private static void startTimer(int iTime, final String filePath, final PacketCaptureStateChangeListen listen)
//	{
//		sCaptureTask = new TimerTask() {
//             public void run() {
//                
//                 killTcpdump();
////                 File captrueFile = new File(filePath);
////                 if (!captrueFile.exists()) {
////                	 listen.onErrorCapture();
////                 } else {
////                     listen.onFinishCapture();
////                 }
//             }
//         };
//         sCaptureTimer.schedule(sCaptureTask, iTime * 1000);
//	}
//	
//	public static void killTcpdump() {
//		TR069Log.sayInfo("keill tcpdump");
//		ShellUtils.execCommand("busybox killall tcpdump", true);
//    }
//	
//	public static void deleteCpaFile(String local) {
//        File file = new File(local);
//        if (file.isDirectory()) {
//            File[] filelist = file.listFiles();
//            if (filelist != null) {
//                TR069Log.sayInfo("file num:=" + filelist.length);
//                for (int i = 0; i < filelist.length; i++) {
//                    if (filelist[i].getName().endsWith(".pcap")) {
//                        filelist[i].delete();
//                    }
//                }
//            } else {
//                TR069Log.sayInfo("no files in " + file.getAbsolutePath());
//            }
//        } else {
//            TR069Log.sayInfo("is not dir " + file.getAbsolutePath());
//        }
//    }
//	
//	static class CapTureStopThread extends Thread
//	{
//		private PacketCaptureStateChangeListen listen;
//		
//		public CapTureStopThread(PacketCaptureStateChangeListen listen)
//		{
//			this.listen = listen;
//		}
//		
//		@Override
//		public void run() {
//			while(listen.onCheckStop())
//			{
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			killTcpdump();
//		}
//		
//	}

}
