package com.inspur.work.bandwidth;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.inspur.tools.TR069Log;

import android.text.TextUtils;



public class DownloadThread extends Thread {
	private static final String TAG = DownloadThread.class.getSimpleName();
	private static final String CONTENT_TYPE = "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
	private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
	private static final int TIMEOUT = 20 * 1000; // 瓒呮椂
	private static final int BUFFER_SIZE = 1024; // 缂撳瓨1M
	private CheckSpeedListener listener;
	private boolean stopFlag;

	private int downLoadedFileSize = 0;
	private CalculateThread calculateThread;

	private int ignoreCalculateTimes = 1;
	private final int MAX_CALCULATE_TIMEs = 6;

	private double percent = 0.8;

	private String mDownloadUrl;
	private String mUserName = "";
	private String mPassword = "";
	
	public DownloadThread(String downloadUrl, CheckSpeedListener listener) {
		TR069Log.sayInfo(TAG + " DownloadThread created.");
		this.listener = listener;
		mDownloadUrl = downloadUrl;
	}

	public void setUserAndPassword(String username, String password){
		
		if(username == null || password == null 
				|| "".equals(username.trim()) || "".equals(password.toCharArray()))
		{
			TR069Log.sayError("username=" + username + ",passwork=" + password);
			return;
		}
		mUserName = username;
		mPassword = password;
		
//		Authenticator.setDefault(new DownloadAuthenticator());
		
	}
	
	public void downloadStop() {
		stopFlag = true;
		TR069Log.sayInfo(TAG + " downloadStop() stopFlag = " + stopFlag);
		if (calculateThread != null) {
			
		try{
			calculateThread.calculateStop();
			calculateThread.interrupt();
			calculateThread = null;
		}catch(Exception e)
		{
			
		}
		}
	}

	public int getDownLoadedFileSize() {
		TR069Log.sayInfo(TAG + " getDownLoadedFileSize() downLoadedFileSize = " + downLoadedFileSize);
		return downLoadedFileSize;
	}
	
