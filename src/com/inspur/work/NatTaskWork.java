package com.inspur.work;

import com.inspur.config.InspurData;
import com.inspur.nat.header.MessageHeader;
import com.inspur.nat.util.InspurNat;
import com.inspur.tools.GlobalContext;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import static com.inspur.work.TR069Utils.Device.ManagementServer.*;

public class NatTaskWork extends BaseTaskWork{

//	final public static String STUNSERVERADDRESS_KEY = "Device.ManagementServer.STUNServerAddress";
//	final public static String STUNSERVERADDRESS_UNDEFINE = "undefine";
//	final public static String STUNSERVERPORT_KEY = "Device.ManagementServer.STUNServerPort";

	 final int MAXREPEAT = 1;
	 
	 private String mServerAddress = Undefine;
	 private String mServerPort = "8888";
	 private int miServerPort = 8888;
	 
	 public static void init(){
	 	TR069Log.sayInfo("NatTaskWork register!");
		TR069Utils.registerWork(new NatTaskWork());
	 }
	
	@Override
	public boolean startWork() {
		String addr = InspurData.getInspurValue(STUNServerAddress);
		if(addr == null || "".equals(addr.trim()))
			return false;
		if(Undefine.equals(addr))
			return false;
		
		if(mServerAddress.equals(addr))
			return false;
		
		String newServerPort = InspurData.getInspurValue(STUNServerPort);
		if(newServerPort == null || "".equals(newServerPort.trim())){
			newServerPort = "8888";
		}
		
		mServerAddress = addr;
		mServerPort = newServerPort;
		
		try{
			miServerPort = Integer.parseInt(newServerPort);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		setTaskSleepTime(1 * 60);
		setDelayTime(5);
		startTask();
		
		return true;
	}
	
	@Override
	protected boolean checkStopTask() {
		String addr = InspurData.getInspurValue(STUNServerAddress);
		if(addr == null || "".equals(addr.trim()))
			return false;
		if(Undefine.equals(addr))
			return false;
		
		
		return true;
	}
	
	@Override
	protected void doTaskWork() {
		TR069Log.sayInfo(TAG + " nattask run...,ready send udp package to stun server");
		
		String addr = InspurData.getInspurValue(STUNServerAddress);
		if (addr == null || addr.trim().equals("")) {
			Tr069DataFile.set(STUNServerAddress, mServerAddress);
		}else if(!addr.equals(mServerAddress)){
			mServerAddress = addr;
		}
		
		String newServerPort = InspurData.getInspurValue(STUNServerPort);
		if(newServerPort == null || "".equals(newServerPort.trim())){
			Tr069DataFile.set(STUNServerPort, mServerPort);
		}else if(!newServerPort.equals(mServerPort)){
			mServerPort = newServerPort;
			try{
				miServerPort = Integer.parseInt(newServerPort);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try {
            // 同一个心跳目的，保持id一样!
			GlobalContext.STUNState = true;
            MessageHeader sendMH = new MessageHeader(MessageHeader.MessageHeaderType.BindingRequest);
            sendMH.generateTransactionID();
            for (int i = 0; i < MAXREPEAT; i++) {
            	if(!GlobalContext.STUNState)
            		break;
            	InspurNat.sendBinding(mServerAddress, miServerPort, sendMH.getTransactionID());
            	Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onFinishWork() {
		// TODO Auto-generated method stub
		super.onFinishWork();
	}

	@Override
	public void onErrorWork() {
		Tr069DataFile.set(STUNServerAddress, Undefine);
		super.onErrorWork();
	}
}
