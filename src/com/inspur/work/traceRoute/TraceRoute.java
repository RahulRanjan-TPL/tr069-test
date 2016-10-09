package com.inspur.work.traceRoute;

import com.inspur.platform.Platform;
import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class TraceRoute extends BaseShellCommond{


	private String mMaxHopCount;
	private String mHost;
	
	public TraceRoute(){}
	
	public TraceRoute(IShellCommondStateListen listen)
	{
		super(listen);
	}
	
	public void setMaxHopCount(String maxHopCount) {
		if(Integer.parseInt(maxHopCount) > 10 )
		{
			this.mMaxHopCount = "10";
		}else
		{
			this.mMaxHopCount = maxHopCount;
		}
	}
	
	public void setHost(String host) {
		this.mHost = host;
	}
	
	@Override
	protected void doStopCommond() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String[] getCommond() {
		String cmd = Platform.get().getShellCmd(Platform.TRACEROUTE_CMD) + " -m " + mMaxHopCount + " -w 1 " + mHost;
		if(mstrFilePath != null || "".equals(mstrFilePath))
		{
			if(mbIsApend)
			{
				cmd = cmd + " >> " + mstrFilePath;
			}else
			{
				cmd = cmd + " > " + mstrFilePath;
			}
		}
		
		return new String[]{cmd};
	}

}
