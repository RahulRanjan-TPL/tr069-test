package com.inspur.work;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.inspur.config.InspurData;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import static com.inspur.work.TR069Utils.FactoryReset;
import static com.inspur.work.TR069Utils.FactoryResetTrue;
import static com.inspur.work.TR069Utils.FactoryresetFalse;

public class FactoryResetWork extends BaseWork{

//	final public static String FACTORYRESET_KEY = "FactoryReset";
//	final public static String FACTORYRESET_TRUE = "true";
//	final public static String FACTORYRESET_FALSE = "false";
	
	public static void init() {
		TR069Log.sayInfo("FactoryResetWork register!");
		Tr069DataFile.setNotWrite(FactoryReset, FactoryresetFalse);
		TR069Utils.registerWork(new FactoryResetWork());
	}
	
	@Override
	public boolean startWork() {
		if(!FactoryResetTrue.equals(InspurData.getInspurValue(FactoryReset)))
		{
			return false;
		}
		
		doFactoryReset();
		
		return true;
	}

	public static void doFactoryReset() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GlobalContext.getGlobalContext(), "---正在执行恢复出厂设置，请稍后---", Toast.LENGTH_LONG).show();
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        if (GlobalContext.getGlobalContext() != null) {
            TR069Log.sayInfo("ready execute fatory reset!");
            GlobalContext.getGlobalContext().sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
        }
    }
}
