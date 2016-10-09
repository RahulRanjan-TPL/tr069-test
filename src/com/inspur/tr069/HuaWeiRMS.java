
package com.inspur.tr069;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.AuthCache;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.auth.DigestScheme;
import ch.boye.httpclientandroidlib.impl.client.BasicAuthCache;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.inspur.config.HuaWeiData;
import com.inspur.soap.SoapFactory;
import com.inspur.tools.EventStructCode;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.work.TR069Utils;

/*
 * 调用华为RMS RPC 方法汇总
 * */
public class HuaWeiRMS {
    public static String ACS_GetRPCMethods = "GetRPCMethods";
    public static String ACS_Inform = "Inform";
    public static String ACS_TransferComplete = "TransferComplete";
    public static String ACS_RequestDownload = "RequestDownload";
    public static String ACS_Kicked = "Kicked";
    public static final int BOOT_EVENT = 1;
    public static final int PERIODIC_EVENT = 2;
    public static final int SHUTDOWN_EVENT = 3;
    public static final int CONNECTION_EVENT = 4;

    public static final boolean EMPTY_POST = true;
    public static final boolean NO_EMPTY_POST = false;
    
    final static private String CHARSET = "UTF-8";
    final static private String HEADER = "http://schemas.xmlsoap.org/soap/envelope/";
    
//    private static DefaultHttpClient sHttpClient = new DefaultHttpClient();
//    private static BasicHttpContext sBasicHttpContext = new BasicHttpContext();
//    private static HttpHost sTargetHost;
//    private static HttpPost sHttpEntityPost = new HttpPost();
//    private static HttpPost sHttpEmptyPost = new HttpPost();
//    private static String sCurrentUrl = "";
    
    private static HttpHost initClient(HttpClient client, String url){
    	
    	URL httpUrl = null;
		try {
			httpUrl = new URL(url);
		} catch (MalformedURLException e) {
			TR069Log.sayError("init http client error!!!! url=" + url);
			e.printStackTrace();
			return null;
		}
		HttpHost targetHost = new HttpHost(httpUrl.getHost(), httpUrl.getPort(), "http");
		AuthScope authScope = new AuthScope(targetHost.getHostName(),targetHost.getPort());
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(HuaWeiData.getUsername(url), HuaWeiData.getPassword(url));
		((DefaultHttpClient)client).getCredentialsProvider().setCredentials(authScope, credentials);
		
		return targetHost;
    }
    
    private static HttpPost getEmptyPost(String url){
    	HttpPost httpPost = new HttpPost(url);
    	httpPost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
    	httpPost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
    	return httpPost;
    }
    
    private static void setPostEntity(HttpPost post, String entity) throws UnsupportedEncodingException{
    	StringEntity se = new StringEntity(entity, CHARSET);
    	se.setContentType("text/xml");
    	post.setEntity(se);
    }
    
    private static HttpPost getEntityPost(String url, String entity){
    	HttpPost httpPost = getEmptyPost(url);
		try {
			setPostEntity(httpPost, entity);
		} catch (UnsupportedEncodingException e) {
			TR069Log.sayError("setPostEntity is error! entity:" + entity);
			e.printStackTrace();
		}
		
		return httpPost;
    }
    
    private static BasicHttpContext getHttpContext(HttpHost host){
    	
    	BasicHttpContext context = new BasicHttpContext();
    	AuthCache authCache = new BasicAuthCache();
		DigestScheme digestAuth = new DigestScheme();
		digestAuth.overrideParamter("realm", "");
//        // Suppose we already know the expected nonce value
        digestAuth.overrideParamter("nonce", "");
        authCache.put(host, digestAuth);
        
        // Add AuthCache to the execution context
        context.setAttribute(ClientContext.AUTH_CACHE, authCache);
        
        return context;
    }
    
