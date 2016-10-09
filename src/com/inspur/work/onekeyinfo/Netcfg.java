package com.inspur.work.onekeyinfo;

import com.inspur.platform.Platform;
import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class Netcfg extends BaseShellCommond{

	private String mCmd;
	
	public Netcfg(IShellCommondStateListen listen)
	{
		super(listen);
		mCmd = Platform.get().getShellCmd(Platform.NETCFG_CMD);
	}
	
	@Override
	protected String[] getCommond() {
		String cmd = null;
		if(mstrFilePath != null || "".equals(mstrFilePath))
		{
			if(mbIsApend)
			{
				cmd = "(" + mCmd + " && echo =====================================================) >> " + mstrFilePath;
			}else
			{
				cmd = mCmd + " > " + mstrFilePath;
			}
		}else
		{
			cmd = mCmd;
		}
		
		return new String[]{cmd};
	}

	@Override
	protected void doStopCommond() {
		// TODO Auto-generated method stub
		
	}

}
