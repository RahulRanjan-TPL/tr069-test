
package com.inspur.tr069;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.inspur.config.InspurData;
import com.inspur.soap.SoapFactory;
import com.inspur.tools.DebugInfo;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.work.TR069Utils;

public class TR069Processor {

    public static String processor(String soapContent) {
        if (DebugInfo.DEBUG) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            TR069Log.say("methodName = " + methodName);
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            TR069Log.say("Method :" + traceElement.getMethodName());
            // TR069Log.say(DebugInfo.getDebug().parseXML(soapContent));
            TR069Log.say("========ready to processor RMS response,content start");
            TR069Log.say(soapContent);
            TR069Log.say("========ready to processor RMS response,content over");
        }

        if (soapContent == null || soapContent.trim().equals(""))
            return "";
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(soapContent));
            int eventType = xpp.getEventType();
            String id = "1";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        // TR069Log.say("Start document");
                        break;
                    case XmlPullParser.START_TAG:
                        // TR069Log.say("Start tag " + xpp.getName());
                        if (xpp.getName().trim().equalsIgnoreCase("Header")) {
                            if (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("ID")) {
                                id = xpp.nextText();
                                TR069Log.say("id:" + id);
                            }
                        }
                        if (xpp.getName().trim().equalsIgnoreCase("Body")) {
                            xpp.nextTag();
                            return processBody(xpp, id);
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        // TR069Log.say("End tag " + xpp.getName());
                        break;
                    case XmlPullParser.TEXT:
                        // TR069Log.say("Text " + xpp.getText());
                        break;
                }

                eventType = xpp.next();
            }
            TR069Log.say("End document");
            xpp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return "";
    }
 
    private static String processBody(XmlPullParser xpp, String id) throws XmlPullParserException, IOException {
        TR069Log.say("xxxxxxxxxx processBody," + xpp.getName());
        if (xpp.getName().trim().equalsIgnoreCase("GetParameterValues")) {
            TR069Log.say("####EC: yes, we got one get parameter values command!");
            if (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("ParameterNames")) {
                ArrayList<String> listArray = new ArrayList<String>();
                while (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("string")) {
                    String key = xpp.nextText();
                    listArray.add(key);
//                    TR069Log.say("xxxxxxxxxx GetParameterValues key:" + key);
                }
                
//                String[] tmp = new String[listArray.size()];
//                String[] list = listArray.toArray(tmp);
                
                return SoapFactory.createGetParameterValuesResponseXml(listArray, id);
            }
        }
        if (xpp.getName().trim().equalsIgnoreCase("GetParameterNames")) {
            TR069Log.say("GetParameterNames operation start");
            int flag = 0;
            String ParameterPath = null;
            String NextLevel = null;
            while (flag < 2) {
                int nexttag = xpp.next();
                if (nexttag == XmlPullParser.START_TAG) {
                    TR069Log.say("yyyyyyyyy name:" + xpp.getName());
                    if (xpp.getName().trim().equalsIgnoreCase("ParameterPath")) {
                        ParameterPath = xpp.nextText();
                        TR069Log.say("ParameterPath:" + ParameterPath);
                        flag = flag + 1;
                    }
                    if (xpp.getName().trim().equalsIgnoreCase("NextLevel")) {
                        NextLevel = xpp.nextText();
                        TR069Log.say("NextLevel:" + NextLevel);
                        flag = flag + 1;
                    }
                }
                if (nexttag == XmlPullParser.END_TAG && xpp.getName().trim().equals("GetParameterNames")) {
                    break;
                }
            }
            return SoapFactory.createGetParameterNamesResponseXml(id, ParameterPath, NextLevel);
        }
        if (xpp.getName().trim().equalsIgnoreCase("GetParameterAttributes")) {
            TR069Log.say("####EC: yes, we got one GetParameterAttributes!");
            ArrayList<String> params = new ArrayList<String>();
            while (true) {
                int nexttag = xpp.nextTag();
                if (nexttag == XmlPullParser.END_TAG && xpp.getName().trim().equals("GetParameterAttributes")) {
                    break;
                }
                if (nexttag == XmlPullParser.START_TAG) {
                    if (xpp.getName().trim().equalsIgnoreCase("string")) {
                        params.add(xpp.nextText());
                    }
                }

            }
            return SoapFactory.createGetParameterAttributesResponse(id, params);
        }

        if (xpp.getName().trim().equalsIgnoreCase("Download")) {
            TR069Log.say("####EC: yes, we got one download command!");
            String url = null;
            int flag = 0;
            while (flag < 2) {
                if (xpp.next() == XmlPullParser.START_TAG) {
                    TR069Log.say("yyyyyyyyy name:" + xpp.getName());
                    if (xpp.getName().trim().equalsIgnoreCase("CommandKey")) {
                        InspurData.setInspurValue("CommandKey", xpp.nextText());
                        flag = flag + 1;
                    }
                    if (xpp.getName().trim().equalsIgnoreCase("URL")) {
                        url = xpp.nextText();
                        flag = flag + 1;
                    }
                }
            }
            TR069Log.say("yyyyyyyyy name:" + xpp.getName());
            TR069Log.say("xxxxxxxxxxxxxxxxx upgrade download url:" + url);
            if (url != null && !url.trim().equals("")) {
                final String MyUrl = url;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        TR069Utils.doUpgrade(MyUrl);
                    }
                }).start();

                return SoapFactory.createDownloadResponseXml(id);
            } else {
                return "";
            }
        }
        if (xpp.getName().trim().equalsIgnoreCase("SetParameterValues")) {
            TR069Log.say("####EC: yes, we got one setparameter values command!");
            if (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("ParameterList")) {
                HashMap<String, String> map = new HashMap<String, String>();
                while (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("ParameterValueStruct")) {
                    String key = null;
                    String value = null;
                    if (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("Name")) {
                        key = xpp.nextText();
                    }

                    if (xpp.nextTag() == XmlPullParser.START_TAG && xpp.getName().trim().equalsIgnoreCase("Value")) {
                        value = xpp.nextText();
                    }
                    if (key != null && value != null) {
                        TR069Log.say("#SetParameterValues key:" + key + "\n--value:" + value);
                        map.put(key, value);
                    }
                    // should be end tag for value
                    if (xpp.nextTag() != XmlPullParser.END_TAG && xpp.getName().trim().equalsIgnoreCase("ParameterValueStruct")) {
                        TR069Log.say("EEEEEEEEEEEEEE");
                        return "";
                    }
                }
                InspurData.updateValues(map);
                return SoapFactory.createSetParameterValuesResponseXml(id);
            }
        }
        if (xpp.getName().trim().equalsIgnoreCase("SetParameterAttributes")) {
            TR069Log.say("####EC: yes, we got one SetParameterAttributes!");
            ArrayList<ParameterAttributes> params = new ArrayList<ParameterAttributes>();
            while (true) {
                int nexttag = xpp.nextTag();
                if (nexttag == XmlPullParser.END_TAG && xpp.getName().trim().equals("SetParameterAttributes")) {
                    break;
                }
                if (nexttag == XmlPullParser.START_TAG) {
                    if (xpp.getName().trim().equalsIgnoreCase("SetParameterAttributesStruct")) {
                        while (true) {
                            ParameterAttributes newpa = new ParameterAttributes();
                            nexttag = xpp.nextTag();
                            if (nexttag == XmlPullParser.END_TAG && xpp.getName().trim().equals("SetParameterAttributesStruct")) {
                                break;
                            }
                            if (xpp.getName().trim().equals("Name")) {
                                newpa.Name = xpp.nextText();
                                // how to do?
                            }
                            if (xpp.getName().trim().equals("NotificationChange")) {
                                newpa.NotificationChange = xpp.nextText();
                            }
                            if (xpp.getName().trim().equals("Notification")) {
                                newpa.Notification = xpp.nextText();
                            }
                            if (xpp.getName().trim().equals("AccessListChange")) {
                                newpa.AccessListChange = xpp.nextText();
                            }
                            params.add(newpa);
                        }
                    }
                }
            }
            return SoapFactory.createSetParameterAttributesResponseXml(id, params);
        }

        // reboot the stb
        if (xpp.getName().trim().equalsIgnoreCase("Reboot")) {
            TR069Log.say("####EC: yes, we got one reboot command!");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("Reboot", "true");
            InspurData.updateValues(map);
            return SoapFactory.createRebootResponseXml(id);
        }

        // wipe the data
        if (xpp.getName().trim().equalsIgnoreCase("FactoryReset")) {
            TR069Log.say("####EC: yes, we got one factory reset command!");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("FactoryReset", "true");
            InspurData.updateValues(map);
            return SoapFactory.createFactoryResetResponseXml(id);
        }

        // just inform response, you should do nothing
        if (xpp.getName().trim().equalsIgnoreCase("InformResponse")) {
            TR069Log.say("####EC: yes, we got one informresponse!");
            return "";
        }

        if (xpp.getName().trim().equalsIgnoreCase("GetRPCMethods")) {
            TR069Log.say("####EC: yes, we got one GetRPCMethods!");

            return SoapFactory.createGetRPCMethodsInformXml(id);
        }

        TR069Log.say("####EC: now you must know there is still something that you have to do:" + xpp.getName());
        GlobalContext.addError("9000");

        return "";
    }
}
