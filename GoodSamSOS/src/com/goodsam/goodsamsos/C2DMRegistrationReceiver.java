package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

public class C2DMRegistrationReceiver extends BroadcastReceiver {
    SharedPreferences s;

    @Override
    public void onReceive(Context context, Intent intent) {
	s = context
		.getSharedPreferences("details", Context.MODE_WORLD_READABLE);
	String action = intent.getAction();
	Log.w("C2DM", "Registration Receiver called");
	if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
	    Log.w("C2DM", "Received registration ID");
	    final String registrationId = intent
		    .getStringExtra("registration_id");
	    String error = intent.getStringExtra("error");

	    Log.d("C2DM", "dmControl: registrationId = " + registrationId
		    + ", error = " + error);
	    String deviceId = Secure.getString(context.getContentResolver(),
		    Secure.ANDROID_ID);
	    Toast.makeText(context, "C2DMID Updated", 1).show();
	    // createNotification(context, registrationId);
	    sendRegistrationIdToServer(deviceId, registrationId);
	    // Also save it in the preference to be able to show it later
	    saveRegistrationId(context, registrationId);
	}
    }

    private void saveRegistrationId(Context context, String registrationId) {
	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(context);
	Editor edit = prefs.edit();
	edit.putString(C2DMClientActivity.AUTH, registrationId);
	edit.commit();
    }

    public void createNotification(Context context, String registrationId) {
	NotificationManager notificationManager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	Notification notification = new Notification(R.drawable.icon,
		"Registration successful", System.currentTimeMillis());
	// Hide the notification after its selected
	notification.flags |= Notification.FLAG_AUTO_CANCEL;

	Intent intent = new Intent(context, RegistrationResultActivity.class);
	intent.putExtra("registration_id", registrationId);
	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
		intent, 0);
	notification.setLatestEventInfo(context, "Registration",
		"Successfully registered", pendingIntent);
	notificationManager.notify(0, notification);
    }

    // Incorrect usage as the receiver may be canceled at any time
    // do this in an service and in an own thread
    public void sendRegistrationIdToServer(String deviceId,
	    String registrationId) {
	Log.d("C2DM", "Sending registration ID to my application server");
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    // Get the deviceID
	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs
		    .add(new BasicNameValuePair("c2dm_id", registrationId));

	    BufferedReader rd = Poster.send(SecureStorage.page_update_c2dm,
		    nameValuePairs);

	    String line = "";
	    while ((line = rd.readLine()) != null) {
		Log.e("HttpResponse", line);
	    }
	} catch (Exception e) {

	    e.printStackTrace();
	}
    }
}