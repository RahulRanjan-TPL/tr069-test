package com.inspur.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.inspur.platform.Platform;


public class TR069DateManager {

	private interface IFileRead{
		public boolean doReadLine(String line);
	}
	
	private static HashMap<String, String> sTr069Map = new HashMap<String, String>();
	private static HashMap<String, String> sTr069Cache = new HashMap<String, String>();
	private static File sTr069File = new File("/data/system/tr069data.txt");
	private static StringBuffer sStringBuffer = new StringBuffer(4096);
	private static Object sLock = new Object();
	
	public static void init(){ 
		sTr069Map.putAll(Platform.get().getTr069DefaultValue());
		if(!sTr069File.exists())
		{
			TR069Log.say("InspurDataNew init:file not found");
			try {
				sTr069File.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			TR069Log.say("InspurDataNew init:get file");
			getTr069ValuesformFile(sTr069Map);
//			printfMaps();
		}
		if(!sTr069Cache.isEmpty()){
			sTr069Map.putAll(sTr069Cache);
			sTr069Cache.clear();
			setTr069ValueToFile();
		}
	}
	
	public static String getTr069Value(String key){
		synchronized (sLock) {
			String getValue = null;
			if(sTr069Map.containsKey(key))
			{
				getValue = sTr069Map.get(key);
				return getValue == null ? "" : getValue;
			}else{
				getValue = getTr069ValueformFile(key);
				if(!"".equals(getValue))
				{
					sTr069Map.put(key, getValue);
				}
				
				return getValue;
			}
		}
	}
	
	public static Map<String, String> getTr069Values(List<String> keyList){
		Map<String, String> map = new HashMap<String, String>();
//		for(String key : keyList){//这里这种写法要求List不是由迭代器初始化而来，建议如下写
//			if(sTr069Map.containsKey(key)){
//				map.put(key, sTr069Map.get(key));
//				keyList.remove(key);
//			}
//		}
		Map<String, String> mapFile = new HashMap<String, String>();
		synchronized (sLock) {
			for(Iterator<String> iterator = keyList.iterator(); iterator.hasNext();){
				String key = iterator.next();
				if(sTr069Map.containsKey(key)){
					map.put(key, sTr069Map.get(key));
					iterator.remove();
				}
			}
			
			if(!keyList.isEmpty()){
				getTr069ValuesformFile(keyList, mapFile);
				sTr069Map.putAll(mapFile);
				if(!keyList.isEmpty()){
					for(String key : keyList){
						mapFile.put(key, "");
					}
				}
			}
		}
		
		
		//TODO
		map.putAll(mapFile);
		
		return map;
	}
	
	public static boolean setTr069Value(String key, String value){
		if(key == null || "".equals(key.trim()))
		{
			TR069Log.sayError("setTr069Value key = " + key);
			return false;
		}
		synchronized (sLock) {
			if(value == null || value.equals(""))
			{
				if(sTr069Map.containsKey(key))
					sTr069Map.remove(key);
			}else{
				sTr069Map.put(key, value);
			}
			
			return setTr069ValueToFile();
		}
	}
	
	public static boolean setTr069Values(Map<String, String> keyMaps){
		
		Set<String> keys = keyMaps.keySet();
		ArrayList<String> arrayKeys = new ArrayList<String>(keys);
		
		Map<String, String> maps = getTr069Values(arrayKeys);
		
		synchronized (sLock) {
			sTr069Map.putAll(keyMaps);
			
			Iterator<Entry<String, String>> iterator = keyMaps.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, String> entry = iterator.next();
				if(maps.get(entry.getKey()).equals(entry.getValue()))
				{
//				keyMaps.remove(entry.getKey());//迭代过程中不能这样删除元素同arraylist
					iterator.remove();
				}
			}
			
			return setTr069ValueToFile();
		}
	}
	
	public static List<String> getAllTr069Key(){
		synchronized (sLock) {
			Set<String> keys = sTr069Map.keySet();
			ArrayList<String> keyArray = new ArrayList<String>(keys);
			return keyArray;
		}
	}
	
	/**
	 * 程序初始化过程中的值的缓存，初始化完成后将写入文件
	 * @param key
	 * @param value
	 */
	public static void setTr069Cache(String key, String value){
		sTr069Cache.put(key, value);
	}
	
	public static void printfMaps(){
		Iterator<Entry<String, String>> iterator = sTr069Map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			TR069Log.say("file key=" + entry.getKey() + ",value=" + entry.getValue());
		}
	}
	
	private static boolean setTr069ValueToFile(){
		
		boolean result = false;
		Set<Entry<String, String>> set = sTr069Map.entrySet();
		Iterator<Entry<String, String>> iterator = set.iterator();
		sStringBuffer.delete(0, sStringBuffer.length());
		while(iterator.hasNext()){
			Entry<String, String> entry = iterator.next();
			if(entry.getValue() != null || !entry.getValue().equals(""))
				sStringBuffer.append(entry.getKey() + "=" + entry.getValue() + "\n");
		}
		
		  BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(sTr069File));
			writer.write(sStringBuffer.toString());
			writer.flush();
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	private static String getTr069ValueformFile(final String key){
		final StringBuffer result = new StringBuffer("");
		if(!sTr069File.exists())
			return result.toString();
		
		IFileRead read = new IFileRead() {

			@Override
			public boolean doReadLine(String line) {
				String[] keyValue = line.split("=");
				if(key.equals(keyValue[0])){
					if(keyValue.length > 1)
						result.append(keyValue[1]);
					
					return true;
				}
				return false;
			}
			
		};
		
		readFile(read);
		return result.toString();
		
	}
	
	private static boolean getTr069ValuesformFile(final List<String> keyList, final Map<String, String> map){
		if(!sTr069File.exists())
			return false;
		
		IFileRead read = new IFileRead() {
			
			@Override
			public boolean doReadLine(String line) {
				String[] keyValue = line.split("=");
				if(keyList.contains(keyValue[0])){
					if(keyValue.length > 1){
						map.put(keyValue[0], keyValue[1]);
						keyList.remove(keyValue[0]);
					}else{
//						map.put(keyValue[0], "");
					}
					if(keyList.isEmpty())
						return true;
				}
				return false;
			}
		};
		
		return readFile(read);
	}
	
	private static boolean getTr069ValuesformFile(final Map<String, String> map){
		if(!sTr069File.exists())
			return false;
		
		IFileRead read = new IFileRead() {
			
			@Override
			public boolean doReadLine(String line) {
				String[] keyValue = line.split("=");
//				TR069Log.say(line);
				if(keyValue.length > 1){
					map.put(keyValue[0], keyValue[1]);
				}else{
//					map.put(keyValue[0], "");
				}
				return false;
			}
		};
		
		return readFile(read);
	}
	
	private static boolean readFile(IFileRead fileRead){
		boolean result = false;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(sTr069File);
			bufferedReader = new BufferedReader(fileReader);
			String line = null;
			while((line = bufferedReader.readLine()) != null){
				if(fileRead.doReadLine(line))
					break;
			}
			result = true;
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return result;
	}

}
