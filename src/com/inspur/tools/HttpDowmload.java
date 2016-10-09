package com.inspur.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDowmload {
	
	private static final String CONTENT_TYPE = "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*";
	private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";
	private static final int TIMEOUT = 20 * 1000; // 瓒呮椂
	private static final int BUFFER_SIZE = 1024; // 缂撳瓨1M
	
	public static boolean startDownload(String url, String filePath)
	{
		HttpURLConnection http = null;
		InputStream inStream = null;
		
		File file = new File(filePath);
		FileOutputStream fileOutputStream = null;
		try {
//			String url = "http://10.8.10.200/EMMC.bin";

			URL downUrl = new URL(url);
			http = (HttpURLConnection) downUrl.openConnection();
			http.setRequestMethod("GET");
			http.setConnectTimeout(TIMEOUT);
			http.setReadTimeout( TIMEOUT ); 
			http.setRequestProperty("Accept", CONTENT_TYPE);
			http.setRequestProperty("Accept-Language", "zh-CN");
			http.setRequestProperty("Referer", downUrl.toString());
			http.setRequestProperty("Charset", "UTF-8");
			http.setRequestProperty("User-Agent", USER_AGENT);
			http.setRequestProperty("Connection", "Keep-Alive");

//			fileLength = http.getContentLength();

//			netWorkSpeedInfo.totalBytes = fileLength;
			inStream = http.getInputStream();
			byte[] buffer = new byte[BUFFER_SIZE]; // 缂撳啿鏁版嵁澶у皬
			
			fileOutputStream = new FileOutputStream(file);
			
//			int offset = 0;
//			calculateThread = new CalculateThread();
//			calculateThread.start();
			while (inStream.read(buffer, 0, BUFFER_SIZE) != -1) {
//				if ((offset = inStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
//					System.out.println("inStream.read," + downLoadedFileSize);
//					downLoadedFileSize += offset;
					
					fileOutputStream.write(buffer);
//				}
			}
			fileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (http != null) {
					http.disconnect();
				}
				if (inStream != null) {
					inStream.close();
				}
				if(fileOutputStream != null)
				{
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
