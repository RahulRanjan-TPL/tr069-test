package com.inspur.work.networkcapture;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;
import com.inspur.tr069.ShellUtils.CommandResult;

public class PacketCaptureStatic {
	
	private static TimerTask sCaptureTask;
	private static Timer sCaptureTimer;
	
	static{
		sCaptureTimer = new Timer();
	}
	
	public interface PacketCaptureStateChangeListen {
		public void onStartCapture();

		public void onErrorCapture();

		public boolean onCheckStop();
		
		public void onFinishCapture();
		
	}

	public static void doPkgCapture(final String filePath, final String cmd, int iTime, final PacketCaptureStateChangeListen listen) {
		
		if(filePath == null || cmd == null || filePath.isEmpty() || cmd.isEmpty())
		{
			listen.onErrorCapture();
			return;
		}
		
		Thread captureThread = new Thread(new Runnable() {

			@Override
			public void run() {
				listen.onStartCapture();
				CommandResult result = ShellUtils.execCommand(cmd, true);
				if (result.result != 0) {
					listen.onErrorCapture();
				}
				
				  File captrueFile = new File(filePath);
		            if (!captrueFile.exists()) {
		           	 listen.onErrorCapture();
		           	 return;
		            }
				
				listen.onFinishCapture();
			}
		});
		captureThread.start();
		
		if(iTime == -1)
		{
			CapTureStopThread thread = new CapTureStopThread(listen);
			thread.start();
		}else{
			startTimer(iTime, filePath, listen);
		}
	}
	
	private static void startTimer(int iTime, final String filePath, final PacketCaptureStateChangeListen listen)
	{
		sCaptureTask = new TimerTask() {
             public void run() {
                
                 killTcpdump();
//                 File captrueFile = new File(filePath);
//                 if (!captrueFile.exists()) {
//                	 listen.onErrorCapture();
//                 } else {
//                     listen.onFinishCapture();
//                 }
             }
         };
         sCaptureTimer.schedule(sCaptureTask, iTime * 1000);
	}
	
	public static void killTcpdump() {
		TR069Log.say("keill tcpdump");
		ShellUtils.execCommand("busybox killall tcpdump", true);
    }
	
	public static void deleteCpaFile(String local) {
        File file = new File(local);
        if (file.isDirectory()) {
            File[] filelist = file.listFiles();
            if (filelist != null) {
                TR069Log.say("file num:=" + filelist.length);
                for (int i = 0; i < filelist.length; i++) {
                    if (filelist[i].getName().endsWith(".pcap")) {
                        filelist[i].delete();
                    }
                }
            } else {
                TR069Log.say("no files in " + file.getAbsolutePath());
            }
        } else {
            TR069Log.say("is not dir " + file.getAbsolutePath());
        }
    }
	
	static class CapTureStopThread extends Thread
	{
		private PacketCaptureStateChangeListen listen;
		
		public CapTureStopThread(PacketCaptureStateChangeListen listen)
		{
			this.listen = listen;
		}
		
		@Override
		public void run() {
			while(listen.onCheckStop())
			{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			killTcpdump();
		}
		
	}
}
