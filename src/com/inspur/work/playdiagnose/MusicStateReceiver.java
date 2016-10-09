package com.inspur.work.playdiagnose;

import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.work.TR069Tools;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.PlayDiagnostics.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class MusicStateReceiver extends BroadcastReceiver {

	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SoftDetect.ini";
	
	private final String ACTION = "MEDIA_PLAY_MONITOR_MESSAGE";
//	private final String PLAYSTART_OPEN = "OPEN";
	public static final String PLAYSTART_PLAYSTART = "playStart";
	public static final String PLAYSTART_PLAYPAUSE = "pause";
//	private final String PLAYSTART_PLAYRESUME = "resume";
	public static final String PLAYSTART_PLAYEND = "playEnd";
//	private final String PLAYSTART_PLAYSEEKEND = "seekEnd";
//	private final String PLAYSTART_PLAYBUFFSERSTART = "bufferStart";
//	private final String PLAYSTART_PLAYBUFFEREND = "bufferEnd";
//	private final String PLAYSTART_PLAYSEEKSART = "seekStart";
//	public final String TAG = "ReadWriteSqmService";
	
//	public static String PLAY_STATE = PLAYSTART_PLAYEND;
	
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.getAction().equals(ACTION)) {
			TR069Log.sayInfo("xxxxxxxx ACTION  " + ACTION);

			String musicState = intent.getExtras().getString("PlayerState");
			if (PLAYSTART_PLAYSTART.equals(musicState)) {
				Tr069DataFile.set(PlayState,Playing);
				String url = TR069Tools.getPlayDiagnoseIniFile(FILE_PATH);
				Tr069DataFile.set(PlayURL,url);
//				PLAY_STATE = PLAYSTART_PLAYSTART;
			} else if (PLAYSTART_PLAYPAUSE.equals(musicState)) {
//				Tr069DataFile.set("Device.X_00E0FC.PlayDiagnostics.PlayState",PLAYSTART_PLAYPAUSE);
//				PLAY_STATE = PLAYSTART_PLAYPAUSE;
			} else if (PLAYSTART_PLAYEND.equals(musicState)) {
				Tr069DataFile.set(PlayState,Stoped);
//				PLAY_STATE = PLAYSTART_PLAYEND;
				Tr069DataFile.set(PlayURL,"null");
			}
		}
	}
}
