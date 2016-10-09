
package com.inspur.tools;

import java.util.List;

/**
 * 过度类，由于使用的地方太多，尽量不破坏结构
 * @author Inpur
 *
 */
public class Tr069DataFile {
  
    public static void set(String key, String newValue) {
       TR069DateManager.setTr069Value(key, newValue);
    }

    public static void setNotWrite(String key, String newValue) {
        TR069DateManager.setTr069Cache(key, newValue);
     }
    
    public static String get(String key) {
        return TR069DateManager.getTr069Value(key);
    }

    public static List<String> getAllKey() {
        return TR069DateManager.getAllTr069Key();
    }
}
