
package com.inspur.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tr069DataFileOld {

    private static File file = new File("/data/system/tr069data.txt");
    private static Object lock = new Object();
    private static StringBuffer sFileBuffer;
    
    static{
    	sFileBuffer = new StringBuffer(4096);
    }
    
    public static void setNotWrite(String key, String newValue) {
    	set(key, newValue);
     }
    
    public static void set(String key, String newValue) {
        synchronized (lock) {
            if (newValue == null || newValue.trim().equals("")) {
                newValue = "";
            }
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    FileWriter fw = new FileWriter(file);
                    fw.write(key + "=" + newValue + "\n");
                    if (fw != null) {
                        fw.flush();
                        fw.close();
                    }

                } else {
                	String value = get(key);
                    // 情况1 key 不存在
                    if (value == null) {
                        FileWriter fw = new FileWriter(file, true);
                        fw.append(key + "=" + newValue + "\n");
                        if (fw != null) {
                            fw.flush();
                            fw.close();
                        }
                        return;
                    }
                    // 情况2 key 存在，value一致
                    if (value.equals(newValue))
                    {
                    	return;
                    } else// 情况3 key 存在，value不一致 要更新
                    {
                    	sFileBuffer.delete(0, sFileBuffer.length());
                        BufferedReader br = new BufferedReader(new java.io.FileReader(file));
//                        StringBuffer sb = new StringBuffer(4096);// 最大2k
                        String line;
                        while ((line = br.readLine()) != null) {
                        	if(line.contains(key+"=")){
                        		line = key + "=" + newValue;
                        	}
//                        	String[] keyvalue = line.split("=");
//                            if (line.split("=").length == 1) {
//                                continue;
//                            }
//                            String linekey = line.split("=")[0];
////                            String linevalue = line.split("=")[1];
//                            if (linekey.equals(key)) {
//                                // br.close();
//                                line = linekey + "=" + newValue;
//                            }
                        	sFileBuffer.append(line + "\n");

                        }
                        br.close();
                        // 删除重写
                        file.delete();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                        bw.write(sFileBuffer.toString());
                        bw.flush();
                        bw.close();
                    }

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String get(String key) {
        synchronized (lock) {
            String value = null;
            if (!file.exists())
                return value;
            BufferedReader br = null;
            try {
                br = new BufferedReader(new java.io.FileReader(file));

                String line;
                while ((line = br.readLine()) != null) {
                    if(line.contains(key + "="))
                    {
                    	String[] keyvalue = line.split("=");
                    	if(keyvalue.length > 1){
                    		br.close();
                    		return keyvalue[1];
                    	}
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally{
            	if(br != null)
            	{
            		try {
            			br.close();
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
            	}
            }

            return value;
        }
    }

    public static ArrayList<String> getAllKey() {
        ArrayList<String> keyList = new ArrayList<String>();
        if (file.exists() == false)
            return null;
        BufferedReader br;
        try {
            br = new BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split("=").length == 1) {
                    continue;
                }
                String linekey = line.split("=")[0];
                keyList.add(linekey);
            }
            br.close();
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
        return keyList;
    }
}
