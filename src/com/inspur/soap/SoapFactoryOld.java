
package com.inspur.soap;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.inspur.config.InspurData;
import com.inspur.tools.DebugInfo;
import com.inspur.tools.EventStructCode;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.ParameterAttributes;

public class SoapFactoryOld {

	 public static String createInformValueChangeXml(String list) {
		 ArrayList<String> keys = new ArrayList<String>(Arrays.asList(list));
		 return createInformValueChangeXml(keys);
	 }
    public static String createInformValueChangeXml(List<String> list) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!

            serializer.startTag("", "Manufacturer");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.text("4 VALUE CHANGE");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            //serializer.text(InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z"));
			serializer.text(InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss"));
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterValueStruct[18]");

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    String key = list.get(i);
                    ParameterHelp(serializer, key);
                }
            }

            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    // EventCode:1:boot 2:PERIODIC
    public static String createInformXml(int EventCode) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!

            serializer.startTag("", "Manufacturer");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            // serializer.attribute("", "xsi:type", "SOAP-ENC:Array");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");
            switch (EventCode) {
                case EventStructCode.BOOT:
                    serializer.startTag("", "EventCode");
                    serializer.text("1 BOOT");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.BOOTSTRAP:
                    serializer.startTag("", "EventCode");
                    serializer.text("0 BOOTSTRAP");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.PERIODIC:
                    serializer.startTag("", "EventCode");
                    serializer.text("2 PERIODIC");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.VALUECHANGE:
                    serializer.startTag("", "EventCode");
                    serializer.text("4 VALUE CHANGE");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                case EventStructCode.XSHUTDOWN:
                    serializer.startTag("", "EventCode");
                    serializer.text("X CUCC Shutdown");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.CONNECTIONREQUEST:
                    serializer.startTag("", "EventCode");
                    serializer.text("6 CONNECTION REQUEST");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.DIAGNOSTICSCOMPLETE:
                    serializer.startTag("", "EventCode");
                    serializer.text("8 DIAGNOSTICSCOMPLETE");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                case EventStructCode.XEVENTCODE:
                    serializer.startTag("", "EventCode");
                    serializer.text("X 00E0FC ErrorCode");// !!!!!!!!!!!!
                    serializer.endTag("", "EventCode");
                    break;
                default:
                    break;
            }

            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            //serializer.text(InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z"));
			serializer.text(InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss"));
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterValueStruct[24]");

            // huawei param set
            ParameterHelp(serializer, "Device.DeviceSummary");
            ParameterHelp(serializer, "Device.DeviceInfo.HardwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.SoftwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.AdditionalHardwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.AdditionalSoftwareVersion");
            // ParameterHelp(serializer, "Device.DeviceInfo.ModelID");
            ParameterHelp(serializer, "Device.DeviceInfo.ModelName");
            ParameterHelp(serializer, "Device.DeviceInfo.Description");
            ParameterHelp(serializer, "Device.DeviceInfo.FirstUseDate");
            ParameterHelp(serializer, "Device.DeviceInfo.UpTime");
            ParameterHelp(serializer, "Device.DeviceInfo.SerialNumber");
            // ParameterHelp(serializer, "Device.DeviceInfo.ProvisioningCode");

            ParameterHelp(serializer, "Device.ManagementServer.URL");
            // ParameterHelp(serializer, "Device.ManagementServer.URLBakup");
            ParameterHelp(serializer, "Device.ManagementServer.ParameterKey");
            ParameterHelp(serializer, "Device.ManagementServer.NATDetected");
            ParameterHelp(serializer, "Device.ManagementServer.UDPConnectionRequestAddress");
             ParameterHelp(serializer, "Device.ManagementServer.ConnectionRequestURL");
            ParameterHelp(serializer, "Device.ManagementServer.ConnectionRequestUsername");
             ParameterHelp(serializer, "Device.ManagementServer.ConnectionRequestPassword");
            // ParameterHelp(serializer, "Device.TIME.NTPServer");
            // ParameterHelp(serializer, "Device.LAN.DNSServers");
            // ParameterHelp(serializer, "Device.LAN.AddressingType");
            ParameterHelp(serializer, "Device.LAN.IPAddress");
            ParameterHelp(serializer, "Device.LAN.MACAddress");

            ParameterHelp(serializer, "Device.X_00E0FC.STBID");
            ParameterHelp(serializer, "Device.X_00E0FC.ServiceInfo.UserID");
            ParameterHelp(serializer, "Device.X_00E0FC.ServiceInfo.AuthURL");
            ParameterHelp(serializer, "Device.X_00E0FC.ServiceInfo.PPPoEID"); // adasdf
            ParameterHelp(serializer, "Device.X_00E0FC.IsEncryptMark");

            // ParameterHelp(serializer, "Device.X_CM_OTV.STBInfo.STBID");
            // ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.UserID");
            // ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.PPPoEID");
            // ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.AuthURL");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.STBInfo.STBID");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.UserID");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.Password");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.PPPoEID");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.AuthURL");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.PlatformURL");
            // ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.PlatformBackupURL");

            // ParameterHelp(serializer, "Device.DeviceInfo.SerialNumber");
            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        TR069Log.say("lilililililil:" + writer.toString());
        return writer.toString();
    }

    public static String createReponseXml() {
        return "";
    }

    private static void ParameterHelp(XmlSerializer serializer, String key) throws IOException {
        serializer.startTag("", "ParameterValueStruct");// ParameterValueStruct1
        serializer.startTag("", "Name");
        serializer.text(key);
        serializer.endTag("", "Name");
        serializer.startTag("", "Value");
        serializer.attribute("", "xsi:type", "xsd:string");
        if (InspurData.getInspurValue(key) != null) {
            serializer.text(InspurData.getInspurValue(key));
        } else {
            serializer.text("null");
        }
        serializer.endTag("", "Value");
        serializer.endTag("", "ParameterValueStruct");
    }

    public static String createGetParameterValuesResponseXml(ArrayList<String> list, String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");
            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:GetParameterValuesResponse");
            // deviceId
            serializer.startTag("", "ParameterList");
            serializer.attribute("", "xsi:type", "SOAP-ENC:Array");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterValueStruct[" + list.size() + "]");
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    serializer.startTag("", "ParameterValueStruct");
                    serializer.startTag("", "Name");
                    serializer.text(list.get(i));
                    serializer.endTag("", "Name");
                    serializer.startTag("", "Value");
                    if (InspurData.getInspurValue(list.get(i)) != null) {
                        serializer.text(InspurData.getInspurValue(list.get(i)));
                    } else {
                        serializer.text("null");
                    }
                    serializer.endTag("", "Value");
                    serializer.endTag("", "ParameterValueStruct");
                }
            }
            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:GetParameterValuesResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createDownloadResponseXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head

            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");
            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:DownloadResponse");
            serializer.startTag("", "Status");
            serializer.attribute("", "xsi:type", "xsd:int");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "Status");
            serializer.endTag("", "cwmp:DownloadResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createSetParameterValuesResponseXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:SetParameterValuesResponse");

            serializer.startTag("", "Status");
            serializer.attribute("", "xsi:type", "xsd:int");
            serializer.text("0");
            serializer.endTag("", "Status");

            serializer.endTag("", "cwmp:SetParameterValuesResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createUpgradeInformXml(String CommandKey) {

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!

            serializer.startTag("", "Manufacturer");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.text("1 BOOT");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "EventCode");
            serializer.text("M TRANSFERCOMPLETE");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "EventCode");
            serializer.text("4 VALUE CHANGE");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "CommandKey");
            serializer.text(CommandKey);
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            //serializer.text(InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z"));
			serializer.text(InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss"));
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterValueStruct[18]");
            // huawei param set
            ParameterHelp(serializer, "Device.DeviceInfo.HardwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.SoftwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.AdditionalHardwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.AdditionalSoftwareVersion");
            ParameterHelp(serializer, "Device.DeviceInfo.ModelName");
            ParameterHelp(serializer, "Device.DeviceInfo.Description");
            ParameterHelp(serializer, "Device.DeviceInfo.FirstUseDate");
            ParameterHelp(serializer, "Device.DeviceInfo.UpTime");
             ParameterHelp(serializer, "Device.ManagementServer.ConnectionRequestURL");
            ParameterHelp(serializer, "Device.TIME.NTPServer");
            ParameterHelp(serializer, "Device.LAN.DNSServers");
            ParameterHelp(serializer, "Device.LAN.AddressingType");
            ParameterHelp(serializer, "Device.LAN.IPAddress");
            ParameterHelp(serializer, "Device.LAN.MACAddress");
            ParameterHelp(serializer, "Device.ManagementServer.NATDetected");
            ParameterHelp(serializer, "Device.ManagementServer.UDPConnectionRequestAddress");
            ParameterHelp(serializer, "Device.X_CM_OTV.STBInfo.STBID");
            ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.UserID");
            ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.PPPoEID");
            ParameterHelp(serializer, "Device.X_CM_OTV.ServiceInfo.AuthURL");
            ParameterHelp(serializer, "Device.X_CMCC_OTV.STBInfo.STBID");
            ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.UserID");
            ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.PPPoEID");
            ParameterHelp(serializer, "Device.X_CMCC_OTV.ServiceInfo.AuthURL");
            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();

    }

    public static String createTransferCompleteXml(String CommandKey, String FaultCode) {

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");

            serializer.startTag("", "cwmp:TransferComplete");
            serializer.startTag("", "CommandKey");
            serializer.text(CommandKey);
            serializer.endTag("", "CommandKey");
            serializer.startTag("", "FaultStruct");

            serializer.startTag("", "FaultCode");
            serializer.text(FaultCode);
            serializer.endTag("", "FaultCode");
            if (FaultCode.equals("0")) {
                serializer.startTag("", "FaultString");
                serializer.endTag("", "FaultString");
            } else {
                serializer.startTag("", "FaultString");
                serializer.text("inspur say upgrade fail!");
                serializer.endTag("", "FaultString");
            }

            serializer.endTag("", "FaultStruct");

            serializer.startTag("", "StartTime");
            serializer.text("undefine");
            serializer.endTag("", "StartTime");
            serializer.startTag("", "CompleteTime");
            serializer.text("undefine");
            serializer.endTag("", "CompleteTime");

            serializer.endTag("", "cwmp:TransferComplete");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();

    }

    public static String createRebootResponseXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");

            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:RebootResponse");
            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "cwmp:RebootResponse");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createRebootInformXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");

            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform

            serializer.startTag("", "Event");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.text("M Reboot");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.endTag("", "EventStruct");
            serializer.endTag("", "Event");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createFactoryResetResponseXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");

            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:FactoryResetResponse");
            serializer.endTag("", "cwmp:FactoryResetResponse");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createFactoryResetInformXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");

            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform

            serializer.startTag("", "Event");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.text("0 BOOTSTRAP");
            serializer.endTag("", "EventCode");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    // 8 DIAGNOSTICS COMPLETE
    public static String createPingInformXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap:Envelope");
            serializer.attribute("", "xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap:mustUnderstand", "1");

            serializer.text(id);

            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap:Header");

            // body
            serializer.startTag("", "soap:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!
            serializer.attribute("", "xsi:type", "cwmp:DeviceIdStruct");

            serializer.startTag("", "Manufacturer");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.attribute("", "xsi:name", "");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.attribute("", "xsi:type", "xsd:string(6)");
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            serializer.attribute("", "soap:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text("8 DIAGNOSTICS COMPLETE");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            serializer.attribute("", "xsi:type", "xsd:dateTime");
            //String result = InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z");
			String result = InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss");
            serializer.text(result);
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap:arrayType", "cwmp:ParameterValueStruct[1]");

            serializer.startTag("", "ParameterValueStruct");// ParameterValueStruct1
            serializer.startTag("", "Name");
            serializer.attribute("", "xsi:type", "xsd:string(256)");
            serializer.text("Device.X_CMCC_OTV.STBInfo.STBID");
            serializer.endTag("", "Name");
            serializer.startTag("", "Value");
            serializer.attribute("", "xsi:type", "xsd:string(32)");
            if (InspurData.getInspurValue("Device.X_CMCC_OTV.STBInfo.STBID") != null) {
                serializer.text(InspurData.getInspurValue("Device.X_CMCC_OTV.STBInfo.STBID"));
            } else {
                serializer.text("null");
            }
            serializer.endTag("", "Value");
            serializer.endTag("", "ParameterValueStruct");

            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap:Body");
            serializer.endTag("", "soap:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    // 8 DIAGNOSTICS COMPLETE
    public static String createTraceRouteInformXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap:Envelope");
            serializer.attribute("", "xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap:mustUnderstand", "1");
            serializer.text(id);

            // if (!id.equalsIgnoreCase("1")) {
            // TR069Log.say("####EC: id != 1, it's someproblem here?!");
            // }

            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap:Header");

            // body
            serializer.startTag("", "soap:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!
            serializer.attribute("", "xsi:type", "cwmp:DeviceIdStruct");

            serializer.startTag("", "Manufacturer");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.attribute("", "xsi:name", "");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.attribute("", "xsi:type", "xsd:string(6)");
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            serializer.attribute("", "soap:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text("8 DIAGNOSTICS COMPLETE");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            serializer.attribute("", "xsi:type", "xsd:dateTime");
            //String result = InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z");
			String result = InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss");
            serializer.text(result);
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap:arrayType", "cwmp:ParameterValueStruct[1]");

            serializer.startTag("", "ParameterValueStruct");// ParameterValueStruct1
            serializer.startTag("", "Name");
            serializer.attribute("", "xsi:type", "xsd:string(256)");
            serializer.text("Device.X_CMCC_OTV.STBInfo.STBID");
            serializer.endTag("", "Name");
            serializer.startTag("", "Value");
            serializer.attribute("", "xsi:type", "xsd:string(32)");
            if (InspurData.getInspurValue("Device.X_CMCC_OTV.STBInfo.STBID") != null) {
                serializer.text(InspurData.getInspurValue("Device.X_CMCC_OTV.STBInfo.STBID"));
            } else {
                serializer.text("null");
            }
            serializer.endTag("", "Value");
            serializer.endTag("", "ParameterValueStruct");

            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap:Body");
            serializer.endTag("", "soap:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    // X CM ErrorCode
    public static String createErrorCodeInformXml() {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap:Envelope");
            serializer.attribute("", "xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:encodingStyle", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap:mustUnderstand", "1");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap:Header");

            // body
            serializer.startTag("", "soap:Body");
            // inform
            serializer.startTag("", "cwmp:Inform");
            // deviceId
            serializer.startTag("", "DeviceId");// !!!!!!!!!!!!!!!!
            serializer.attribute("", "xsi:type", "cwmp:DeviceIdStruct");

            serializer.startTag("", "Manufacturer");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.attribute("", "xsi:name", "");
            serializer.text(InspurData.Manufacturer);
            serializer.endTag("", "Manufacturer");

            serializer.startTag("", "OUI");// OUI
            serializer.attribute("", "xsi:type", "xsd:string(6)");
            serializer.text(InspurData.OUI);
            serializer.endTag("", "OUI");

            serializer.startTag("", "ProductClass");// ProductClass
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.ProductClass);
            serializer.endTag("", "ProductClass");

            serializer.startTag("", "SerialNumber");// SerialNumber
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text(InspurData.SerialNumber);
            serializer.endTag("", "SerialNumber");
            serializer.endTag("", "DeviceId");

            // Envent
            serializer.startTag("", "Event");
            serializer.attribute("", "soap:arrayType", "cwmp:EventStruct[1]");

            serializer.startTag("", "EventStruct");

            serializer.startTag("", "EventCode");
            serializer.attribute("", "xsi:type", "xsd:string(64)");
            serializer.text("X 00E0FC ErrorCode");// !!!!!!!!!!!!
            serializer.endTag("", "EventCode");

            serializer.startTag("", "CommandKey");
            serializer.endTag("", "CommandKey");
            serializer.endTag("", "EventStruct");

            serializer.endTag("", "Event");

            serializer.startTag("", "MaxEnvelopes");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text(String.valueOf(new Random().nextInt()));
            serializer.endTag("", "MaxEnvelopes");
            serializer.startTag("", "CurrentTime");
            serializer.attribute("", "xsi:type", "xsd:dateTime");
            //String result = InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z");
			String result = InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss");
            serializer.text(result);
            serializer.endTag("", "CurrentTime");
            serializer.startTag("", "RetryCount");
            serializer.attribute("", "xsi:type", "xsd:unsigendInt");
            serializer.text("0");
            serializer.endTag("", "RetryCount");

            serializer.startTag("", "ParameterList");
            int num = GlobalContext.allErrors.size();

            serializer.attribute("", "soap:arrayType", "cwmp:ParameterValueStruct[" + num + "]");
            for (int i = 0; i < num; i++) {
                serializer.startTag("", "ParameterValueStruct");// ParameterValueStruct1
                serializer.startTag("", "Name");
                serializer.attribute("", "xsi:type", "xsd:string(256)");
                serializer.text("Device.X_00E0FC.ErrorCode." + i + ".ErrorCodeTime");
                serializer.endTag("", "Name");
                serializer.startTag("", "Value");
                serializer.attribute("", "xsi:type", "xsd:string(32)");
                serializer.text(GlobalContext.allErrors.get(i).getErrorCodeTime());
                serializer.endTag("", "Value");
                serializer.endTag("", "ParameterValueStruct");

                serializer.startTag("", "ParameterValueStruct");// ParameterValueStruct1
                serializer.startTag("", "Name");
                serializer.attribute("", "xsi:type", "xsd:string(256)");
                serializer.text("Device.X_00E0FC.ErrorCode." + i + ".ErrorCodeValue");
                serializer.endTag("", "Name");
                serializer.startTag("", "Value");
                serializer.attribute("", "xsi:type", "xsd:string(32)");
                serializer.text(GlobalContext.allErrors.get(i).getErrorCodeValue());
                serializer.endTag("", "Value");
                serializer.endTag("", "ParameterValueStruct");
            }
            // 已经上报了 清空它。
            GlobalContext.allErrors.clear();

            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:Inform");
            serializer.endTag("", "soap:Body");
            serializer.endTag("", "soap:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createGetRPCMethodsInformXml(String id) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");

            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:GetRPCMethodsResponse");
            serializer.startTag("", "MethodList");
            serializer.attribute("", "soap-enc:arrayType", "string[4]");

            serializer.startTag("", "string");
            serializer.text("GetRPCMethods");
            serializer.endTag("", "string");

            serializer.startTag("", "string");
            serializer.text("Inform");
            serializer.endTag("", "string");

            serializer.startTag("", "string");
            serializer.text("TransferComplete");
            serializer.endTag("", "string");

            serializer.startTag("", "string");
            serializer.text("AutonomousTransferComplete");
            serializer.endTag("", "string");

            serializer.endTag("", "MethodList");
            serializer.endTag("", "cwmp:GetRPCMethodsResponse");

            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();
    }

    public static String createGetParameterNamesResponseXml(String id, String parameterPath, String nextLevel) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");
            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:GetParameterNamesResponse");
            // deviceId
            List<String> list = Tr069DataFile.getAllKey();
            serializer.startTag("", "ParameterList");
            serializer.attribute("", "xsi:type", "SOAP-ENC:Array");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterInfoStruct[" + list.size() + "]");
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    serializer.startTag("", "ParameterInfoStruct");
                    serializer.startTag("", "Name");
                    serializer.text(list.get(i));
                    serializer.endTag("", "Name");
                    serializer.startTag("", "Writable");
                    serializer.text("true");
                    serializer.endTag("", "Writable");
                    serializer.endTag("", "ParameterInfoStruct");
                }
            }
            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:GetParameterNamesResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();

    }

    public static String createGetParameterAttributesResponse(String id, ArrayList<String> params) {
        // TODO Auto-generated method stub
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");
            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:GetParameterAttributesResponse");
            // deviceId
            serializer.startTag("", "ParameterList");
            serializer.attribute("", "soap-enc:arrayType", "cwmp:ParameterAttributeStruct[" + params.size() + "]");
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    serializer.startTag("", "ParameterAttributeStruct");
                    serializer.startTag("", "Name");
                    serializer.text(params.get(i));
                    serializer.endTag("", "Name");
                    serializer.startTag("", "Notification");
                    serializer.text("2");
                    serializer.endTag("", "Notification");
                    serializer.startTag("", "AccessList");
                    serializer.text("Subscriber");
                    serializer.endTag("", "AccessList");
                    serializer.endTag("", "ParameterAttributeStruct");
                }
            }
            serializer.endTag("", "ParameterList");
            serializer.endTag("", "cwmp:GetParameterAttributesResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();

    }

    public static String createSetParameterAttributesResponseXml(String id, ArrayList<ParameterAttributes> params) {

        // TODO Auto-generated method stub
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, null);
            serializer.startTag("", "soap-env:Envelope");
            serializer.attribute("", "xmlns:soap-env", "http://schemas.xmlsoap.org/soap/envelope/");
            serializer.attribute("", "xmlns:soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            serializer.attribute("", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.attribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.attribute("", "xmlns:cwmp", "urn:dslforum-org:cwmp-1-0");

            // head
            serializer.startTag("", "soap-env:Header");
            serializer.startTag("", "cwmp:ID");
            serializer.attribute("", "soap-env:mustUnderstand", "1");
            serializer.text(id);
            serializer.endTag("", "cwmp:ID");
            serializer.endTag("", "soap-env:Header");
            // body
            serializer.startTag("", "soap-env:Body");
            // inform
            serializer.startTag("", "cwmp:SetParameterAttributesResponse");
            serializer.endTag("", "cwmp:SetParameterAttributesResponse");
            serializer.endTag("", "soap-env:Body");
            serializer.endTag("", "soap-env:Envelope");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DebugInfo.DEBUG) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            TR069Log.say(DebugInfo.getDebug().parseXML(writer.toString()));
        }
        return writer.toString();

    }
}
