package com.inspur.work.playdiagnose;

import android.os.Environment;

import com.inspur.config.InspurData;
import com.inspur.tools.TR069Log;
import com.inspur.tools.Tr069DataFile;
import com.inspur.tr069.HuaWeiRMS;
import com.inspur.work.BaseWork;
import com.inspur.work.TR069Tools;
import com.inspur.work.TR069Utils;
import static com.inspur.work.TR069Utils.Device.X_00E0FC.PlayDiagnostics.*;

public class PlaydiagnoseWork extends BaseWork {

//	final public static String DIAGNOSTICS_STATE_KEY = "Device.X_00E0FC.PlayDiagnostics.DiagnosticsState";
//	final public static String STATE_REQUESTED = "Requested";
//	final public static String STATE_COMPLETE = "Complete";
//	final public static String STATE_NONE = "None";
//	final public static String PLAY_URL_KEY = "Device.X_00E0FC.PlayDiagnostics.PlayURL";
//	final public static String PLAY_STATE = "Device.X_00E0FC.PlayDiagnostics.PlayState";
//	final public static String PLAYING = "1";
//	final public static String STOPED = "0";

	private static final String FILE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SoftDetect.ini";

	private String mstrFileUrl;

	public static void init() {
		TR069Log.sayInfo("PlaydiagnoseWork register!");
		Tr069DataFile.setNotWrite(DiagnosticsState, StateStop);
		Tr069DataFile.setNotWrite(PlayState, Stoped);
		Tr069DataFile.setNotWrite(PlayURL, "null");
		TR069Utils.registerWork(new PlaydiagnoseWork());
	}

	@Override
	public boolean startWork() {

		if (!StateRequested.equals(InspurData
				.getInspurValue(DiagnosticsState))) {
			return false;
		}

		startWorkBase();
		mstrFileUrl = TR069Tools.getPlayDiagnoseIniFile(FILE_PATH);
		String compareUrl = Tr069DataFile.get(PlayURL);

		if (mstrFileUrl != null && mstrFileUrl.equals(compareUrl)) {
			TR069Log.sayInfo(TAG + "compareUrl == fileUrl");
			Tr069DataFile.set(DiagnosticsState, StateComplete);
		} else {
			TR069Log.sayInfo(TAG + "compareUrl != fileUrl");
			Tr069DataFile.set(DiagnosticsState, StateStop);
		}

		onFinishWork();
		return true;
	}

	@Override
	public void onFinishWork() {

		HuaWeiRMS.ACSDoPlayDiagnoseCodeInform(TR069Utils.getManagementServer(),
				TR069Utils.getCommandKey(), "0");

		if (Playing.equals(Tr069DataFile.get(PlayState))) {
			Tr069DataFile.set(PlayURL, mstrFileUrl);
		} else {
			Tr069DataFile.set(PlayURL, null);
		}

		super.onFinishWork();
	}

	@Override
	public void onErrorWork() {
		// TODO Auto-generated method stub
		super.onErrorWork();
	}

}
