
package com.inspur.tools;

import android.util.Log;

public class TR069Log {
    public static boolean LOG_DEBUG = true;

    public static void say(String message) {
        if (LOG_DEBUG) {
            Log.d("tr069", "tr069=========" + message);
        }
    }

    public static void sayInfo(String message) {
        if (LOG_DEBUG) {
            Log.i("tr069", "tr069=========" + message);
        }
    }
    
    public static void sayError(String message) {
            Log.e("tr069", "tr069=========" + message);
    }
}
