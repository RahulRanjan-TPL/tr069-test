
package com.inspur.tools;

public class EventStructCode {
    public static final int BOOTSTRAP = 0x0000;
    public static final int BOOT = 0x0001;
    public static final int PERIODIC = 0x0002;
    public static final int SCHEDULED = 0x0003;
    public static final int VALUECHANGE = 0x0004;
    public static final int KICKED = 0x0005;
    public static final int CONNECTIONREQUEST = 0x0006;
    public static final int TRANSFERCOMPLETE = 0x0007;
    public static final int DIAGNOSTICSCOMPLETE = 0x0008;
    public static final int MMETHOD = 0x0009;
    public static final int XEVENTCODE = 0x000a;
    public static final int XSHUTDOWN = 0x000b;

    public static String eventToString(int eventcode) {
        switch (eventcode) {
            case 0x0:
                return "BOOTSTRAP";
            case 0x1:
                return "BOOT";
            case 0x2:
                return "PERIODIC";
            case 0x3:
                return "SCHEDULED";
            case 0x4:
                return "VALUECHANGE";
            case 0x5:
                return "KICKED";
            case 0x6:
                return "CONNECTIONREQUEST";
            case 0x7:
                return "TRANSFERCOMPLETE";
            case 0x8:
                return "DIAGNOSTICSCOMPLETE";
            case 0x9:
                return "MMETHOD";
            case 0xa:
                return "XEVENTCODE";
            case 0x0b:
                return "XSHUTDOWN";

            default:
                break;
        }
        return "";

    }
}
