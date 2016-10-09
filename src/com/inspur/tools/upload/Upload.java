package com.inspur.tools.upload;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;

public class Upload {
	
	public static final int FTP = 0;
	public static final int UDP = 1;
	
	public interface UploadStateListen
	{
		
	}
	
	public static boolean doUploadFile(int uploadType, String url, String file, String user, String password)
	{
		return doUploadFile(uploadType, url, file, user, password, null);
	}
	
	public static boolean doUploadFile(int uploadType, String url, String file, String user, String password, String data)
	{
		switch (uploadType) {
		case FTP:
			return uploadFTP(url, file, user, password);
		case UDP:
			uploadUDP(url, file, data);
			break;
		default:
			break;
		}
		
		return false;
	}
	
	private static boolean uploadFTP(String url, String file, String user, String password)
	{
		if(user == null)
			user = "";
		if(password == null)
			password = "";
		
        String[] tmp = new String[2];
        tmp = url.substring(7).split(":");
        String host = tmp[0];
        
        Pattern pattern = Pattern.compile("(?<=:).\\d[0-9]*");
		Matcher matcher = pattern.matcher(url);
		String strPort = "80";
		if(matcher.find())
			strPort = matcher.group(0);
		
        int port = Integer.valueOf(strPort).intValue();

        String c1 = "busybox chmod 777 " + file;
        String c2 = "busybox chown system " + file;
        String c3 = "busybox chgrp system " + file;
        String[] commands = {
                c1, c2, c3
        };
        ShellUtils.execCommand(commands, true);

        TR069Log.say("Upload the file:{" + file + "}to sftp://" + host + ":" + port);
        TR069Log.say("name=[" + user + "]");
        TR069Log.say("psd=[" + password + "]"); 

        // File filetmp = new File(file);
        // FileTool.upLoadFromProduction(host, port, username, password, "./", filetmp.getName(), file);
        String directory = "";
        if(url.lastIndexOf("/") > 7)
        	directory = url.substring(url.lastIndexOf("/") + 1, url.length());
        
        return SFTPAdapter.uploadeFile(host, port, user, password, directory, file);
	}

	private static boolean uploadUDP(String url, String file, String data)
	{
		int portNum = 0;

		String host = url.substring(0, url.lastIndexOf(":"));
		String port = url.substring(url.lastIndexOf(":") + 1, url.length());
		if(port != null && !port.isEmpty())
			portNum = Integer.valueOf(port);
		
		 TR069Log.say("uploadUDP host=[" + host + "] " + "port=[" + port + "] " + "file=[" + file + "]");
		
		Sender theSender;
        try {
            theSender = new UDPSender(InetAddress.getByName(host), portNum);
            theSender.sendFile(new File(file), data);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
	}
}
