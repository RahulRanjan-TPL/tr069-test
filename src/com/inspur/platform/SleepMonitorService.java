
package com.inspur.platform;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.inspur.tools.EventStructCode;
import com.inspur.tools.TR069Log;
import com.inspur.tr069.HuaWeiRMS;

public class SleepMonitorService extends Service {
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            TR069Log.say("tr069 got  broadcast!");
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                TR069Log.say("got ACTION_SCREEN_OFF broadcast!");
                TR069Log.say("send shutdown to tr069!");
                new Thread(new Runnable() {
                    public void run() {
                        HuaWeiRMS.CPEInformACSToTMU(EventStructCode.XSHUTDOWN);
                    }
                }).start();

                try {
                    // 等一下上报完成
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TR069Log.say("send shutdown to tr069 over!");
            }
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                TR069Log.say("got ACTION_SCREEN_ON broadcast!");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        
    }

    private void dynamicRegister(Context context) {
        TR069Log.say("ready register ACTION_SCREEN");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

        context.registerReceiver(mReceiver, intentFilter);
        TR069Log.say("register ACTION_SCREEN over");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (null != mReceiver) {
            this.unregisterReceiver(mReceiver);
        }
        
        Intent i = new Intent();
        i.setClass(this, SleepMonitorService.class);
        startService(i);
        
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	dynamicRegister(this);
        return super.onStartCommand(intent, START_STICKY, startId);
    }

}
