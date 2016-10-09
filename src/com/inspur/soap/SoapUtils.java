package com.inspur.soap;

import java.util.ArrayList;
import java.util.Arrays;

import com.inspur.tools.EventStructCode;
import com.inspur.work.TR069Utils;

public class SoapUtils {
	
	final public static String ENV_NAMESPACE = "";
	final public static String ENV_UNDEFINE = "undefine";
	
	//start 
	final public static String ENV_ENVELOPE = "soap-env:Envelope";
	final public static String XML_SOAP_ENV = "xmlns:soap-env";
	final public static String XML_SOAP_ENV_VALUE = "http://schemas.xmlsoap.org/soap/envelope/";
	final public static String XML_SOAP_ENC = "xmlns:soap-enc";
	final public static String XML_SOAP_ENC_VALUE = "http://schemas.xmlsoap.org/soap/encoding/";
	final public static String XML_XSD = "xmlns:xsd";
	final public static String XML_XSD_VALUE = "http://www.w3.org/2001/XMLSchema";
	final public static String XML_XSI = "xmlns:xsi";
	final public static String XML_XSI_VALUE = "http://www.w3.org/2001/XMLSchema-instance";
	final public static String XML_CWMP = "xmlns:cwmp";
	final public static String XML_CWMP_VALUE = "urn:dslforum-org:cwmp-1-0";
	
	//header
	final public static String ENV_HEADER = "soap-env:Header";
	final public static String CWMP_ID = "cwmp:ID";
	final public static String ENV_MUSTUNDERSTAND = "soap-env:mustUnderstand";
	
	//body
	final public static String ENV_BODY = "soap-env:Body";
		//inform
		final public static String CWMP_INFORM = "cwmp:Inform";
		final public static String CWMP_GET_PARAMETERVALUES_RESPONSE = "cwmp:GetParameterValuesResponse";
		final public static String CWMP_SET_PARAMETERVALUES_RESPONSE = "cwmp:SetParameterValuesResponse";
		final public static String CWMP_DOWNLOAD_RESPONSE = "cwmp:DownloadResponse";
		final public static String CWMP_TRANSFER_COMPLETE = "cwmp:TransferComplete";
		final public static String CWMP_REBOOT_RESPONSE = "cwmp:RebootResponse";
		final public static String CWMP_FACTORYRESET_RESPONSE = "cwmp:FactoryResetResponse";
		final public static String CWMP_GET_RPC_METHODS_RESPONSE = "cwmp:GetRPCMethodsResponse";
		final public static String CWMP_GET_PARAMETERNAMES_RESPONSE = "cwmp:GetParameterNamesResponse";
		final public static String CWMP_GET_PARAMETERATTR_RESPONSE = "cwmp:GetParameterAttributesResponse";
		final public static String CWMP_SET_PARAMETERATTR_RESPONSE = "cwmp:SetParameterAttributesResponse";
		final public static String ENV_STATUS = "Status";
			//device id
			final public static String DEVICE_ID = "DeviceId";
			final public static String MANUFACTURER_ID = "Manufacturer";
			final public static String OUI_ID = "OUI";
			final public static String PRODUCT_CLASS_ID = "ProductClass";
			final public static String SERIAL_NUMBER = "SerialNumber";

	//Event
	final public static String EVENT = "Event";
	final public static String ENC_ARRAYTYPE = "soap-enc:arrayType";
	final public static String ENC_ARRAYTYPE_EVENT_STRUCT = "cwmp:EventStruct[%d]";
	final public static String EVENT_STRUCT = "EventStruct";
	final public static String EVENT_CODE = "EventCode";
	final public static String COMMAND_KEY = "CommandKey";
	final public static String MAX_ENVELOPES = "MaxEnvelopes";
	final public static String CURRENT_TIME = "CurrentTime";
	final public static String RETRY_COUNT = "RetryCount";
	final public static String PARAMETER_LIST = "ParameterList";
	final public static String ENC_ARRAYTYPE_PARAMETER = "cwmp:ParameterValueStruct[%d]";
	final public static String ENC_ARRAYTYPE_INFO_PARAMETER = "cwmp:ParameterInfoStruct[%d]";
	final public static String ENC_ARRAYTYPE_ATTR_PARAMETER = "cwmp:ParameterAttributeStruct[%d]";
	final public static String PARAMETER_STRUCT = "ParameterValueStruct";
	final public static String PARAMETER_INFO_STRUCT = "ParameterInfoStruct";
	final public static String PARAMETER_ATTR_STRUCT = "ParameterAttributeStruct";
	final public static String PARAMETER_NAME = "Name";
	final public static String PARAMETER_VALUE = "Value";
	final public static String XML_XSI_TYPE_KEY = "xsi:type";
	final public static String XML_XSD_TYPE_STRING = "xsd:string";
	final public static String XML_XSD_TYPE_INT = "xsd:int";
	final public static String FAULT_STRUCT = "FaultStruct";
	final public static String FAULT_CODE = "FaultCode";
	final public static String FAULT_STRING = "FaultString";
	final public static String ENV_START_TIME = "StartTime"; 
	final public static String ENV_COMPLETE_TIME = "CompleteTime"; 
	final public static String ENV_METHOD_LIST = "MethodList"; 
	final public static String ENV_STRING = "string"; 
	final public static String PARAMETER_WRITEABLE = "Writable"; 
	final public static String PARAMETER_NOTIFICATION = "Notification"; 
	final public static String PARAMETER_ACCESSLIST = "AccessList"; 
	
	
	
