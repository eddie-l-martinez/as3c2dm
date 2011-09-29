package com.riaspace.c2dm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.adobe.fre.FREContext;

public class C2DMBroadcastReceiver extends BroadcastReceiver {

	public C2DMBroadcastReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
			handleMessage(context, intent);
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		FREContext freContext = C2DMExtension.context;
		String registration = intent.getStringExtra("registration_id");

		if (intent.getStringExtra("error") != null) {
			String error = intent.getStringExtra("error");
			Log.d("as3c2dm", "Registration failed with error: " + error);
			if (freContext != null) {
				freContext.dispatchStatusEventAsync(error, "error");
			}
		} else if (intent.getStringExtra("unregistered") != null) {
			Log.d("as3c2dm", "Unregistered successfully");
			if (freContext != null) {
				freContext.dispatchStatusEventAsync("unregistered",
						"unregistered");
			}
		} else if (registration != null) {
			Log.d("as3c2dm", "Registered successfully");
			if (freContext != null) {
				freContext.dispatchStatusEventAsync(registration, "registered");
			}
		}
	}

	private void handleMessage(Context context, Intent intent) {
		try {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			int icon = R.drawable.icon;
			long when = System.currentTimeMillis();

			String parameters = intent.getStringExtra("parameters");
			CharSequence tickerText = intent.getStringExtra("tickerText");
			CharSequence contentTitle = intent.getStringExtra("contentTitle");
			CharSequence contentText = intent.getStringExtra("contentText");

			Intent notificationIntent = new Intent(context, 
					Class.forName(context.getPackageName() + ".AppEntry"));
			notificationIntent.setData(Uri.parse(parameters));

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			nm.notify(1, notification);
		} catch (Exception e) {
			Log.e("as3c2dm", "Error activating application:", e);
		}
	}
}