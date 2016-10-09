
package com.inspur.tr069;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.AuthCache;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.auth.DigestScheme;
import ch.boye.httpclientandroidlib.impl.client.BasicAuthCache;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.inspur.config.HuaWeiData;
import com.inspur.config.InspurData;
import com.inspur.soap.SoapFactory;
import com.inspur.tools.EventStructCode;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.work.TR069Utils;

/*
 * 调用华为RMS RPC 方法汇总
 * */
public class HuaWeiRMSOld {
    public static String ACS_GetRPCMethods = "GetRPCMethods";
    public static String ACS_Inform = "Inform";
    public static String ACS_TransferComplete = "TransferComplete";
    public static String ACS_RequestDownload = "RequestDownload";
    public static String ACS_Kicked = "Kicked";
    public static final int BOOT_EVENT = 1;
    public static final int PERIODIC_EVENT = 2;
    public static final int SHUTDOWN_EVENT = 3;
    public static final int CONNECTION_EVENT = 4;

    /*
     * 该类保证交付质量，如果出现失败，会一直重试到成功。
     */

    public static int CPEInformACSToTMU(int eventStructCode) {
        TR069Log.say("####EC: CPEInformACS eventStructCode :" + EventStructCode.eventToString(eventStructCode));
        int ret = -3;
        String urlStr = InspurData.getInspurValue("Device.ManagementServer.URL");
        while (urlStr == null || urlStr == "") {
            TR069Log.say("fatal error,url null,retry get from tmc!");
            TR069Utils.firstOrTMUnull();
            urlStr = InspurData.getInspurValue("Device.ManagementServer.URL");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to
                // the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformXml(eventStructCode), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : GetParameterValues() or 6 : SetParameterValues()
                response = httpclient.execute(targetHost, httppost, localcontext);
                // �����жϿպ�ֱ�ӷ��أ�����finally������޷�ִ��
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                    	String reslut = TR069Processor.processor(soapContent);
                        // 5 : GetParameterValuesResponse() or 7 :
                        // SetParameterValuesResponse()
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                        TR069Processor.processor(soapContent);
                    }
                }
                ret = 0;
                TR069Log.say("####EC: CPEInformACS eventStructCode :" + EventStructCode.eventToString(eventStructCode) + " over.");
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                ret = -2;
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return ret;
    }

    public static int ACSPassiveConnect(String urlStr) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to
                // the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformXml(CONNECTION_EVENT), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : GetParameterValues() or 6 : SetParameterValues()
                response = httpclient.execute(targetHost, httppost, localcontext);
                // ²»ÄÜÅÐ¶Ï¿ÕºóÖ±½Ó·µ»Ø£¬·ñÔòfinallyµÄÓï¾äÎÞ·¨Ö´ÐÐ
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 : GetParameterValuesResponse() or 7 :
                        // SetParameterValuesResponse()
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static void ACSInformValueChange(String urlStr, String key) {
    	ArrayList<String> tmp = new ArrayList<String>(Arrays.asList(key));
    	ACSInformValueChange(urlStr, tmp);
    }
    
    public static void ACSInformValueChange(String urlStr, List<String> list) {
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformValueChangeXml(list), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : GetParameterValues() or 6 : SetParameterValues()
                response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 : GetParameterValuesResponse() or 7 :
                        // SetParameterValuesResponse()
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return;
    }

    
    
    public static void ACSDOShutdownInform(String urlStr) {
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformXml(SHUTDOWN_EVENT), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : HTTP Response empty() or others
                response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
    }

    public static void ACSDoHeartbeat(String urlStr) {
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.PERIODIC_EVENT
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformXml(PERIODIC_EVENT), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : HTTP Response empty() or others
                response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
    }

    public static int ACSDoRegisterOnStartup(String urlStr) {
        int ret = -1;
        do {
            TR069Log.say("ACS_DO_Inform:" + urlStr);
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createInformXml(BOOT_EVENT), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : GetParameterValues() or 6 : SetParameterValues()
                response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    TR069Log.say("####EC: soapcontent = " + soapContent);
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 : GetParameterValuesResponse() or 7 :
                        // SetParameterValuesResponse()
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoUpgradeInform(String urlStr, String CommandKey, String FaultCode) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 0 BOOTSTRAP / 1 BOOT()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createUpgradeInformXml(CommandKey), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 3.call TransferComplete
                httppost = new HttpPost(urlStr);
                se = new StringEntity(SoapFactory.createTransferCompleteXml(CommandKey, FaultCode), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 4 : TransferComplete response
                response = httpclient.execute(targetHost, httppost, localcontext);
                EntityUtils.consume(response.getEntity());
                // 5.HTTP Post empty()
                httppost = new HttpPost(urlStr);
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                // 4 : GetParameterValues() or 6 : SetParameterValues()
                response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !(soapContent.trim().equals("")) && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 : GetParameterValuesResponse() or 7 :
                        // SetParameterValuesResponse()
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoFactoryResetInform(String urlStr, String CommandKey, String FaultCode) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // inform 0 bootstrap
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createFactoryResetInformXml(CommandKey), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoRebootInform(String urlStr, String CommandKey, String FaultCode) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform M Reboot()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createRebootInformXml(CommandKey), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoPingInform(String urlStr, String CommandKey, String FaultCode) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 10: Inform 8 DIAGNOSTICS COMPLETE()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createPingInformXml(CommandKey), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 11 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoTraceRouteInform(String urlStr, String CommandKey, String FaultCode) {
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 8 DIAGNOSTICS COMPLETE()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createTraceRouteInformXml(CommandKey), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoErrorCodeInform(String urlStr) {
        // TODO Auto-generated method stub
        TR069Log.say("ACS_DO_Inform:" + urlStr);
        int ret = -1;
        do {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                HttpHost targetHost = new HttpHost(new URL(urlStr).getHost(), new URL(urlStr).getPort(), "http");
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()), new UsernamePasswordCredentials(HuaWeiData.getUsername(urlStr), HuaWeiData.getPassword(urlStr)));
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                // Generate DIGEST scheme object, initialize it and add it to the
                // local auth cache
                DigestScheme digestAuth = new DigestScheme();
                // Suppose we already know the realm name
                digestAuth.overrideParamter("realm", "");
                // Suppose we already know the expected nonce value
                digestAuth.overrideParamter("nonce", "");
                authCache.put(targetHost, digestAuth);
                // Add AuthCache to the execution context
                BasicHttpContext localcontext = new BasicHttpContext();
                localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
                // 1.Inform 8 DIAGNOSTICS COMPLETE()
                HttpPost httppost = new HttpPost(urlStr);
                StringEntity se = new StringEntity(SoapFactory.createErrorCodeInformXml(), HTTP.UTF_8);
                se.setContentType("text/xml");
                httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                httppost.setEntity(se);
                // 2 : InformResponse()
                HttpResponse response = httpclient.execute(targetHost, httppost, localcontext);
                if (response.getEntity() != null) {
                    String soapContent = EntityUtils.toString(response.getEntity());
                    EntityUtils.consume(response.getEntity());
                    while (soapContent != null && !soapContent.trim().equals("") && soapContent.contains("http://schemas.xmlsoap.org/soap/envelope/")) {
                        String reslut = TR069Processor.processor(soapContent);
                        // 5 continue
                        httppost = new HttpPost(urlStr);
                        httppost.setHeader("Accept", "text/html, application/xhtml+xml, */*");
                        httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
                        se = new StringEntity(reslut, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        response = httpclient.execute(targetHost, httppost, localcontext);
                        if (response.getEntity() == null)
                            break;
                        soapContent = EntityUtils.toString(response.getEntity());
                        EntityUtils.consume(response.getEntity());
                    }
                }
                ret = 0;
            } catch (Exception e) {
                e.printStackTrace();
                GlobalContext.addError("9001");
                try {
                    TR069Log.say("get error,ready to retry...");
                    Thread.sleep(1000);
                } catch (Exception exception) {

                    exception.printStackTrace();
                }
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        } while (ret != 0);
        return 0;
    }

    public static int ACSDoPlayDiagnoseCodeInform(String urlStr, String CommandKey, String FaultCode) {
    	return ACSDoPingInform(urlStr, CommandKey, FaultCode);
    }
}
