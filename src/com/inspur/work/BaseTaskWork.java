package com.inspur.work;

public abstract class BaseTaskWork extends BaseWork{

	private int miSleepSecond = 5;
	private int miDelayTime = 0;
	
	public void setTaskSleepTime(int iTime)
	{
		miSleepSecond = iTime;
	}
	
	protected void startTask()
	{
		TaskThread taskThread = new TaskThread();
		taskThread.start();
	}
	
	protected void setDelayTime(int iTime)
	{
		miDelayTime = iTime;
	}
	
	protected abstract boolean checkStopTask();
	protected abstract void doTaskWork();
	
	class TaskThread extends Thread
	{

		@Override
		public void run() {
			
			if(miDelayTime != 0)
			{
				try {
					Thread.sleep(miDelayTime * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			startWorkBase();
			do{

				doTaskWork();
				try {
					Thread.sleep(miSleepSecond * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}while(checkStopTask());
			
			onFinishWork();
		}
		
	}
}
