package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GoodSamSOSActivity extends Activity {

    SharedPreferences settings;
    static String screen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	settings = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	screen = settings.getString("screen", "unknown");
	SecureStorage.msg_sent = false;
	if (settings.getString("login", "0").equals("1")) {
	    Intent intent1 = new Intent(this, MyService.class);
	    startService(intent1);
	}

	// Run Splash screen for required time
	Thread splashThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    int waited = 0;
		    while (waited < 2000) {
			sleep(100);
			waited += 100;
		    }
		} catch (InterruptedException e) {
		    Log.e("SLASH", e.toString());
		    Toast.makeText(getApplicationContext(), e.toString(), 0);
		} finally {
		    // Check if connection is possible to server

		    if (checkConnection()) {

			if (screen.equals("unknown")) {
			    // Check if the phone is logged in
			    if (settings.getString("login", "0").equals("0")) {
				startActivity(new Intent(
					"com.goodsam.goodsamsos.LoginActivity"));

			    }
			    // If logged in assume emergency and start emergency
			    // screen
			    else
				startActivity(new Intent(
					"com.goodsam.goodsamsos.Emergency"));
			} else
			    startActivity(new Intent("com.goodsam.goodsamsos."
				    + screen));

		    }
		    // No connection found, show error
		    else {
			sad.text = "Connection Error";
			startActivity(new Intent("com.goodsam.goodsamsos.sad"));
		    }
		}

	    }
	};
	splashThread.start();

    }

    @Override
    protected void onPause() {
	// Kill Activity once off the screen
	super.onPause();
	finish();
    }

    public boolean checkConnection() {

	try {
	    // Add attributes to post msg
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    nameValuePairs.add(new BasicNameValuePair("gsid", settings
		    .getString("gsid", "0")));
	    String a = new String();

	    BufferedReader rd = Poster.send(SecureStorage.page_alive,
		    nameValuePairs);

	    if (rd.readLine().contains("alive")) {
		if (rd.readLine().contains("true"))
		    return true;
		else {
		    settings.edit().clear().commit();
		    screen = "LoginActivity";
		    return true;
		}
	    } else {
		Log.e("SPLASH", "else");
		while (!(a = rd.readLine()).equals(""))
		    Log.w("Splash", a);

	    }
	    return false;

	} catch (Exception e1) {
	    Log.e("SPLASH", e1.toString());
	    return false;
	}
    }
}