package com.example.yena.unolauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

public class GlobalKeyReceiver extends BroadcastReceiver {

	private static final String TAG = "GlobalKeyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.GLOBAL_BUTTON")) {
			KeyEvent key = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (key.getAction() == KeyEvent.ACTION_UP) {
				Log.d("keyevent","키는들어왓지만들어오진않았따");
				int keycode = key.getKeyCode();
				if (keycode == KeyEvent.KEYCODE_PERIOD) {
					SharedPreferences pref = context.getSharedPreferences(UNOSharedPreferences.NAME, 0);
					if(pref.getInt(UNOSharedPreferences.IS_FOREGROUND,MainActivity.IS_IN_BACKGROUND) == MainActivity.IS_IN_BACKGROUND){
						Intent mIntent = new Intent(context, MainActivity.class);
						mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
							context.startActivity(mIntent);
							Log.d(TAG, "start Activity!");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}
}
