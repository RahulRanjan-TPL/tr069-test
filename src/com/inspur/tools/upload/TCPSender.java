
package com.inspur.tools.upload;

import java.net.*;
import java.io.*;

import com.inspur.tools.TR069Log;

/**
 * UDPSender is an implementation of the Sender interface, using UDP as the transport protocol. The object is bound to a specified receiver host and port when created, and is able to send the contents of a file to this receiver.
 * 
 * @author Shawn Howell (haoxiong@inspur.com)
 */
public class TCPSender implements Sender {
    private File theFile;
    private FileInputStream fileReader;
    private Socket s;
    private int fileLength, currentPos, bytesRead, toPort, length;
    private byte[] msg, buffer;
    private String toHost, initReply;
    private InetAddress toAddress;
    private OutputStream theOutstream;
    private InputStream theInstream;

    /**
     * Class constructor. Creates a new UDPSender object capable of sending a file to the specified address and port.
     * 
     * @param address the address of the receiving host
     * @param port the listening port on the receiving host
     */
    public TCPSender(InetAddress address, int port) throws IOException {
        toPort = port;
        toAddress = address;
        msg = new byte[8192];
        buffer = new byte[8192];
        s = new Socket(toAddress, toPort);
        theOutstream = s.getOutputStream();
        theInstream = s.getInputStream();
    }

    /**
     * Sends a file to the bound host. Reads the contents of the specified file, and sends it via UDP to the host and port specified at when the object was created.
     * 
     * @param theFile the file to send
     */
    public void sendFile(File theFile, String head) throws IOException {
        // Init stuff
        fileReader = new FileInputStream(theFile);
        fileLength = fileReader.available();

        TR069Log.say(" -- Filename: " + theFile.getName());
        TR069Log.say(" -- Bytes to send: " + fileLength);

        // 1. Send the filename and length to the receiver
        theOutstream.write((theFile.getName() + "::" + fileLength).getBytes());
        theOutstream.flush();

        // 2. Wait for a reply from the receiver
        TR069Log.say(" -- Waiting for OK from the receiver");
        length = 0;
        while (length <= 0) {
            length = theInstream.read(buffer);
            if (length > 0)
                initReply = (new String(buffer, 0, length));
        }

        // 3. Send the content of the file
        if (initReply.equals("OK")) {
            TR069Log.say("  -- Got OK from receiver - sending the file ");

            while (currentPos < fileLength) {
                // TR069Log.say("Will read at pos: "+currentPos);
                bytesRead = fileReader.read(msg);
                theOutstream.write(msg);
                // TR069Log.say("Bytes read: "+bytesRead);
                currentPos = currentPos + bytesRead;
            }

            TR069Log.say("  -- File transfer complete...");
        } else {
            TR069Log.say("  -- Recieved something other than OK... exiting");
        }
    }
}
