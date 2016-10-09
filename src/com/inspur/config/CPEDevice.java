package com.inspur.config;

import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;
import com.inspur.tr069.ShellUtils.CommandResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CPEDevice {
    public static String getTotalMemory() {
        String memfile = "/proc/meminfo";
        String key = "MemTotal";
        String str = null;
        String result = null;
        try {
            FileReader fr = new FileReader(memfile);
            BufferedReader br = new BufferedReader(fr, 8192);
            while ((str = br.readLine()) != null) {
                TR069Log.say("---" + str);
                if (str.contains(key)) {
                    result = str.split(":")[1].trim();
                    TR069Log.say("---getTotalMemory:" + result);
                    break;
                }

            }
            br.close();
        } catch (IOException e) {
        }
        long lr = Long.parseLong(result.substring(0, result.length() - 3).trim()) / 1000;
        return String.valueOf(lr);
    }

    public static String getFreeMemory() {
        String memfile = "/proc/meminfo";
        String key = "MemFree";
        String str = null;
        String result = null;
        try {
            FileReader fr = new FileReader(memfile);
            BufferedReader br = new BufferedReader(fr, 8192);
            while ((str = br.readLine()) != null) {
                if (str.contains(key)) {
                    result = str.split(":")[1].trim();
                    TR069Log.say("getFreeMemory:" + result);
                    break;
                }

            }
            br.close();
        } catch (IOException e) {
        }
        long lr = Long.parseLong(result.substring(0, result.length() - 3).trim()) / 1000;
        return String.valueOf(lr);
    }

    public static String getCPUUsage() {
        String result = null;
        String commands = "top -m 1 -n 1";
        CommandResult cr = ShellUtils.execCommand(commands, true);
        String all = cr.successMsg;
        String line = null;
        String key = "User";
        if (all != null) {
            String[] allline = all.split("\n");
            for (int i = 0; i < allline.length; i++) {
                line = allline[i];
                if (line.contains(key)) {
                    int start = line.indexOf(key) + key.length();
                    int end = start;
                    while (line.charAt(end) != '%') {
                        end++;
                    }
                    result = line.substring(start, end);
                    TR069Log.say("getCPUUsage:" + result);
                    break;
                }
            }
        }
        return result.trim();
    }

    public static String getUpTime() {
        String cmd = "uptime";
        long result = 0;
        CommandResult cr = ShellUtils.execCommand(cmd, true);
        String all = cr.successMsg;
        String key = "up time:";
        if (all != null && all.contains(key)) {
            int start = all.indexOf(key) + key.length();
            int end = start;
            while (all.charAt(end) != ',') {
                end++;
            }
            String time = all.substring(start, end - 1).trim();
//            TR069Log.say("getUpTime:" + time);
            String[] timearray = time.split(":");
            if (timearray.length == 3) {
                result = Integer.parseInt(timearray[0]) * 60 * 60 + Integer.parseInt(timearray[1]) * 60 + Integer.parseInt(timearray[2]);
            }
        }

        return String.valueOf(result);

    }

}
