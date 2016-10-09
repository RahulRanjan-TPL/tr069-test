package com.inspur.work.bandwidth;

public class NetWorkSpeedInfo {
	/** Network speed */
	public long speed = 0; // 平均速度

	/** Had finished bytes */
	public long hadFinishedBytes = 0; // 已下载大小

	/** Total bytes of a file, default is 1024 bytes,1K */
	public long totalBytes = 0; // 文件大小

	public long minSpeed = 0;// 最小速度
	public long maxSpeed = 0;// 最大速度
	public long currentSpeed = 0;// 单次下载速度
	
	public static String doubletoString(double d) {

		String str;
		d = d * 8;
		if (d > 1024) {

			str = ((d / 1024.0) * 1.1 + "");
			if (null != str && str.length() > 4) {

				str = str.substring(0, 4) + "Mbps";
			} else {

				str = str + "Mbps";
			}
		} else {

			str = d + "Kbps";
		}
		return str;
	}
}
