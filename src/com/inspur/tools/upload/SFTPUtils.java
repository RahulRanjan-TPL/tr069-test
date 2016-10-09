
package com.inspur.tools.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.inspur.tools.TR069Log;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtils {

    private String host = "127.0.0.1";
    private String username = "MingMac";
    private String password = "×××";
    private int port = 22;
    private ChannelSftp sftp = null;
    private String localPath = "/var/test";
    private String remotePath = "/var/tset";
    private String fileListPath = "/var/test/java/test.txt";
    private final String seperator = "/";

    /**
     * connect server via sftp
     */
    public void connect() {
        try {
            if (sftp != null) {
                TR069Log.say("sftp is not null");
            }
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
            e.printStackTrace();
        }
    }

    /**
     * Disconnect with server
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
            } else if (this.sftp.isClosed()) {
                TR069Log.say("sftp is closed already");
            }
        }

    }

    public void download() {
        // TODO Auto-generated method stub

    }

    private void download(String directory, String downloadFile, String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File file = new File(saveFile);
            sftp.get(downloadFile, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * upload all the files to the server
     */
    public void upload() {
        List<String> fileList = this.getFileEntryList(fileListPath);
        try {
            if (fileList != null) {
                for (String filepath : fileList) {
                    String localFile = this.localPath + this.seperator + filepath;
                    File file = new File(localFile);

                    if (file.isFile()) {
                        TR069Log.say("localFile : " + file.getAbsolutePath());
                        String remoteFile = this.remotePath + this.seperator + filepath;
                        TR069Log.say("remotePath:" + remoteFile);
                        File rfile = new File(remoteFile);
                        String rpath = rfile.getParent();
                        try {
                            createDir(rpath, sftp);
                        } catch (Exception e) {
                            TR069Log.say("*******create path failed" + rpath);
                        }

                        this.sftp.put(new FileInputStream(file), file.getName());
                        TR069Log.say("=========upload down for " + localFile);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SftpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * create Directory
     * 
     * @param filepath
     * @param sftp
     */
    private void createDir(String filepath, ChannelSftp sftp) {
        boolean bcreated = false;
        boolean bparent = false;
        File file = new File(filepath);
        String ppath = file.getParent();
        try {
            this.sftp.cd(ppath);
            bparent = true;
        } catch (SftpException e1) {
            bparent = false;
        }
        try {
            if (bparent) {
                try {
                    this.sftp.cd(filepath);
                    bcreated = true;
                } catch (Exception e) {
                    bcreated = false;
                }
                if (!bcreated) {
                    this.sftp.mkdir(filepath);
                    bcreated = true;
                }
                return;
            } else {
                createDir(ppath, sftp);
                this.sftp.cd(ppath);
                this.sftp.mkdir(filepath);
            }
        } catch (SftpException e) {
            TR069Log.say("mkdir failed :" + filepath);
            e.printStackTrace();
        }

        try {
            this.sftp.cd(filepath);
        } catch (SftpException e) {
            e.printStackTrace();
            TR069Log.say("can not cd into :" + filepath);
        }

    }

    /**
     * get all the files need to be upload or download
     * 
     * @param file
     * @return
     */
    private List<String> getFileEntryList(String file) {
        ArrayList<String> fileList = new ArrayList<String>();
        InputStream in = null;
        try {

            in = new FileInputStream(file);
            InputStreamReader inreader = new InputStreamReader(in);

            LineNumberReader linreader = new LineNumberReader(inreader);
            String filepath = linreader.readLine();
            while (filepath != null) {
                fileList.add(filepath);
                filepath = linreader.readLine();
            }
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                in = null;
            }
        }

        return fileList;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the sftp
     */
    public ChannelSftp getSftp() {
        return sftp;
    }

    /**
     * @param sftp the sftp to set
     */
    public void setSftp(ChannelSftp sftp) {
        this.sftp = sftp;
    }

    /**
     * @return the localPath
     */
    public String getLocalPath() {
        return localPath;
    }

    /**
     * @param localPath the localPath to set
     */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    /**
     * @return the remotePath
     */
    public String getRemotePath() {
        return remotePath;
    }

    /**
     * @param remotePath the remotePath to set
     */
    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    /**
     * @return the fileListPath
     */
    public String getFileListPath() {
        return fileListPath;
    }

    /**
     * @param fileListPath the fileListPath to set
     */
    public void setFileListPath(String fileListPath) {
        this.fileListPath = fileListPath;
    }

    public static void main22(String[] args) {
        // TODO Auto-generated method stub
        SFTPUtils ftp = new SFTPUtils();
        ftp.connect();
        ftp.upload();
        ftp.disconnect();
        System.exit(0);
    }

}
