
package com.inspur.tr069;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inspur.platform.SleepMonitorService;
import com.inspur.tools.GlobalContext;

public class BootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 保存全局 context
        GlobalContext.setGlobalContext(context);
        Intent intentTr069 = new Intent("com.inspur.tr069.InspurTr069Service");
//        if()
        context.startService(intentTr069);
        Intent i = new Intent();
        i.setClass(context, SleepMonitorService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}
