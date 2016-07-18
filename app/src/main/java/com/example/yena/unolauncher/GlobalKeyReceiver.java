package com.example.yena.unolauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class GlobalKeyReceiver extends BroadcastReceiver {

	private static final String TAG = "GlobalKeyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("android.intent.action.GLOBAL_BUTTON")) {
			Log.d("d","ddddddddddddddddddddd");
			KeyEvent key = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (key.getAction() == KeyEvent.ACTION_UP) {
				int keycode = key.getKeyCode();
				if (keycode == KeyEvent.KEYCODE_DVR) {
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
