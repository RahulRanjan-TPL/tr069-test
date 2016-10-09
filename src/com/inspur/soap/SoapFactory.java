
package com.inspur.soap;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.inspur.config.InspurData;
import com.inspur.tools.DebugInfo;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069DateManager;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.ErrorCode;
import com.inspur.tr069.ParameterAttributes;
import com.inspur.work.TR069Utils;

public class SoapFactory {

	private interface ISocapWork{
		public void doWork(XmlSerializer serializer) throws IOException;
	}
	
	private static void createDocStart(XmlSerializer serializer) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_ENVELOPE);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_SOAP_ENV, SoapUtils.XML_SOAP_ENV_VALUE);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_SOAP_ENC, SoapUtils.XML_SOAP_ENC_VALUE);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_XSD, SoapUtils.XML_XSD_VALUE);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_XSI, SoapUtils.XML_XSI_VALUE);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_CWMP, SoapUtils.XML_CWMP_VALUE);
	}
	
	private static void createDocEnd(XmlSerializer serializer) throws IOException{
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_ENVELOPE);
	}
	
	private static void createDocHeader(XmlSerializer serializer, String id) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_HEADER);
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.CWMP_ID);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_MUSTUNDERSTAND, "1");
		serializer.text(id);
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.CWMP_ID);
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_HEADER);
	}
	
	private static void createDocBodyStart(XmlSerializer serializer) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_BODY);
	}
	
	private static void createDocBodyEnd(XmlSerializer serializer) throws IOException{
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_BODY);
	}
	
	private static void createDocInformStart(XmlSerializer serializer, String value) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, value);
	}
	
	private static void createDocInformEnd(XmlSerializer serializer, String value) throws IOException{
		serializer.endTag(SoapUtils.ENV_NAMESPACE, value);
	}
	
	private static void createDocDeviceID(XmlSerializer serializer) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.DEVICE_ID);// !!!!!!!!!!!!!!!!
	   
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.MANUFACTURER_ID);
		serializer.text(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.Manufacturer));
//		TR069Log.say(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.Manufacturer));
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.MANUFACTURER_ID);
		// OUI
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.OUI_ID);
		serializer.text(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.ManufacturerOUI));
//		TR069Log.say(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.ManufacturerOUI));
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.OUI_ID);
		// ProductClass
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PRODUCT_CLASS_ID);
       	serializer.text(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.ProductClass));
//       	TR069Log.say(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.ProductClass));
       	serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PRODUCT_CLASS_ID);
       	// SerialNumber
       	serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.SERIAL_NUMBER);
       	serializer.text(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.SerialNumber));
//       	TR069Log.say(TR069DateManager.getTr069Value(TR069Utils.Device.DeviceInfo.SerialNumber));
       	serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.SERIAL_NUMBER);
       	serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.DEVICE_ID);
	}
	
	private static void createDocCommandkey(XmlSerializer serializer, String CommandKey) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.COMMAND_KEY);
		if(CommandKey != null)
			serializer.text(CommandKey);
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.COMMAND_KEY);
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT_STRUCT);
	}
	
	private static void createDocEvent(XmlSerializer serializer, String[] events, String CommandKey) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE, 
				String.format(SoapUtils.ENC_ARRAYTYPE_EVENT_STRUCT, events.length));

		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT_STRUCT);

		for(String event : events){
			serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT_CODE);
			serializer.text(event);// !!!!!!!!!!!!
			serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT_CODE);
		}
		createDocCommandkey(serializer, CommandKey);
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.EVENT);
	}
	
	private static void createDocCurrentTime(XmlSerializer serializer) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.MAX_ENVELOPES);
		serializer.text(String.valueOf(new Random().nextInt()));
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.MAX_ENVELOPES);
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.CURRENT_TIME);
		//serializer.text(InspurData.getDate(SystemClock.elapsedRealtime(), "yyyyMMdd'T'HHmmss.Z"));
		serializer.text(InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss"));
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.CURRENT_TIME);
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.RETRY_COUNT);
		serializer.text("0");
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.RETRY_COUNT);
	}
	
	private static void createDocParamList(XmlSerializer serializer, List<String> params) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE,
				String.format(SoapUtils.ENC_ARRAYTYPE_PARAMETER, params.size()));
 
		Map<String, String> maps = InspurData.getInspurValue(params);
		for(Entry<String, String> entry : maps.entrySet()){
			TR069Log.say("#get key :" +  entry.getKey() + "\n--value ：" + entry.getValue());
			ParameterHelp(serializer, entry.getKey(), entry.getValue());
		}

