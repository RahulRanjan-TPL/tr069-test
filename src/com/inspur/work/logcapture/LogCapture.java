package com.inspur.work.logcapture;


import com.inspur.platform.Platform;
import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class LogCapture extends BaseShellCommond{
	
	private int miLogType;
	
	public LogCapture(){}
	
	public LogCapture(IShellCommondStateListen listen)
	{
		super(listen);
	}
	
	public void setLogType(int LogType)
	{
		miLogType = LogType;
	}
	
	@Override
	protected String[] getCommond() {
		String cmd[] = null;
		if(mbIsApend)
		{
			cmd = getCMDApend(Platform.get().getShellCmd(Platform.LOGCAT_CMD), mstrFilePath);
		}else
		{
			cmd = getCMD(Platform.get().getShellCmd(Platform.LOGCAT_CMD), mstrFilePath, miLogType);
		}
		return cmd;
	}

	@Override
	protected void doStopCommond() {
		// TODO Auto-generated method stub
		
	}
	
//	/*
//	 * not thread to caoture
//	 */
//	public static boolean doLogCapturePage(String filePath,int iLogType, LogCaptureStateListen listen)
//	{
//		if(listen == null)
//		{
//			TR069Log.sayError("eeeeeeeeee doLogCapture listen is null!");
//			return false;
//		}
//		
//		CaptureThread thread = new CaptureThread(filePath, getCMD("logcat -d", filePath, iLogType), listen);
//		thread.start();
//		
//		return true;
//	}
	
//	public static boolean doLogCapturePageApend(String filePath, LogCaptureStateListen listen)
//	{
//		if(listen == null)
//		{
//			TR069Log.sayError("eeeeeeeeee doLogCapture listen is null!");
//			return false;
//		}
//		
//		CaptureThread thread = new CaptureThread(filePath, getCMDApend("logcat -d", filePath), listen);
//		thread.start();
//		
//		return true;
//	}
	
//	public static void doLogCapture(final String filePath,final int iLogType, final LogCaptureStateListen listen)
//	{
//		if(listen == null)
//		{
//			TR069Log.sayError("eeeeeeeeee doLogCapture listen is null!");
//			return ;
//		}
//		
//		Thread captureThred = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				listen.onStartState();
//				ShellUtils.execCommand(getCMD("logcat", filePath, iLogType), true);
//				listen.onFinishState();
//			}
//		});
//		captureThred.start();
//		
//		Thread checkStopThread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(listen.onCheckStop())
//				{
//					
//				}
//			}
//		});
//		checkStopThread.start();
//		
//	}
	
//	public static boolean startLogCapture(String logcatCmd, String filePath,int iLogType, LogCaptureStateListen listen)
//	{
//		if(filePath == null || filePath.isEmpty())
//		{
//			if(listen != null)
//			{
//				listen.onErrorState();
//			}
//			return false;
//		}
//		
//		if(listen != null)
//		{
//			listen.onStartState();
//		}   
//	  
//	    ShellUtils.execCommand(getCMD(logcatCmd, filePath, iLogType), true);
//	    File file = new File(filePath);
//	    if (!file.exists()) {
//	        TR069Log.sayInfo("No content to logcat! \n");
//	        if(listen != null)
//			{
//				listen.onErrorState();
//			}
//	        return false;
//	    }
//	    
//	    if(listen != null)
//		{
//			listen.onFinishState();
//		}
//		
//	    
//	    return true;
//	}
	
	private String[] getCMD(String logcatCmd, String filePath ,int iLogType)
	{
//		String saveFileLocal = filePath.substring(0, filePath.lastIndexOf("/"));
//		String c0 = "mkdir -p " + saveFileLocal;
//		String c1 = "rm " + filePath;
		String c2 = logcatCmd + " -f " + filePath; //logcat -d -f /tmp/capture/log_tmp.txt
		
		switch (iLogType) {
		case LOG_INFO:
			c2 = logcatCmd + " | grep I/ > " + filePath;
			break;
		case LOG_DEBUG:
			c2 = logcatCmd + " | grep D/ > " + filePath;
			break;
		case LOG_ERROR:
			c2 = logcatCmd + " | grep E/ > " + filePath;
			break;
		default:
			
			break;
		}
		
		String c3 = "logcat -c";
//		String c4 = "chmod -R 777 " + saveFileLocal;
		return new String[]{c2, c3};
	}
	
	private String[] getCMDApend(String logcatCmd, String filePath)
	{
//		String saveFileLocal = filePath.substring(0, filePath.lastIndexOf("/"));
//		String c0 = "mkdir -p " + saveFileLocal;
		String c2 = logcatCmd + " >> " + filePath; //logcat -d -f /tmp/capture/log_tmp.txt
		String c3 = "logcat -c";
//		String c4 = "chmod -R 777 " + saveFileLocal;
		return new String[]{c2, c3};
	}
	
//	static class CaptureThread extends Thread
//	{
//		private String filePath;
//		private String[] logCatCMD;
//		private LogCaptureStateListen listen;
//		
//		public CaptureThread(String filePath, String[] cmd, LogCaptureStateListen listen){
//			logCatCMD = cmd;
//			this.filePath = filePath;
//			this.listen = listen;
//		}
//		
//		@Override
//		public void run() {
//			listen.onStartState();
//			while(listen.onCheckStop())
//			{
//				listen.onStartOneTime();
//				ShellUtils.execCommand(logCatCMD, true);
//				listen.onFinishOneTime();
//				
//				 File file = new File(filePath);
//				    if (!file.exists()) {
//				        TR069Log.sayError("No content to logcat! \n");
//				        listen.onErrorState(ERROR_FILE_NOT_EXSITS);
//				    }
//				    
//				try {
//					Thread.sleep(SLEEP_TIME);
//				} catch (InterruptedException e) {
//					 listen.onErrorState(ERROR_THREAD_EXCEPTION);
//				}
//			}
//			listen.onFinishState();
//		}
//		
//	}
}
