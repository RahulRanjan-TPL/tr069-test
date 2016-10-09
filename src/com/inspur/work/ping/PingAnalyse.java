
package com.inspur.work.ping;

import com.inspur.tools.TR069Log;

public class PingAnalyse {

    private static String SuccessCount = "0";
    private static String FailureCount = "0";
    private static String AverageResponseTime = "0";
    private static String MaximumResponseTime = "0";
    private static String MinimumResponseTime = "0";

    public static void reset() {
        SuccessCount = "0";
        FailureCount = "0";
        AverageResponseTime = "0";
        MaximumResponseTime = "0";
        MinimumResponseTime = "0";
    }

    public static String getSuccessCount(String result) {
        try {
            reset();
            if (result.contains("received,")) {
                String endkey = " received,";
                int endindex = result.indexOf(endkey);
                int startindex = endindex - 1;
                while (true) {
                    if (result.charAt(startindex) == ' ' || result.charAt(startindex) == '\n') {
                        break;
                    }
                    startindex--;
                }
                SuccessCount = result.substring(startindex + 1, endindex);
            }
            TR069Log.sayInfo("pppppppppp successcount:" + SuccessCount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return SuccessCount.trim();
    }

    public static String getFailureCount(String result) {
        try {
            reset();
            if (result.contains("packets transmitted")) {
                String endkey = " packets transmitted,";
                int endindex = result.indexOf(endkey);
                int startindex = endindex - 1;
                while (true) {
                    if (result.charAt(startindex) == ' ' || result.charAt(startindex) == '\n') {
                        break;
                    }
                    startindex--;
                }
                int success = Integer.parseInt(getSuccessCount(result));
                int all = Integer.parseInt(result.substring(startindex + 1, endindex));
                FailureCount = String.valueOf(all - success);
            }
            TR069Log.sayInfo("pppppppppp FailureCount:" + FailureCount);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return FailureCount.trim();
    }

    public static String getAverageResponseTime(String result) {
        try {
            reset();
            String startkey = "rtt min/avg/max/mdev = ";
            String endkey = " ms";
            if (result.contains("min/avg/max/")) {
                int start = result.indexOf(startkey) + startkey.length();
                int end = result.indexOf(endkey);
                String[] time = result.substring(start, end).split("\\/");
                // 华为网管不认识浮点数
                AverageResponseTime = time[1].split("\\.")[0];
            }
            TR069Log.sayInfo("pppppppppp AverageResponseTime:" + AverageResponseTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return AverageResponseTime.trim();
    }

    public static String getMaximumResponseTime(String result) {
        try {
            reset();
            String startkey = "rtt min/avg/max/mdev = ";
            String endkey = " ms";
            if (result.contains("min/avg/max/")) {
                int start = result.indexOf(startkey) + startkey.length();
                int end = result.indexOf(endkey);
                String[] time = result.substring(start, end).split("\\/");
                // 华为网管不认识浮点数
                MaximumResponseTime = time[2].split("\\.")[0];
            }
            TR069Log.sayInfo("pppppppppp MaximumResponseTime:" + MaximumResponseTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return MaximumResponseTime.trim();
    }

    public static String getMinimumResponseTime(String result) {
        try {
            reset();
            String startkey = "rtt min/avg/max/mdev = ";
            String endkey = " ms";
            if (result.contains("min/avg/max/")) {
                int start = result.indexOf(startkey) + startkey.length();
                int end = result.indexOf(endkey);
                String[] time = result.substring(start, end).split("\\/");
                // 华为网管不认识浮点数
                MinimumResponseTime = time[0].split("\\.")[0];
            }
            TR069Log.sayInfo("pppppppppp MinimumResponseTime:" + MinimumResponseTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return MinimumResponseTime.trim();
    }

}
