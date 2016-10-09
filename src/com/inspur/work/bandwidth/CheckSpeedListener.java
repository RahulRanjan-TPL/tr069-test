package com.inspur.work.bandwidth;


public interface CheckSpeedListener {
	void downloadFinish(NetWorkSpeedInfo netWorkSpeedInfo);
	
	void downloadError(String errorcode);
}
