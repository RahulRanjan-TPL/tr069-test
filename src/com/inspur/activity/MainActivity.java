
package com.inspur.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
 
import com.inspur.platform.SleepMonitorService;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 保存全局 context
        GlobalContext.setGlobalContext(this);
        Intent intent = new Intent();
        intent.setAction("com.inspur.tr069.InspurTr069Service");
        intent.setPackage(getPackageName());
        startService(intent);
        TR069Log.say("MainActivity onCreate");
        
        Intent i = new Intent();
        ComponentName name = new ComponentName(this, SleepMonitorService.class);
//        i.setClass(this, SleepMonitorService.class);
        i.setComponent(name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(i);
    }

}