	@Override
	public void run() {
		NetWorkSpeedInfo netWorkSpeedInfo = new NetWorkSpeedInfo();
		HttpURLConnection http = null;
		InputStream inStream = null;
		long fileLength;

		try {
			//String url = SystemProperties.get("inspur.netspeed.url", "");
//			String url = "http://10.8.10.200/lilili";
			TR069Log.sayInfo(TAG + " run() url = " + mDownloadUrl);
			if (TextUtils.isEmpty(mDownloadUrl)) {
				TR069Log.sayInfo(TAG + " run() url is empty.");
				listener.downloadError("102050");
				return;
			}
			
			URL downUrl = new URL(mDownloadUrl);
			http = (HttpURLConnection) downUrl.openConnection();
			if(mUserName != null && mPassword != null 
					&& !"".equals(mUserName.trim()) && !"".equals(mPassword.toCharArray()))
			{
				http.addRequestProperty("Authorization", "Basic "+ Base64.encode((mUserName + ":" + mPassword) .getBytes()));
			}
			http.setRequestMethod("GET");
			http.setConnectTimeout(TIMEOUT);
			http.setReadTimeout( TIMEOUT ); 
			http.setRequestProperty("Accept", CONTENT_TYPE);
			http.setRequestProperty("Accept-Language", "zh-CN");
			http.setRequestProperty("Referer", downUrl.toString());
			http.setRequestProperty("Charset", "UTF-8");
			http.setRequestProperty("User-Agent", USER_AGENT);
			http.setRequestProperty("Connection", "Keep-Alive");
			

			fileLength = http.getContentLength();
			TR069Log.sayInfo(TAG + " run() fileLength = " + fileLength);

			netWorkSpeedInfo.totalBytes = fileLength;
			inStream = http.getInputStream();
			byte[] buffer = new byte[BUFFER_SIZE]; // 缂撳啿鏁版嵁澶у皬
			int offset = 0;
			calculateThread = new CalculateThread(this.listener, netWorkSpeedInfo);
			calculateThread.start();
			while (!stopFlag) {
				if ((offset = inStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
					downLoadedFileSize += offset;
				}
			}
			TR069Log.sayInfo(TAG + " run() totalBytes = " + netWorkSpeedInfo.totalBytes + ", hadFinishedBytes = "
					+ netWorkSpeedInfo.hadFinishedBytes);
			downloadStop();
			listener.downloadFinish(netWorkSpeedInfo);
		} catch (Exception e) {
			TR069Log.sayError(TAG + " run() Exception = " + e.getMessage());
			e.printStackTrace();
			listener.downloadError("102051");
		} finally {
			TR069Log.sayInfo(TAG + " run() finally block. ");
			try {
				if (http != null) {
					http.disconnect();
				}
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				TR069Log.sayError(TAG + " run() IOException = " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private class CalculateThread extends Thread {
		private CheckSpeedListener listener;
		private boolean calStopFlag;
		private int calTime = 0;
		private List<Long> avaiableList = new ArrayList<Long>();
		// private List<Integer> discardedTimeList = new ArrayList<Integer>();
		// private List<Long> discardedDataList = new ArrayList<Long>();
		// private final int DISCARDED_LIMIT = 8;

		private NetWorkSpeedInfo netWorkSpeedInfo;

		public CalculateThread(CheckSpeedListener listener, NetWorkSpeedInfo netWorkSpeedInfo) {
			TR069Log.sayInfo(TAG + " CalculateThread created.");
			this.listener = listener;
			this.netWorkSpeedInfo = netWorkSpeedInfo;
		}

		public void calculateStop() {
			TR069Log.sayInfo(TAG + " CalculateThread calculateStop()");
			calStopFlag = true;
		}

		@Override
		public void run() {
			while (!calStopFlag) {
				CalculateSpeed(calTime);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					TR069Log.sayError(TAG + " CalculateThread run() InterruptedException = " + e.getMessage());
					e.printStackTrace();
				}
				calTime++;
				if (calTime == MAX_CALCULATE_TIMEs) {
					TR069Log.sayInfo(TAG + " CalculateThread run() calTime = " + calTime + ", stop check speed.");
					downloadStop();
				}
			}
		}

		private void CalculateSpeed(int calTime) {
			TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() ------------------------------------------- calTime = "
					+ calTime + " ---------------------------------------------------------");
			if (null != netWorkSpeedInfo) {
				int downLoadedFileSize = getDownLoadedFileSize();
				netWorkSpeedInfo.currentSpeed = (downLoadedFileSize - netWorkSpeedInfo.hadFinishedBytes) / 1000;
				TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() downLoadedFileSize = " + downLoadedFileSize
						+ ", currentSpeed = " + netWorkSpeedInfo.currentSpeed);

				if (calTime < ignoreCalculateTimes) {
					// 鍓� IGNORE_CALCULATE_TIMES 娆″彧璁板綍鏈�澶ч�熷害
					if (netWorkSpeedInfo.currentSpeed > netWorkSpeedInfo.maxSpeed) {// 璁剧疆鏈�澶ч�熷害
						netWorkSpeedInfo.maxSpeed = netWorkSpeedInfo.currentSpeed;
						TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() ++++++++++++++++++ calTime = " + calTime
								+ ", maxSpeed = " + netWorkSpeedInfo.maxSpeed + "  +++++++++++++++++++");
					}
				} else {
					if (netWorkSpeedInfo.currentSpeed >= netWorkSpeedInfo.maxSpeed * percent) {
						if (netWorkSpeedInfo.currentSpeed > netWorkSpeedInfo.maxSpeed) {
							netWorkSpeedInfo.maxSpeed = netWorkSpeedInfo.currentSpeed;
						}
						if (netWorkSpeedInfo.minSpeed == 0
								|| netWorkSpeedInfo.currentSpeed < netWorkSpeedInfo.minSpeed) {
							netWorkSpeedInfo.minSpeed = netWorkSpeedInfo.currentSpeed;
						}
						avaiableList.add(netWorkSpeedInfo.currentSpeed);

						updateCheckState();
						// if (discardedTimeList.contains(calTime - 1)) {
						// TR069Log.sayInfo(TAG,
						// "CalculateThread CalculateSpeed() &&&&&&&&&&&&&&&&&&&
						// clear discarded data &&&&&&&&&&&&&&&&&");
						// discardedDataList.clear();
						// discardedTimeList.clear();
						// }
					} else {
						TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() %%%%%%%%%%%%%%%%%%%% calTime = " + calTime
								+ ", currentSpeed is discarded  %%%%%%%%%%%%%%%%%%%%");
						// discardedTimeList.add(calTime);
						// discardedDataList.add(netWorkSpeedInfo.currentSpeed);
						// if (discardedDataList.size() >= DISCARDED_LIMIT) {
						// avaiableList.addAll(discardedDataList);
						// TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() calTime
						// = " + calTime
						// + ", discardedList is enabled, avaiable size = " +
						// avaiableList.size());
						// netWorkSpeedInfo.minSpeed = getMinValue(avaiableList,
						// calTime);
						//
						// updateCheckState();
						// discardedDataList.clear();
						// discardedTimeList.clear();
						// }
					}
				}
				netWorkSpeedInfo.hadFinishedBytes = downLoadedFileSize;
			} else {
				TR069Log.sayInfo(TAG + " CalculateThread CalculateSpeed() netWorkSpeedInfo is unll , calTime = " + calTime);
				this.listener.downloadError("102052");
			}
		}

		private void updateCheckState() {
			netWorkSpeedInfo.speed = getAverageValue(avaiableList, calTime);
//			this.listener.download(netWorkSpeedInfo);
//			TR069Log.sayInfo(TAG,
//					"CalculateThread CalculateSpeed()  ***********************************   calTime = " + calTime
//							+ ", maxSpeed = " + netWorkSpeedInfo.maxSpeed + ", minSpeed = " + netWorkSpeedInfo.minSpeed
//							+ ", speed = " + netWorkSpeedInfo.speed + "  ***********************************");
		}

		// private long getMinValue(List<Long> list, int calTime) {
		// if (null == list || 0 == list.size()) {
		// TR069Log.sayInfo(TAG + " getMinValue() calTime = " + calTime + ", list is
		// empty.");
		// return 0;
		// }
		// long min = list.get(0);
		// for (Long temp : list) {
		// if (temp < min) {
		// min = temp;
		// }
		// }
		// TR069Log.sayInfo(TAG + " getMinValue() calTime = " + calTime + ", size = " +
		// list.size() + ", min = " + min);
		// return min;
		// }

		private long getAverageValue(List<Long> list, int calTime) {
			if (null == list || 0 == list.size()) {
				TR069Log.sayInfo(TAG + " getAverageValue() calTime = " + calTime + ", list is empty.");
				return 0;
			}
			long sum = 0;
			for (Long temp : list) {
				sum += temp;
			}
			long average = Math.round((double) sum / list.size());
			TR069Log.sayInfo(TAG + " getAverageValue() calTime = " + calTime + ", size = " + list.size() + ", average = " + average);
			return average;
		}
	}
	
//	class DownloadAuthenticator extends Authenticator
//	{
//
//		@Override
//		protected PasswordAuthentication getPasswordAuthentication() {
////	        String username = "lili";
////	        String password = "123456";
//	        return new PasswordAuthentication(mUserName, mPassword.toCharArray());
//		}
//
//		@Override
//		protected URL getRequestingURL() {
//			// TODO Auto-generated method stub
//			return super.getRequestingURL();
//		}
//
//		@Override
//		protected RequestorType getRequestorType() {
//			// TODO Auto-generated method stub
//			return super.getRequestorType();
//		}
//		
//	}
}
