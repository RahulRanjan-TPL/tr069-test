package com.inspur.work;

import com.inspur.tr069.ShellUtils.CommandResult;

public interface IShellCommondStateListen {
	
		public void onStartState();
		public void onFinishState(CommandResult result);
		public void onStartOneTime();
		public void onFinishOneTime();
		public void onErrorState(int errorCode);
		public boolean onCheckStop(boolean bIsApend);
	
}
