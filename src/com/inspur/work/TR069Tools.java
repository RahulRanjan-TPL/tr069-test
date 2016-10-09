package com.inspur.work;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;

public class TR069Tools {
	
	public static String getUploadName(String split)
	{
		 DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
         String time = formatter.format(new Date());
         if (InspurData.getInspurValue("Device.LAN.MACAddress") == null) {
             TR069Log.say("cccccccccc error get mac address.");
             return null;
         }
         
         String[] mac = InspurData.getInspurValue("Device.LAN.MACAddress").split(":");
         String MAC = mac[0] + mac[1] + mac[2] + mac[3] + mac[4] + mac[5];
         String fileName = MAC + split + time;
         fileName = fileName.toLowerCase();
         
         return fileName;
	}

	public static String getMonStr(String mon) {
        String result = null;
        if (mon.equals("01") || mon.equals("1")) {
            result = "Jan";
        } else if (mon.equals("02") || mon.equals("2")) {
            result = "Feb";
        } else if (mon.equals("03") || mon.equals("3")) {
            result = "Mar";
        } else if (mon.equals("04") || mon.equals("4")) {
            result = "Apr";
        } else if (mon.equals("05") || mon.equals("5")) {
            result = "May";
        } else if (mon.equals("06") || mon.equals("6")) {
            result = "Jun";
        } else if (mon.equals("07") || mon.equals("7")) {
            result = "Jul";
        } else if (mon.equals("08") || mon.equals("8")) {
            result = "Aug";
        } else if (mon.equals("09") || mon.equals("9")) {
            result = "Sep";
        } else if (mon.equals("10")) {
            result = "Oct";
        } else if (mon.equals("11")) {
            result = "Nov";
        } else if (mon.equals("12")) {
            result = "Dec";
        }

        return result;
    }
	
    public static int versionToInt(String version) {
        // 适合 a.b.c[.d.e...]-rcA格式
        int iversion = 0;
        try {
            String pverion = version.split("-")[0];
            String[] aversion = pverion.split("\\.");
            for (int i = 0; i < aversion.length; i++) {
                iversion = (int) (iversion + Integer.parseInt(aversion[i]) * Math.pow(10, aversion.length - 1 - i));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return iversion;

    }
    
    public static String getPlayDiagnoseIniFile(String path) {
		String url = null;
		BufferedReader bufferedReader = null;
		try {
			FileInputStream inputStream = new FileInputStream(path);
			InputStreamReader reader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(reader);
			// int length = inputStream.available();

			// byte[] buffer = new byte[length];

			// String urlTmp = EncodingUtils.getString(buffer, "UTF-8");
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("playUrl")) {
					TR069Log.sayInfo("xxxxxxxx get url:" + line);
					Pattern pattern = Pattern.compile("(?<=http.?):.*?.m3u8");
					Matcher matcher = pattern.matcher(line);

					if (matcher.find()) {
						url = "http" + matcher.group();
						break;
					}

				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			TR069Log.sayInfo("xxxxxxxx not found file :" + path);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return url;
	}
}