	final public static String BOOT_EVENT = "1 BOOT";
	final public static String BOOTSTART_EVENT = "0 BOOTSTRAP";
	final public static String PERIODIC_EVENT = "2 PERIODIC";
	final public static String VALUE_CHANGE_EVENT = "4 VALUE CHANGE";
	final public static String SHUTDOWN_EVENT = "X CUCC Shutdown";
	final public static String CONNECTION_REQUEST_EVENT = "6 CONNECTION REQUEST";
	final public static String DIAGNOSTICSCOMPLETE_EVENT = "8 DIAGNOSTICS COMPLETE";
	final public static String O0E0FC_ERRORCODE_EVENT = "X 00E0FC ErrorCode";
	final public static String TRANSFER_COMPLETE_EVENT = "M TRANSFERCOMPLETE";
	final public static String REBOOT_EVENT = "M Reboot";
	
	final public static ArrayList<String> SOAP_INFO = new ArrayList<String>(Arrays.asList(
			TR069Utils.Device.DeviceSummary,
			TR069Utils.Device.DeviceInfo.SoftwareVersion, TR069Utils.Device.DeviceInfo.HardwareVersion,
			TR069Utils.Device.DeviceInfo.AdditionalHardwareVersion, TR069Utils.Device.DeviceInfo.AdditionalSoftwareVersion,
			TR069Utils.Device.DeviceInfo.ModelName, TR069Utils.Device.DeviceInfo.Description,
			TR069Utils.Device.DeviceInfo.FirstUseDate, TR069Utils.Device.DeviceInfo.UpTime,
			TR069Utils.Device.DeviceInfo.SerialNumber, TR069Utils.Device.ManagementServer.URL,
			TR069Utils.Device.ManagementServer.ParameterKey, TR069Utils.Device.ManagementServer.NATDetected,
			TR069Utils.Device.ManagementServer.UDPConnectionRequestAddress, TR069Utils.Device.ManagementServer.ConnectionRequestURL,
			TR069Utils.Device.ManagementServer.ConnectionRequestUsername, TR069Utils.Device.ManagementServer.ConnectionRequestPassword,
			TR069Utils.Device.LAN.IPAddress, TR069Utils.Device.LAN.MACAddress,
			TR069Utils.Device.X_00E0FC.STBID, TR069Utils.Device.X_00E0FC.IsEncryptMark,
			TR069Utils.Device.X_00E0FC.ServiceInfo.UserID, TR069Utils.Device.X_00E0FC.ServiceInfo.AuthURL,
			TR069Utils.Device.X_00E0FC.ServiceInfo.PPPoEID
			));
	
//	final public static String[] SOAP_INFO = {TR069Utils.Device.DeviceSummary,
//		TR069Utils.Device.DeviceInfo.SoftwareVersion, TR069Utils.Device.DeviceInfo.HardwareVersion,
//		TR069Utils.Device.DeviceInfo.AdditionalHardwareVersion, TR069Utils.Device.DeviceInfo.AdditionalSoftwareVersion,
//		TR069Utils.Device.DeviceInfo.ModelName, TR069Utils.Device.DeviceInfo.Description,
//		TR069Utils.Device.DeviceInfo.FirstUseDate, TR069Utils.Device.DeviceInfo.UpTime,
//		TR069Utils.Device.DeviceInfo.SerialNumber, TR069Utils.Device.ManagementServer.URL,
//		TR069Utils.Device.ManagementServer.ParameterKey, TR069Utils.Device.ManagementServer.NATDetected,
//		TR069Utils.Device.ManagementServer.UDPConnectionRequestAddress, TR069Utils.Device.ManagementServer.ConnectionRequestURL,
//		TR069Utils.Device.ManagementServer.ConnectionRequestUsername, TR069Utils.Device.ManagementServer.ConnectionRequestPassword,
//		TR069Utils.Device.LAN.IPAddress, TR069Utils.Device.LAN.MACAddress,
//		TR069Utils.Device.X_00E0FC.STBID, TR069Utils.Device.X_00E0FC.IsEncryptMark,
//		TR069Utils.Device.X_00E0FC.ServiceInfo.UserID, TR069Utils.Device.X_00E0FC.ServiceInfo.AuthURL,
//		TR069Utils.Device.X_00E0FC.ServiceInfo.PPPoEID,
//		};
	
	public static String getEventString(int EventCode){
		String event = "";
		switch (EventCode) {
          case EventStructCode.BOOT:
              event = "1 BOOT";// !!!!!!!!!!!!
              break;
          case EventStructCode.BOOTSTRAP:
        	  event = "0 BOOTSTRAP";// !!!!!!!!!!!!
              break;
          case EventStructCode.PERIODIC:
        	  event = "2 PERIODIC";// !!!!!!!!!!!!
              break;
          case EventStructCode.VALUECHANGE:
        	  event = "4 VALUE CHANGE";// !!!!!!!!!!!!
          case EventStructCode.XSHUTDOWN:
        	  event = "X CUCC Shutdown";// !!!!!!!!!!!!
              break;
          case EventStructCode.CONNECTIONREQUEST:
        	  event = "6 CONNECTION REQUEST";// !!!!!!!!!!!!
              break;
          case EventStructCode.DIAGNOSTICSCOMPLETE:
        	  event = "8 DIAGNOSTICSCOMPLETE";// !!!!!!!!!!!!
              break;
          case EventStructCode.XEVENTCODE:
        	  event = "X 00E0FC ErrorCode";// !!!!!!!!!!!!
              break;
          default:
              break;
		}
		return event;
	}
}
