package com.inspur.work;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;
import com.inspur.tr069.ShellUtils.CommandResult;


public abstract class BaseShellCommond {

	protected final String TAG = getClass().getSimpleName();
	
	private static final int SLEEP_TIME = 5000;
	
	public static final int LOG_ALL = 0;
	public static final int LOG_INFO = 1;
	public static final int LOG_DEBUG = 2;
	public static final int LOG_ERROR = 3;
	
	public static final int ERROR_FILE_NOT_EXSITS = 0;
	public static final int ERROR_THREAD_EXCEPTION = 1;
	
	private IShellCommondStateListen mlisten;
	
	
	protected Timer mCommondTimer;
	private int miExecTime = -1;
	
	protected boolean mbIsApend = false;
	protected String mstrFilePath;	
	
	private boolean mbNeedStopThread = false;
	
	public BaseShellCommond(){}
	
	public BaseShellCommond(IShellCommondStateListen listen)
	{
		mlisten = listen;
	}
	
	public void setApend(boolean isApend)
	{
		mbIsApend = isApend;
	}
	
	public void setFilePath(String FilePath)
	{
		mstrFilePath = FilePath;
	}
	
	public void setNeedStopThread()
	{
		mbNeedStopThread = true;
	}
	
	public void setCommondTime(int time)
	{
		if(mCommondTimer == null)
			mCommondTimer = new Timer();
		
		miExecTime = time;
	}
	
	private void doTimerStop()
	{
		TR069Log.sayInfo(TAG + "is time to stop");
		doStopCommond();
	}
	
	private void doThreadStop()
	{
		TR069Log.sayInfo(TAG + "is thread to stop");
		doStopCommond();
	}
	
	protected abstract void doStopCommond();
	
	public boolean doCommondTask()
	{
		if(mlisten == null )
		{
			TR069Log.sayError("eeeeeeeeee " + getClass().getSimpleName() + " do Commonderror!");
			return false;
		}
		
		CommondThread thread = new CommondThread(mstrFilePath, getCommond());
		thread.start();
		
		if(mbNeedStopThread)
		{
			StopThread stopThread = new StopThread();
			stopThread.start();
		}
		
		if(mCommondTimer != null && miExecTime > 0)
		{
			mCommondTimer.schedule(new StopTask(), miExecTime * 1000);
		}
		return true;
	}
	
	protected abstract String[] getCommond();
	
	class CommondThread extends Thread
	{
		private String filePath;
		private String[] logCatCMD;
		
		public CommondThread(String filePath, String[] cmd){
			logCatCMD = cmd;
			this.filePath = filePath;
		}
		
		@Override
		public void run() {
			mlisten.onStartState();
			CommandResult result = null;
			do
			{
				TR069Log.sayInfo("xxxxxxxxxx" + TAG + "thread run");
				mlisten.onStartOneTime();
				result = ShellUtils.execCommand(logCatCMD, true);
				mlisten.onFinishOneTime();
				
				if(filePath != null && !"".equals(filePath))
				{
					File file = new File(filePath);
					if (!file.exists()) {
						TR069Log.sayError("No content to logcat! \n");
						mlisten.onErrorState(ERROR_FILE_NOT_EXSITS);
					}
				}
				    
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					 mlisten.onErrorState(ERROR_THREAD_EXCEPTION);
				}
			}while(mlisten.onCheckStop(mbIsApend));
			mlisten.onFinishState(result);
		}
		
	}
	
	class StopThread extends Thread
	{
		@Override
		public void run() {
			while(mlisten.onCheckStop(mbIsApend))
			{
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			doThreadStop();
		}
	}
	
	class StopTask extends TimerTask
	{
		@Override
		public void run() {
			doTimerStop();
		}
		
	}
}
