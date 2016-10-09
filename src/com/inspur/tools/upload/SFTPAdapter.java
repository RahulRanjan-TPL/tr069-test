
package com.inspur.tools.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

import com.inspur.tools.TR069Log;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SFTPAdapter {

    private final int defPort = 22;
    private ChannelSftp sftp = null;

    /**
     * 连接sftp服务器
     * 
     * @param host 主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public ChannelSftp connect(String host, int port, String username, String password) {
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            TR069Log.say("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            TR069Log.say("Session connected.");
            TR069Log.say("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;

            TR069Log.say("Connected to " + host);
        } catch (Exception e) {

        }
        return sftp;
    }

    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
            } else if (this.sftp.isClosed()) {
                TR069Log.say("sftp is closed already");
            }
        }
    }

    /**
     * 上传文件
     * 
     * @param directory 上传的目录
     * @param uploadFile 要上传的文件
     */
    public boolean upload(String directory, String uploadFile) {
        try {
            TR069Log.say("pwd=[" + sftp.pwd() + "]");
            // sftp.cd("./");
            File file = new File(uploadFile);
            TR069Log.say("Shawn: src: file=[" + uploadFile + "]");
            TR069Log.say("Shawn: des: file=[" + file.getName() + "]");
            sftp.put(uploadFile, sftp.pwd() + directory);
            // sftp.chmod(777, "./" + file.getName());
            // SftpATTRS attr = sftp.stat("./" + file.getName());
            // TR069Log.say("permission=[" + attr.getPermissionsString() + "]");
            // sftp.get("./" + file.getName(), "/tmp/");
            // disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 下载文件
     * 
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     * @param sftp
     */
    public void download(String directory, String downloadFile, String saveFile) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            sftp.get(downloadFile, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * 
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sftp
     */
    public void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出目录下的文件
     * 
     * @param directory 要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory, ChannelSftp sftp) throws SftpException {
        return sftp.ls(directory);
    }

    public static boolean uploadeFile(String host,// FTP服务器hostname
            int port,// FTP服务器端口
            String username, // FTP登录账号
            String password, // FTP登录密码
            String directory, // FTP服务器保存目录
            String orginfilename) {// 输入流文件名
        SFTPAdapter sf = new SFTPAdapter();

        if (port <= 0) {
            port = sf.defPort;
        }

        sf.sftp = sf.connect(host, port, username, password);
        boolean status = sf.upload(directory, orginfilename);
        sf.disconnect();
        return status;
    }
}
