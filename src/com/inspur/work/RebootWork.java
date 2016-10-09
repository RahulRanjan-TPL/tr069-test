package com.inspur.work;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.Toast;

import com.inspur.config.InspurData;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import static com.inspur.work.TR069Utils.Reboot;
import static com.inspur.work.TR069Utils.RebootTrue;
import static com.inspur.work.TR069Utils.RebootFalse;

public class RebootWork extends BaseWork{

//	final public static String REBOOT_KEY = "Reboot";
//	final public static String REBOOT_TRUE = "true";
//	final public static String REBOOT_FALSE = "false";
	
	public static void init() {
		TR069Log.sayInfo("RebootWork register!");
		Tr069DataFile.setNotWrite(Reboot, RebootFalse);
		TR069Utils.registerWork(new RebootWork());
	}
	
	@Override
	public boolean startWork() {
		if(!RebootTrue.equals(InspurData.getInspurValue(Reboot)))
		{
			return false;
		}
		
		doReboot();
		
		return true;
	}

	private void doReboot()
	{
		 Handler handler = new Handler(Looper.getMainLooper());
	        handler.post(new Runnable() {
	            @Override
	            public void run() {
	                Toast.makeText(GlobalContext.getGlobalContext(), "---系统将在10s后重启---", Toast.LENGTH_LONG).show();
	            }
	        });
	        try {
	            Thread.sleep(10000);
	        } catch (InterruptedException e1) {
	            e1.printStackTrace();
	        }
	        
	        // 已经发送了reboot response 不是要发inform 更不是在这里发！
	        // // inform M reboot
	        // String manageurl = InspurData.getInspurValue("Device.ManagementServer.URL");
	        // String commandkey = InspurData.getInspurValue("CommandKey");
	        // if (manageurl == null || commandkey == null) {
	        // TR069Log.say("error in get reboot param,forget it.");
	        // } else {
	        // HuaWeiRMS.ACSDoRebootInform(InspurData.getInspurValue("Device.ManagementServer.URL"), InspurData.getInspurValue("CommandKey"), "0");
	        // }

	        PowerManager pm = (PowerManager) GlobalContext.getGlobalContext().getSystemService(Context.POWER_SERVICE);
	        pm.reboot(null);
	}
}
