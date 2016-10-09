package com.inspur.work.onekeyinfo;

import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class IfConfigCommond extends BaseShellCommond{

	public IfConfigCommond(IShellCommondStateListen listen) {
		super(listen);
	}
	
	@Override
	protected String[] getCommond() {
//		String[] cmds = {""};
		
		if(mbIsApend)
		{
			return getApendCmds();
		}
		return getPageCmds();

	}

	private String[] getApendCmds()
	{
		String saveFileLocal = mstrFilePath.substring(0, mstrFilePath.lastIndexOf("/"));
		String c0 = "mkdir -p " + saveFileLocal;
	
		String c2 = "(netcfg && echo =====================================================) >> " + mstrFilePath; //logcat -d -f /tmp/capture/log_tmp.txt
		
		String c4 = "chmod -R 777 " + saveFileLocal;
		return new String[]{c0, c2, c4};
	}
	
	private String[] getPageCmds()
	{
		String saveFileLocal = mstrFilePath.substring(0, mstrFilePath.lastIndexOf("/"));
		String c0 = "mkdir -p " + saveFileLocal;
		String c1 = "rm " + mstrFilePath;
		String c2 = "netcfg > " + mstrFilePath; //logcat -d -f /tmp/capture/log_tmp.txt
		
		String c4 = "chmod -R 777 " + saveFileLocal;
		return new String[]{c0, c1, c2, c4};
	}

	@Override
	protected void doStopCommond() {
		// TODO Auto-generated method stub
		
	}
	
}
