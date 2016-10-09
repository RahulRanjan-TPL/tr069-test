package com.inspur.work;

import com.inspur.tools.TR069Log;

public abstract class BaseWork implements IWork{
	
	protected final String TAG = getClass().getSimpleName();
	protected boolean mWorkRun = false;
	
	protected void startWorkBase()
	{
		TR069Log.sayInfo(TAG + ":work start!");
    	mWorkRun = true;
	}
	
	@Override
	public void onFinishWork() {
		TR069Log.sayInfo(TAG + ":work finish!");
		mWorkRun = false;
	}

	@Override
	public void onErrorWork() {
		TR069Log.sayInfo(TAG + ":work error!");
		mWorkRun = false;
	}
	
	@Override
	public boolean doWork() {
		if(!mWorkRun)
			return startWork();
		
		return false;
	}

	public abstract boolean startWork();
	
}
