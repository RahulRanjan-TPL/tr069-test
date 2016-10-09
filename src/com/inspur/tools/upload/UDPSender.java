
package com.inspur.tools.upload;

import java.net.*;
import java.io.*;

import com.inspur.tools.TR069Log;

/**
 * UDPSender is an implementation of the Sender interface, using UDP as the transport protocol. The object is bound to a specified receiver host and port when created, and is able to send the contents of a file to this receiver.
 * 
 * @author Shawn Howell (haoxiong@inspur.com)
 */
public class UDPSender implements Sender {

    private DatagramSocket s;
    private int toPort;
    private InetAddress toAddress;

    /**
     * Class constructor. Creates a new UDPSender object capable of sending a file to the specified address and port.
     * 
     * @param address the address of the receiving host
     * @param port the listening port on the receiving host
     */
    public UDPSender(InetAddress address, int port) throws IOException {
        toPort = port;
        toAddress = address;
        s = new DatagramSocket();
        s.connect(toAddress, toPort);
    }

    /**
     * Sends a file to the bound host. Reads the contents of the specified file, and sends it via UDP to the host and port specified at when the object was created.
     * 
     * @param theFile the file to send
     */
    public void sendFile(File theFile, String head) throws IOException {
        // Init stuff
        int currentPos = 0, fileLength = 0, bytesRead = 0;
        FileInputStream fileReader = null;
        fileReader = new FileInputStream(theFile);
        fileLength = fileReader.available();
        // File file = new File("/tmp/capture/haha.txt");
        // file.createNewFile();
        // FileOutputStream fileOut = new FileOutputStream(file);
        if (fileLength <= 0) {
            fileReader.close();
            return;
        }

        TR069Log.say(" -- Filename: " + theFile.getName());
        TR069Log.say(" -- Bytes to send: " + fileLength);

        while (currentPos < fileLength) {
            byte[] msg = new byte[1024];
            try {
                bytesRead = fileReader.read(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                TR069Log.say(" -- Bytes read error");
                fileReader.close();
                s.close();
                return;
            }
            byte[] send = new byte[head.getBytes().length + bytesRead];
            System.arraycopy(head.getBytes(), 0, send, 0, head.getBytes().length);
            System.arraycopy(msg, 0, send, head.getBytes().length, bytesRead);
            // fileOut.write(send);
            send(send);

            TR069Log.say("Bytes read: " + bytesRead);
            currentPos = currentPos + bytesRead;
        }
        TR069Log.say("  -- File transfer complete...");
        fileReader.close();
        // fileOut.close();
        s.close();
    }

    private void send(byte[] message) throws IOException {
        DatagramPacket packet = new DatagramPacket(message, message.length);
        s.send(packet);
    }

//    private void close() {
//        if (s != null) {
//            s.close();
//        }
//    }
}