//		for(String param : params){
//			ParameterHelp(serializer, param, InspurData.getInspurValue(param));
//		}
		
		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
	}
	
	private static void createDocParamList(XmlSerializer serializer, String params) throws IOException{
		serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
		serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE,
				String.format(SoapUtils.ENC_ARRAYTYPE_PARAMETER, 1));

		
		ParameterHelp(serializer, params, InspurData.getInspurValue(params));

		serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
	}
	
	private static void ParameterHelp(XmlSerializer serializer, String key, String value) throws IOException {
    	// ParameterValueStruct1
        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_STRUCT);
        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
        serializer.text(key);
        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_VALUE);
        serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_XSI_TYPE_KEY, SoapUtils.XML_XSD_TYPE_STRING);
        if (value != null) {
            serializer.text(value);
        } else {
            serializer.text("null");
        }
        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_VALUE);
        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_STRUCT);
    }
	
	private static String commondSoapMethed(ISocapWork work, String id){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument(null, null);
			//start
			createDocStart(serializer);
			//header
			createDocHeader(serializer, id);
			//body
			createDocBodyStart(serializer);
			
			work.doWork(serializer);
			
			createDocBodyEnd(serializer);
			createDocEnd(serializer);
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
	
	//ok
	public static String createInformValueChangeXml(final List<String> list) {
		if(list == null || list.isEmpty())
			return "";
		
		ISocapWork work = new ISocapWork() {
			
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
				//device id
				createDocDeviceID(serializer);
				//event
				createDocEvent(serializer, new String[]{SoapUtils.VALUE_CHANGE_EVENT}, null);
				createDocCurrentTime(serializer);
				//parameter
				createDocParamList(serializer, list);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
			}
		};
		
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createInformValueChangeXml(final String key) {
		if(key == null || "".equals(key))
			return "";
		
		ISocapWork work = new ISocapWork() {
			
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
				//device id
				createDocDeviceID(serializer);
				//event
				createDocEvent(serializer, new String[]{SoapUtils.VALUE_CHANGE_EVENT}, null);
				createDocCurrentTime(serializer);
				//parameter
				createDocParamList(serializer, key);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
			}
		};
		
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createInformXml(final int EventCode) {
		
		ISocapWork work = new ISocapWork() {
			
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
				//device id
				createDocDeviceID(serializer);
				//event
				
				createDocEvent(serializer, new String[]{SoapUtils.getEventString(EventCode)}, null);
				createDocCurrentTime(serializer);
				//parameter
				createDocParamList(serializer, SoapUtils.SOAP_INFO);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
			}
		};
		
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createGetParameterValuesResponseXml(final List<String> list, String id) {
		
		if(list == null)
			return "";
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_GET_PARAMETERVALUES_RESPONSE);
				//params
				createDocParamList(serializer, list);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_GET_PARAMETERVALUES_RESPONSE);
			}
		};
		
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createDownloadResponseXml(String id) {
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_DOWNLOAD_RESPONSE);
				//params
				serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STATUS);
	            serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_XSI_TYPE_KEY, SoapUtils.XML_XSD_TYPE_INT);
	            serializer.text(String.valueOf(new Random().nextInt()));
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STATUS);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_DOWNLOAD_RESPONSE);
			}
		};
		
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createSetParameterValuesResponseXml(String id) {
		
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_SET_PARAMETERVALUES_RESPONSE);
				//params
				serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STATUS);
	            serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.XML_XSI_TYPE_KEY, SoapUtils.XML_XSD_TYPE_INT);
	            serializer.text("0");
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STATUS);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_SET_PARAMETERVALUES_RESPONSE);
			}
		};
		
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createUpgradeInformXml(final String CommandKey) {
		ISocapWork work = new ISocapWork() {
			
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
				//device id
				createDocDeviceID(serializer);
				//event
				
				createDocEvent(serializer, new String[]{SoapUtils.BOOT_EVENT, 
						SoapUtils.TRANSFER_COMPLETE_EVENT, SoapUtils.VALUE_CHANGE_EVENT}, CommandKey);
				createDocCurrentTime(serializer);
				//parameter
				createDocParamList(serializer, SoapUtils.SOAP_INFO);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
			}
		};
		
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createTransferCompleteXml(final String CommandKey, final String FaultCode) {
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_TRANSFER_COMPLETE);
				//params
				createDocCommandkey(serializer, CommandKey);
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_STRUCT);

	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_CODE);
	            serializer.text(FaultCode);
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_CODE);
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_STRING);
	            if (FaultCode.equals("0")) {
	            	serializer.text("inspur say upgrade fail!");
	            }
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_STRING);

	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.FAULT_STRUCT);

	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_START_TIME);
	            serializer.text(SoapUtils.ENV_UNDEFINE);
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_START_TIME);
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_COMPLETE_TIME);
	            serializer.text(SoapUtils.ENV_UNDEFINE);
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_COMPLETE_TIME);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_TRANSFER_COMPLETE);
			}
		};
		
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createRebootResponseXml(String id) {
		
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_REBOOT_RESPONSE);
				//params
				createDocCommandkey(serializer, null);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_REBOOT_RESPONSE);
			}
		};
		
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createRebootInformXml(String id) {
		 
		ISocapWork work = new ISocapWork() {
				@Override
				public void doWork(XmlSerializer serializer) throws IOException {
					//event
					createDocEvent(serializer, new String[]{SoapUtils.REBOOT_EVENT}, null);
				}
			};
			
		return commondSoapMethed(work, id);
	 }
	
	//ok
	public static String createFactoryResetResponseXml(String id) {
		 ISocapWork work = new ISocapWork() {
				@Override
				public void doWork(XmlSerializer serializer) throws IOException {
					//inform
					createDocInformStart(serializer, SoapUtils.CWMP_FACTORYRESET_RESPONSE);
					//endinfom
					createDocInformEnd(serializer, SoapUtils.CWMP_FACTORYRESET_RESPONSE);
				}
			};
			
			return commondSoapMethed(work, id);
	 }
	
	//ok
	public static String createFactoryResetInformXml(String id) {
		 
		 ISocapWork work = new ISocapWork() {
				@Override
				public void doWork(XmlSerializer serializer) throws IOException {
					//event
					createDocEvent(serializer, new String[]{SoapUtils.BOOTSTART_EVENT}, null);
				}
			};
			
		return commondSoapMethed(work, id);
	 }
	
	//ok
	public static String createPingInformXml(String id) {
		 
		 ISocapWork work = new ISocapWork() {
				
				@Override
				public void doWork(XmlSerializer serializer) throws IOException {
					//inform
					createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
					//device id
					createDocDeviceID(serializer);
					//event
					
					createDocEvent(serializer, new String[]{SoapUtils.DIAGNOSTICSCOMPLETE_EVENT}, null);
					createDocCurrentTime(serializer);
					//parameter
					createDocParamList(serializer, TR069Utils.Device.DeviceInfo.SerialNumber);
					//endinfom
					createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
				}
			};
			
		return commondSoapMethed(work, id);
	 }
	 
	//ok
	public static String createTraceRouteInformXml(String id) {
		 return createPingInformXml(id);
	}
	
	
	private static void createDocErrorList(XmlSerializer serializer, ArrayList<ErrorCode> errorcodes) throws IOException{
			serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
			serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE,
					String.format(SoapUtils.ENC_ARRAYTYPE_PARAMETER, errorcodes.size()));

			for(int i = 0; i < errorcodes.size(); i++){
				ParameterHelp(serializer, "Device.X_00E0FC.ErrorCode." + i + ".ErrorCodeTime", errorcodes.get(i).getErrorCodeTime());
				ParameterHelp(serializer, "Device.X_00E0FC.ErrorCode." + i + ".ErrorCodeValue", errorcodes.get(i).getErrorCodeValue());
			}
			
			serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
	}
	
	//ok
	public static String createErrorCodeInformXml() {
		 ISocapWork work = new ISocapWork() {
				
				@Override
				public void doWork(XmlSerializer serializer) throws IOException {
					//inform
					createDocInformStart(serializer, SoapUtils.CWMP_INFORM);
					//device id
					createDocDeviceID(serializer);
					//event
					
					createDocEvent(serializer, new String[]{SoapUtils.O0E0FC_ERRORCODE_EVENT}, null);
					createDocCurrentTime(serializer);
					//parameter
					createDocErrorList(serializer, GlobalContext.allErrors);
					GlobalContext.allErrors.clear();
//					createDocParamList(serializer, SoapUtils.SOAP_INFO);
					//endinfom
					createDocInformEnd(serializer, SoapUtils.CWMP_INFORM);
				}
			};
			
		return commondSoapMethed(work, String.valueOf(new Random().nextInt()));
	}
	
	//ok
	public static String createGetRPCMethodsInformXml(String id) {
		 
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_GET_RPC_METHODS_RESPONSE);
				//params
				serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_METHOD_LIST);
	            serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE, "string[4]");
	
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	            serializer.text("GetRPCMethods");
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	            serializer.text("Inform");
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	            serializer.text("TransferComplete");
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	
	            serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	            serializer.text("AutonomousTransferComplete");
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_STRING);
	
	            serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.ENV_METHOD_LIST);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_GET_RPC_METHODS_RESPONSE);
				}
			};
			
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createGetParameterNamesResponseXml(String id, String parameterPath, String nextLevel) {
		 ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_GET_PARAMETERNAMES_RESPONSE);
				//params
				List<String> list = Tr069DataFile.getAllKey();
				
				serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
				serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE,
						String.format(SoapUtils.ENC_ARRAYTYPE_INFO_PARAMETER, list.size()));

				for(String param : list){
					serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_INFO_STRUCT);
			        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
			        serializer.text(param);
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
			        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_WRITEABLE);
			        serializer.text("true");
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_WRITEABLE);
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_INFO_STRUCT);
				}

				serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_GET_PARAMETERNAMES_RESPONSE);
			}
		};
			
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createGetParameterAttributesResponse(String id, final ArrayList<String> params) {
		 ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_GET_PARAMETERATTR_RESPONSE);
				//params
				
				serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
				serializer.attribute(SoapUtils.ENV_NAMESPACE, SoapUtils.ENC_ARRAYTYPE,
						String.format(SoapUtils.ENC_ARRAYTYPE_ATTR_PARAMETER, params.size()));

				for(String param : params){
					serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_ATTR_STRUCT);
			        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
			        serializer.text(param);
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NAME);
			        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NOTIFICATION);
			        serializer.text("2");
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_NOTIFICATION);
			        serializer.startTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_ACCESSLIST);
			        serializer.text("Subscriber");
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_ACCESSLIST);
			        serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_ATTR_STRUCT);
			    
				}

				serializer.endTag(SoapUtils.ENV_NAMESPACE, SoapUtils.PARAMETER_LIST);
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_GET_PARAMETERATTR_RESPONSE);
			}
		};
			
		return commondSoapMethed(work, id);
	}
	
	//ok
	public static String createSetParameterAttributesResponseXml(String id, ArrayList<ParameterAttributes> params) {
		ISocapWork work = new ISocapWork() {
			@Override
			public void doWork(XmlSerializer serializer) throws IOException {
				//inform
				createDocInformStart(serializer, SoapUtils.CWMP_SET_PARAMETERATTR_RESPONSE);
				//params
				
				//endinfom
				createDocInformEnd(serializer, SoapUtils.CWMP_SET_PARAMETERATTR_RESPONSE);
			}
		};
			
		return commondSoapMethed(work, id);
	 }
	 
