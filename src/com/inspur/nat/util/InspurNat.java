
package com.inspur.nat.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.inspur.config.InspurData;
import com.inspur.nat.attribute.BindingChange;
import com.inspur.nat.attribute.ConnectionRequestBinding;
import com.inspur.nat.attribute.ChangedAddress;
import com.inspur.nat.attribute.ErrorCode;
import com.inspur.nat.attribute.MappedAddress;
import com.inspur.nat.attribute.MessageAttribute;
import com.inspur.nat.attribute.Password;
import com.inspur.nat.attribute.Username;
import com.inspur.nat.header.MessageHeader;
import com.inspur.tools.TR069Log;

public class InspurNat {
    public static int defaultPort = 57690;

    public static MessageHeader sendBinding(String stunServer, int stunPort, byte[] transactionID) {
        DatagramSocket localSocket = null;
        int timeout = 600;
        try {
            // Test 1 including response
            localSocket = new DatagramSocket(null);
            localSocket.setReuseAddress(true);
            localSocket.bind(new InetSocketAddress(InspurNat.defaultPort));

            localSocket.connect(InetAddress.getByName(stunServer), stunPort);
            localSocket.setSoTimeout(timeout);

            MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
            // sendMH.generateTransactionID();
            sendMH.setTransactionID(transactionID);

            // ChangeRequest changeRequest = new ChangeRequest();
            // sendMH.addMessageAttribute(changeRequest);

            if (InspurData.getInspurValue("Device.ManagementServer.STUNUsername") != null) {
                Username username = new Username();
                username.setUsername(InspurData.getInspurValue("Device.ManagementServer.STUNUsername"));
                sendMH.addMessageAttribute(username);
            } else {
                TR069Log.say("nnnnnnnnnnn error get stun username,do not send to stun server");
            }

            if (InspurData.getInspurValue("Device.ManagementServer.STUNPassword") != null) {
                Password password = new Password();
                password.setPassword(InspurData.getInspurValue("Device.ManagementServer.STUNPassword"));
                sendMH.addMessageAttribute(password);
            } else {
                TR069Log.say("nnnnnnnnnnn error get stun password,do not send to stun server");
            }

            ConnectionRequestBinding changerequest = new ConnectionRequestBinding();
            sendMH.addMessageAttribute(changerequest);

            BindingChange binderchangeRequest = new BindingChange();
            sendMH.addMessageAttribute(binderchangeRequest);

            // getByte return 0,useless!
            // MessageIntegrity messageinte = new MessageIntegrity();
            // sendMH.addMessageAttribute(messageinte);

            byte[] data = sendMH.getBytes();
            TR069Log.say("nnnnnnnnnnn ####EC: send data");
            DatagramPacket send = new DatagramPacket(data, data.length);
            localSocket.send(send);

            return sendMH;
        } catch (Exception ste) {
            TR069Log.say("nnnnnnnnnnn attention! sendBinding error!!! ");
            ste.printStackTrace();
            return null;
        } finally {
            localSocket.disconnect();
            localSocket.close();
        }
    }

    public static String getPublicUrlFromUdpPacket(DatagramPacket receive) {
        try {
            MappedAddress ma;
            ChangedAddress ca;
            MessageHeader receiveMH = new MessageHeader();

            receiveMH = MessageHeader.parseHeader(receive.getData());

            TR069Log.say("nnnnnnnnnnn ####EC:udpReceive get data");
            receiveMH.parseAttributes(receive.getData());

            ma = (MappedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.MappedAddress);
            ca = (ChangedAddress) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ChangedAddress);
            ErrorCode ec = (ErrorCode) receiveMH.getMessageAttribute(MessageAttribute.MessageAttributeType.ErrorCode);

            if (ec != null || (ma == null) || (ca == null)) {
                return "";
            } else {
                return ma.getAddress().getInetAddress().getHostAddress() + ":" + ma.getPort();
            }
        } catch (Exception e) {
            TR069Log.say("nnnnnnnnnnn attention! udpReceive error!!! ");
            e.printStackTrace();
            return "";
        }
    }

}
