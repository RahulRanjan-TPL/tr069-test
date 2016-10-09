
package com.inspur.tools;

import android.content.Context;

import com.inspur.config.InspurData;
import com.inspur.tr069.ErrorCode;

import java.util.ArrayList;

public class GlobalContext {
    private static Context context = null;

    public static void setGlobalContext(Context outcontext) {
        context = outcontext;
    }

    public static Context getGlobalContext() {
        if (context == null) {
            TR069Log.say("fatal,error,context not ready!are you seriously?!");
        }
        return context;
    }

    public static void addError(String ErrorCode)
    {
    	ErrorCode newError = new ErrorCode();
        //SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss.Z", Locale.getDefault());
        //newError.setErrorCodeTime(df.format(new Date()));
		newError.setErrorCodeTime(InspurData.getUTCTime("yyyy-MM-dd'T'HH:mm:ss"));
        newError.setErrorCodeValue(ErrorCode);
        allErrors.add(newError);
    }
    
    // 记录系统的所有错误码
    public static ArrayList<ErrorCode> allErrors = new ArrayList<ErrorCode>();
    
    public static boolean STUNState = true;
}
