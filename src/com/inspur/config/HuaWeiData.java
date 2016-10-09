package com.inspur.config;

import com.inspur.tools.Tr069DataFile;

public class HuaWeiData {

	/* 机顶盒连接网管TMC需要HTTP摘要认证，机顶盒上需要配置访问网管的用户名密码 */
	// public static String ManagementServerURL =
	// "http://218.205.67.131:37020/acs";
	public static String ManagementServerURL = "http://120.87.3.143:37020/acs";
	public static String huaweiACSUsername = "testcpe";
	public static String huaweiACSPassword = "ac5entry";

	public static String getPassword(String url) {

		String password = Tr069DataFile.get("Device.ManagementServer.Password");
		if (password == null || password.trim().equals("")) {
			return huaweiACSPassword;
		}
		return password;
	}

	public static String getUsername(String url) {

		String userName = Tr069DataFile.get("Device.ManagementServer.Username");
		if (userName == null || userName.trim().equals("")) {
			return huaweiACSUsername;
		}
		return userName;
	}
}
