package com.inspur.work.ping;

import com.inspur.platform.Platform;
import com.inspur.work.BaseShellCommond;
import com.inspur.work.IShellCommondStateListen;

public class Ping extends BaseShellCommond{

	private String mCount;
	private String mSize;
	private String mTimeout;
	private String mIp;
	
	public Ping(){}
	
	public Ping(IShellCommondStateListen listen)
	{
		super(listen);
	}
	
	public void setCount(String Count) {
		this.mCount = Count;
	}

	public void setSize(String Size) {
		this.mSize = Size;
	}

	public void setTimeout(String Timeout) {
		this.mTimeout = Timeout;
	}

	public void setIp(String Ip) {
		this.mIp = Ip;
	}

	@Override
	protected String[] getCommond() {
		String cmd = Platform.get().getShellCmd(Platform.PING_CMD) + " -q -c" + mCount + " -s" + mSize + " -W" + Integer.parseInt(mTimeout) / 1000 + " " + mIp;
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

	@Override
	protected void doStopCommond() {
		// TODO Auto-generated method stub
		
	}

}
