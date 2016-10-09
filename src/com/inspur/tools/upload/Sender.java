
package com.inspur.tools.upload;

import java.io.*;

/**
 * Interface for a basic network file sender.
 * 
 * @author Shawn Howell (haoxiong@inspur.com)
 */
public interface Sender {

    /**
     * Sends a file to a bound host. Reads the contents of the specified file, and sends it to the host and port specified elswhere.
     * 
     * @param theFile the file to send
     */
    public void sendFile(File theFile, String head) throws IOException;

}
