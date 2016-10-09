
package com.inspur.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FileClient {
    private DatagramSocket ds = null;
    private String host = null;
    String filePath = null;
    private int port = -1;
    String head = null;

    public FileClient(String head, String host, int port, String filePath) {
        this.host = host;
        this.port = port;
        this.head = head;
        this.filePath = filePath;
        try {
            ds = new DatagramSocket();
            ds.connect(InetAddress.getByName(host), port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 传输文件
     * 
     * @param filePath 文件所在路径
     */
    public void TransmitFile(byte[] data) {
        try {
            // 封装数据报
            DatagramPacket packet = new DatagramPacket(data, (int) data.length);
            // 开始传输文件
            ds.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TR069Log.say("Send one udp packet");
        }
    }

    /**
     * 读取文件
     * 
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public boolean readFile() throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        InputStream in = new FileInputStream(file);
        // byte[] send = new byte[65500];
        byte[] data = new byte[65000];
        int len = 0;
        while ((len = in.read(data)) != -1) {
            TransmitFile(data);
        }
        TR069Log.say("read over !!!");
        in.close();
        ds.close();
        return true;
    }
}
