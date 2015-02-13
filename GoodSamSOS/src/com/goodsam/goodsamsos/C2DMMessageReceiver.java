package com.goodsam.goodsamsos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class C2DMMessageReceiver extends BroadcastReceiver {
    static String vicid;
    static String payload = "unknown";

    @Override
    public void onReceive(Context context, Intent intent) {
	String action = intent.getAction();
	Log.w("C2DM", "Message Receiver called");
	if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
	    Log.w("C2DM", "Received message");
	    payload = intent.getStringExtra("payload");
	    vicid = intent.getStringExtra("id");
	    Log.d("C2DM", "dmControl: payload = " + payload);
	    Log.d("C2DM123", "dmControl: payload = " + vicid);
	    SharedPreferences s = context.getSharedPreferences("details",
		    context.MODE_WORLD_WRITEABLE);
	    s.edit().putString("vic_gsid", vicid).commit();
	    // TODO Send this to my application server to get the real data
	    // Lets make something visible to show that we received the message
	    if (payload.equals("1"))
		createNotification(context, payload);

	}
    }

    public void createNotification(Context context, String payload) {

	NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.icon,
		"Emergency!", System.currentTimeMillis());
	notification.defaults = Notification.DEFAULT_ALL;
	// Hide the notification after its selected
	notification.flags |= Notification.FLAG_AUTO_CANCEL;

	Intent intent = new Intent(context, Alert.class);
	intent.removeExtra("vic_gsid");
	intent.putExtra("payload", payload);

	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		| Intent.FLAG_ACTIVITY_CLEAR_TOP);

	// intent.putExtra("vic_gsid", vicid);
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		intent, 0);
	notification.setLatestEventInfo(context, "Your help is required",
		"Tap for info", pendingIntent);
	notificationManager.notify(0, notification);

    }

}