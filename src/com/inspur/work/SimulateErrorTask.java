package com.inspur.work;

import java.util.Random;

import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;

public class SimulateErrorTask extends BaseTaskWork{

	@Override
	protected boolean checkStopTask() {
		return true;
	}

	@Override
	protected void doTaskWork() {
		TR069Log.sayInfo("simulate  some error to allerrors");
	    int max = 9001;
	    int min = 9019;
	    int fake = new Random().nextInt(max) % (max - min + 1) + min;
	    GlobalContext.addError(String.valueOf(fake));
	}

	@Override
	public boolean startWork() {
		startTask();
		return true;
	}

}
