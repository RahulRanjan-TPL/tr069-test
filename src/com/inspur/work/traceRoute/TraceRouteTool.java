
package com.inspur.work.traceRoute;

import com.inspur.tools.TR069Log;
import com.inspur.tr069.ShellUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TraceRouteTool {
    private static long exetime = 0;

    public static TraceRouteDiagnostics execTracertCmd(String host, int MaxHopCount, int Timeout) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        /*
         * FIXME！ TODO 限制跳跃次数，否则网管返回超时
         */
        if (MaxHopCount > 10) {
            MaxHopCount = 10;
        }

        try {
            exetime = 0;
            long startTime = System.currentTimeMillis();
            String command = "busybox traceroute -m " + MaxHopCount + " -w 1 " + host;
            TR069Log.sayInfo("tttttttttt Start trace route command:" + command);
            result = ShellUtils.execCommand(command, true).successMsg;
            long endTime = System.currentTimeMillis();
            exetime = endTime - startTime;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        TR069Log.sayInfo("tttttttttt Start parseResult !:" + result);
        return parseResult(result);
    }

    public static TraceRouteDiagnostics parseResult(String result) {
        // TODO Auto-generated method stub
        TraceRouteDiagnostics trd = new TraceRouteDiagnostics();
        if (result.contains("bad address")) {
            trd.setDiagnosticsState("Error_CannotResolveHostName");
            return trd;
        }
        if (result.contains("not in 1..255 range")) {
            trd.setDiagnosticsState("Error_MaxHopCountExceeded");
            return trd;
        }
        try {
            String[] line = result.split("\n");
            trd.setDiagnosticsState("Complete");
            trd.setResponseTime(String.valueOf(exetime));
            String[] hosts = new String[line.length - 1];

            if (line[0] != null && !line[0].trim().equals("")) {
                int index = line[0].indexOf(" hops max");
                int hop = Integer.parseInt(line[0].substring(index - 1, index));
                trd.setNumberOfRouteHops(line.length - 1);
                TR069Log.sayInfo("tttttttttt  " + hop);
                for (int i = 1; i < line.length; i++) {
                    TR069Log.sayInfo("xxx line" + i + ":" + line[i]);
                    int lindex = line[i].indexOf("(");
                    int rindex = line[i].indexOf(")");
                    if (lindex != -1 && rindex != -1) {
                        hosts[i - 1] = line[i].substring(lindex + 1, rindex);
                        TR069Log.sayInfo("tttttttttt  " + hosts[i - 1]);
                    } else {
                        hosts[i - 1] = "*";
                    }
                }
                trd.setRouteHops(hosts);
            } else {
                trd.setNumberOfRouteHops(0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            trd.setDiagnosticsState("None");
        }
        return trd;
    }
}