/*    public static String createInformValueChangeXml(ArrayList<String> list) {
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
    }*/

/*    // EventCode:1:boot 2:PERIODIC
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
        return writer.toString();
    }*/

    public static String createReponseXml() {
        return "";
    }

/*    public static String createGetParameterValuesResponseXml(ArrayList<String> list, String id) {
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
    }*/

/*    public static String createDownloadResponseXml(String id) {
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
    }*/

/*    public static String createSetParameterValuesResponseXml(String id) {
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
    }*/

/*    public static String createUpgradeInformXml(String CommandKey) {

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

    }*/

/*    public static String createTransferCompleteXml(String CommandKey, String FaultCode) {

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

    }*/

/*    public static String createRebootResponseXml(String id) {
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
    }*/

/*    public static String createRebootInformXml(String id) {
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
    }*/

/*    public static String createFactoryResetResponseXml(String id) {
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
    }*/

/*    public static String createFactoryResetInformXml(String id) {
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
    }*/

    // 8 DIAGNOSTICS COMPLETE
/*    public static String createPingInformXml(String id) {
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
    }*/

    // 8 DIAGNOSTICS COMPLETE
/*    public static String createTraceRouteInformXml(String id) {
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
    }*/

    // X CM ErrorCode
/*    public static String createErrorCodeInformXml() {
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
    }*/

/*    public static String createGetRPCMethodsInformXml(String id) {
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
    }*/

/*    public static String createGetParameterNamesResponseXml(String id, String parameterPath, String nextLevel) {
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
            ArrayList<String> list = Tr069DataFile.getAllKey();
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

    }*/

/*    public static String createGetParameterAttributesResponse(String id, ArrayList<String> params) {
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

    }*/

/*    public static String createSetParameterAttributesResponseXml(String id, ArrayList<ParameterAttributes> params) {

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

    }*/
}
