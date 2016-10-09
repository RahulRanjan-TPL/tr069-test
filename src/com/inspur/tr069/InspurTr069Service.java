
package com.inspur.tr069;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;

import com.inspur.config.InspurData;
import com.inspur.nat.util.InspurNat;
import com.inspur.platform.MXDevice;
import com.inspur.tools.CHexConver;
import com.inspur.tools.EventStructCode;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069DateManager;
import com.inspur.tools.InspurNetwork;
import com.inspur.tools.TR069Log;
import com.inspur.work.TR069Utils;

public class InspurTr069Service extends Service {

	final private String TAG = getClass().getSimpleName();
	
	private BroadcastReceiver mNetReceiver;

    @Override
    public void onCreate() {
        TR069Log.say(TAG + " InspurTr069Service onCreate");
        super.onCreate();
        TR069Utils.init(this);
        TR069DateManager.init();
        mNetReceiver = new NetWorkChange();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TR069Log.say(TAG + " InspurTr069Service onStartCommand flg:" + flags);
        if(intent != null){
        	TR069Log.say("intent :" + intent.getAction());
        }
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(ConnectivityManager.INET_CONDITION_ACTION);
//        filter.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
//        filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        
        registerReceiver(mNetReceiver, filter);
        
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				initWork();
			}
		}).start();
        
        return super.onStartCommand(intent, START_STICKY, startId);
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
    	return null;
    }
    
    @Override
    public void onDestroy() {
    	
    	TR069Log.say(TAG + " InspurTr069Service onDestroy");
    	
    	unregisterReceiver(mNetReceiver);
    	
    	Intent intent = new Intent("this is killed");
    	intent.setClass(this, InspurTr069Service.class);
    	try{
    		startService(intent);
    	}catch(Exception e){
    		
    	}
        super.onDestroy();
    }

    private void initWork(){
      // 检查网络是否已经连通
      while (!InspurNetwork.isConnectInternet()) {
          try {
              Thread.sleep(1000);
              TR069Log.say(TAG + " not connect sleep 1 seconds");
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }

      TR069Log.say(TAG + " check TMU!so important!");
      boolean isfirst = InspurData.checkIsFirstStartup();
      boolean isTmuNull = false;
      if (InspurData.getInspurValue("Device.ManagementServer.URL") == null) {
          isTmuNull = true;
      }
      if (isfirst || isTmuNull) {
          TR069Log.say(TAG + " try to get tmu from tmc,isfirst = " + isfirst + "tmunull:" + isTmuNull);
          TR069Utils.firstOrTMUnull();
      }

      TR069Log.say(TAG + " check TMU end!so important!");

      // important!let tr069 know the nat across
      HuaWeiRMS.CPEInformACSToTMU(EventStructCode.VALUECHANGE);

      String lastversion = null;
      String nowversion = android.os.Build.VERSION.INCREMENTAL;
      String needupgrade = InspurData.getInspurValue("needupgrade");

      if (InspurData.getInspurValue("Device.DeviceInfo.SoftwareVersion") != null) {
          lastversion = InspurData.getInspurValue("Device.DeviceInfo.SoftwareVersion");
      } else {
          lastversion = "";
      }
      TR069Log.say(TAG + " needupgrade:" + needupgrade + " lastversion:" + lastversion + " nowversion:" + nowversion);
      // 判断是否是升级后的启动
      if (needupgrade != null && needupgrade.equals("true")) { // 是执行升级后的首次启动
          TR069Log.say(TAG + " first boot after upgrade");
          if (!nowversion.equals(lastversion)) { // 升级成功
              TR069Log.say(TAG + " upgrade sucess!commandkey:" + InspurData.getInspurValue("CommandKey"));
              // 1.发送升级成功的inform
              HuaWeiRMS.ACSDoUpgradeInform(InspurData.getInspurValue("Device.ManagementServer.URL"), InspurData.getInspurValue("CommandKey"), "0");
              // 3.变更标志
              InspurData.setInspurValue("needupgrade", "false");
          } else {
              TR069Log.sayError(TAG + " upgrade fail!");
              HuaWeiRMS.ACSDoUpgradeInform(InspurData.getInspurValue("Device.ManagementServer.URL"), InspurData.getInspurValue("CommandKey"), "9801");
              InspurData.setInspurValue("needupgrade", "false");
          }

      }
      // 该变量是为了区分升级成功还是失败，用于版本比较
      InspurData.setInspurValue("Device.DeviceInfo.SoftwareVersion", android.os.Build.VERSION.INCREMENTAL);

      TR069Log.say(TAG + " we  get tmu,send startup to it ,get commands! ");
      TR069Utils.sendStartupToTmu();
      MXDevice.startNtpwork();
      doOnParamChangeTask();

      udpReceiveServerStart();
    }
    
    private void doOnParamChangeTask()
    {
    	
    	new Thread(new Runnable() {
			
//    		String ip = "";
        	String userid = "";
    		
			@Override
			public void run() {
				while(true)
				{
//					String getIp = InspurNetwork.getActiveLocalIp();
//					if(getIp == null)
//					{
//						getIp = "";
//					}
//					
//					if(!ip.equals(getIp))
//					{
//						TR069Log.say("doOnParamChangeTask: on ip chanage");
//						ip = getIp;
////						ArrayList<String> lists = new ArrayList<String>();
////						lists.add(TR069Utils.STB_IP_KEY);
//						HuaWeiRMS.ACSInformValueChange(TR069Utils.getManagementServer(), TR069Utils.Device.LAN.IPAddress);
//					}
					
					String getUserId = "";
					Uri uri = Uri.parse("content://stbconfig/authentication/username");
					Cursor cursor = GlobalContext.getGlobalContext().getContentResolver().query(uri, null, null, null,null);
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							getUserId = cursor.getString(0);
						}
						
						cursor.close();
					}
					
					if(!userid.equals(getUserId))
					{
						TR069Log.say("doOnParamChangeTask: on userid chanage");
						userid = getUserId;
//						ArrayList<String> lists = new ArrayList<String>();
//						lists.add(TR069Utils.STB_AUTH_USERID);
						HuaWeiRMS.ACSInformValueChange(TR069Utils.getManagementServer(), TR069Utils.Device.X_CU_STB.AuthServiceInfo.UserID);
					}
					
					TR069Log.say("doOnParamChangeTask: task");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
    }
    
    private void udpReceiveServerStart() {
        new Thread(new Runnable() {
            private byte[] msg = new byte[1024];

            @Override
            public void run() {
            	TR069Utils.doWorkTask();
            	DatagramSocket dRecevieSocket = null;
                try {
                	
                	
                    dRecevieSocket = new DatagramSocket(null);
                    DatagramPacket dReceivePacket = new DatagramPacket(msg, msg.length);
                    dRecevieSocket.setReuseAddress(true);
                    dRecevieSocket.bind(new InetSocketAddress(InspurNat.defaultPort));
                    byte[] lastid = new byte[16];
                    byte[] nowid = new byte[16];
                    String tr069Lastid = null;
                    String tr069nowid = null;
                    String udpConnectionContent = null;
                    String udpConnectionKey = "&sig=";
                    int index = 0;
                    while (true) {
                        dRecevieSocket.receive(dReceivePacket);
                        TR069Log.say("get the data[0] = " + dReceivePacket.getData()[0]);

                        if (dReceivePacket.getData()[0] == 0 || dReceivePacket.getData()[0] == 1) {
                            // nowid reset!!!!
                            Arrays.fill(nowid, (byte) 0);
                            for (index = 4; index < 20; index++) {
                                nowid[index - 4] = dReceivePacket.getData()[index];
                            }
                            TR069Log.say("id:" + CHexConver.byte2HexStr(nowid, nowid.length));

                            // 过滤掉可能重复的包，非常重要。
                            if (Arrays.equals(lastid, nowid)) {
                                TR069Log.say("same id ignore it!");
                                continue;
                            }
                            System.arraycopy(nowid, 0, lastid, 0, nowid.length);
                            if (dReceivePacket.getData()[0] == 1 && dReceivePacket.getData()[1] == 1) {
                                String publicUrl = InspurNat.getPublicUrlFromUdpPacket(dReceivePacket);
                                String oldPublicUrl = InspurData.getInspurValue("Device.ManagementServer.UDPConnectionRequestAddress");
                                TR069Log.say("got binding response ： net through = " + publicUrl);
                                GlobalContext.STUNState = false;
                                if (oldPublicUrl == null || (oldPublicUrl != null && !oldPublicUrl.equals(publicUrl))) {
                                    TR069Log.say("!oldPublicUrl.equals(publicUrl)");
                                    InspurData.setInspurValue("Device.ManagementServer.UDPConnectionRequestAddress", publicUrl);
                                    InspurData.setInspurValue("Device.ManagementServer.NATDetected", "true");
//                                    ArrayList<String> list = new ArrayList<String>();
//                                    list.add("Device.ManagementServer.UDPConnectionRequestAddress");
//                                    list.add("Device.ManagementServer.NATDetected");
//                                    HuaWeiRMS.CPEInformACSToTMU(EventStructCode.VALUECHANGE);
                                }
                            } else {
                                TR069Log.sayError("fatal error,somting we don not konw!" + CHexConver.byte2HexStr(dReceivePacket.getData(), dReceivePacket.getData().length));
                            }
                        } else {
                            TR069Log.say("UDP Connection Requests!");
                            udpConnectionContent = new String(dReceivePacket.getData());
                            // TR069Log.say("nnnnnnnnnn ####EC:UDP Connection Requests content:" + udpConnectionContent);

                            if (udpConnectionContent.contains("GET") && udpConnectionContent.contains(udpConnectionKey)) {
                                int startindex = udpConnectionContent.indexOf(udpConnectionKey) + udpConnectionKey.length();
                                tr069nowid = udpConnectionContent.substring(startindex, startindex + 40);
                                TR069Log.say("UDP Connection Requests,nowid:" + tr069nowid);
                            }
                            // same id got ,ignore
                            if (tr069Lastid != null && tr069nowid != null && tr069nowid.equals(tr069Lastid)) {
                                TR069Log.say(":UDP Connection Requests,sameid got ,ignore it");
                                continue;
                            }
                            tr069Lastid = tr069nowid;

                            HuaWeiRMS.CPEInformACSToTMU(EventStructCode.CONNECTIONREQUEST);

                            TR069Utils.doWorkTask();

                        }
//                        dRecevieSocket.close();
                    }
                } catch (Exception e) {
                    TR069Log.sayError("error binding port!!!!....");
                    e.printStackTrace();
                }finally{
                	if(dRecevieSocket != null)
                		dRecevieSocket.close();
                	
                	udpReceiveServerStart();
                }
            }
        }).start();
    }

    class NetWorkChange extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			TR069Log.say(arg1.getAction());
			InspurNetwork.onNetworkChange();
//			TR069Log.say(arg1.getExtra(ConnectivityManager.EXTRA_NETWORK_TYPE) + "");
		}
    	
    }
}