    /**
     * 
     * 1 : http post inform with entity <br>
     * if(emptyPost){ <br>
     * 		2 : http response and ingore it <br>
     * 		3 : http empty post <br>
     * }<br>
     * 4 : response may be empty or 4 : GetParameterValues() or 6 : SetParameterValues() <br>
     * 5 :                          5 : GetParameterValuesResponse() or 7 : SetParameterValuesResponse()<br>
     * 
     * 8 : empty response<br>
     * @param url inform to url
     * @param entity post entity
     * @param emptyPost true: have empty post false:not have
     * @return result 0 : success
     */
    private static int doInformToTMU1(String url, String entity, boolean emptyPost){
    	int ret = -3;
    	
    	DefaultHttpClient client = new DefaultHttpClient();
    	HttpHost host = initClient(client, url);
    	if(host == null)
    		return ret;
    	
    	HttpPost httpEntityPost = getEntityPost(url, entity);
    	HttpPost httpEmptyPost = getEmptyPost(url);
    	BasicHttpContext context = getHttpContext(host);
    	
    	do{
    		try{
    			HttpResponse response = client.execute(host, httpEntityPost, context);
    			if(emptyPost){
    				EntityUtils.consume(response.getEntity());
    				response = client.execute(host, httpEmptyPost, context);
    			}
    			if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
//                    TR069Log.say("soapContent:" + soapContent);
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains(HEADER)) {
                    	String reslut = TR069Processor.processor(soapContent);
//                    	TR069Log.say("reslut:" + reslut);
                    	setPostEntity(httpEntityPost, reslut);
                        response = client.execute(host, httpEntityPost, context);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
//                        TR069Processor.processor(soapContent);
                    }
    			}
    			ret = 0;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		GlobalContext.addError("9001");
	            ret = -2;
	            TR069Log.say("get error,ready to retry...");
	            try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
	    	}finally{
	    	}
    	}while(ret != 0);
    	client.getConnectionManager().shutdown();
    	return ret;
    }
    
    /*
     * 1 : http post inform with entitys
     * 2 : http response and ingore it (for entitys)
     * 3 : http empty post 
     * 4 : response may be empty or 4 : GetParameterValues() or 6 : SetParameterValues() 
     * 5 :                          5 : GetParameterValuesResponse() or 7 : SetParameterValuesResponse()
     */
    private static int doInformToTMU1(String url, String[] entitys){
    	int ret = -3;
    	
    	DefaultHttpClient client = new DefaultHttpClient();
    	HttpHost host = initClient(client, url);
    	if(host == null)
    		return ret;
    	
    	HttpPost httpEntityPost = getEmptyPost(url);
    	HttpPost httpEmptyPost = getEmptyPost(url);
    	BasicHttpContext context = getHttpContext(host);
    	
    	do{
    		try{
    			HttpResponse response = null;
    			for(String entity : entitys){
    				setPostEntity(httpEntityPost, entity);
    				response = client.execute(host, httpEntityPost, context);
    				EntityUtils.consume(response.getEntity());
    			}
    			response = client.execute(host, httpEmptyPost, context);
    			if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains(HEADER)) {
                    	String reslut = TR069Processor.processor(soapContent);
                    	httpEntityPost = getEntityPost(url, reslut);
                        response = client.execute(host, httpEntityPost, context);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
//                        TR069Processor.processor(soapContent);
                    }
    			}
    			ret = 0;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		GlobalContext.addError("9001");
	            ret = -2;
	            TR069Log.say("get error,ready to retry...");
	            try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
	    	}finally{
//	    		httpEntityPost.releaseConnection();
	    	}
    	}while(ret != 0);
    	client.getConnectionManager().shutdown();
    	return ret;
    }
    
    //盒端管理服务发起的，终端进行回应
    public static int CPEInformACSToTMU(int eventStructCode) {
    	
    	 TR069Log.say("####EC: CPEInformACS eventStructCode :" + EventStructCode.eventToString(eventStructCode));
         String urlStr = TR069Utils.getManagementServer();
         while (urlStr == null || urlStr == "") {
             TR069Log.sayError("fatal error,url null,retry get from tmc!");
             TR069Utils.firstOrTMUnull();
             urlStr = TR069Utils.getManagementServer();
             try {
                 Thread.sleep(1000);
             } catch (Exception e) {
                 
             }
         }
         int ret = doInformToTMU1(urlStr, SoapFactory.createInformXml(eventStructCode), EMPTY_POST);
         TR069Log.say("####EC: CPEInformACS eventStructCode :" + EventStructCode.eventToString(eventStructCode) + ".over");
         return ret;
    }
    
    //盒端发起的
    public static int ACSInformValueChange(String strUrl, List<String> list){
    	TR069Log.say("####EC: ACSInformValueChange");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createInformValueChangeXml(list), EMPTY_POST);
    	TR069Log.say("####EC: ACSInformValueChange.over");
    	return ret;
    }
    
    public static int ACSInformValueChange(String strUrl, String key){
    	TR069Log.say("####EC: ACSInformValueChange for one change key:" + key);
    	int ret = doInformToTMU1(strUrl, SoapFactory.createInformValueChangeXml(key), EMPTY_POST);
    	TR069Log.say("####EC: ACSInformValueChange for one change.over");
    	return ret;
    }
    
    public static int ACSDOShutdownInform(String urlStr){
    	TR069Log.say("####EC: ACSDOShutdownInform");
    	int ret = doInformToTMU1(urlStr, SoapFactory.createInformXml(SHUTDOWN_EVENT), EMPTY_POST);
    	TR069Log.say("####EC: ACSDOShutdownInform.over");
    	return ret;
    }
    
    public static int ACSDoHeartbeat(String strUrl){
    	TR069Log.say("####EC: ACSDoHeartbeat");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createInformXml(PERIODIC_EVENT), EMPTY_POST);
    	TR069Log.say("####EC: ACSDoHeartbeat.over");
    	return ret;
    }
    
    public static int ACSDoRegisterOnStartup(String strUrl){
    	TR069Log.say("####EC: ACSDoRegisterOnStartup");
    	String entity = SoapFactory.createInformXml(BOOT_EVENT);
    	
//    	File file = new File("/data/error.txt");
//		OutputStream stream;
//		try {
//			stream = new FileOutputStream(file);
//			OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
//			streamWriter.write(s);
//			streamWriter.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	int ret = doInformToTMU1(strUrl, entity, EMPTY_POST);
    	TR069Log.say("####EC: ACSDoRegisterOnStartup.over");
    	return ret;
    }
    
    public static int ACSDoUpgradeInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoUpgradeInform");
    	int ret = doInformToTMU1(strUrl, 
    			new String[]{SoapFactory.createUpgradeInformXml(CommandKey),
    						 SoapFactory.createTransferCompleteXml(CommandKey, FaultCode)});
    	TR069Log.say("####EC: ACSDoUpgradeInform.over");
    	return ret;
    }
    
    public static int ACSDoFactoryResetInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoFactoryResetInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createFactoryResetInformXml(CommandKey), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoFactoryResetInform");
    	return ret;
    }
    
    public static int ACSDoRebootInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoRebootInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createRebootInformXml(CommandKey), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoRebootInform");
    	return ret;
    }
    
    public static int ACSDoPingInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoPingInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createPingInformXml(CommandKey), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoPingInform");
    	return ret;
    }
    
    public static int ACSDoTraceRouteInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoTraceRouteInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createTraceRouteInformXml(CommandKey), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoTraceRouteInform");
    	return ret;
    }
    
    public static int ACSDoErrorCodeInform(String strUrl) {
    	TR069Log.say("####EC: ACSDoErrorCodeInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createErrorCodeInformXml(), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoErrorCodeInform");
    	return ret;
    }
    
    public static int ACSDoPlayDiagnoseCodeInform(String strUrl, String CommandKey, String FaultCode) {
    	TR069Log.say("####EC: ACSDoPlayDiagnoseCodeInform");
    	int ret = doInformToTMU1(strUrl, SoapFactory.createPingInformXml(CommandKey), NO_EMPTY_POST);
    	TR069Log.say("####EC: ACSDoPlayDiagnoseCodeInform");
    	return ret;
    }
}
