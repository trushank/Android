package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    String deviceId;
    SharedPreferences s;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.login);
	deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);

	s.edit().clear().commit();

    }

    public void login(View v) {

	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	    nameValuePairs.add(new BasicNameValuePair("email",
		    ((EditText) findViewById(R.id.login_email)).getText()
			    .toString()));
	    nameValuePairs.add(new BasicNameValuePair("pwd",
		    ((EditText) findViewById(R.id.login_password)).getText()
			    .toString()));
	    nameValuePairs.add(new BasicNameValuePair("device_id", deviceId));
	    BufferedReader rd = Poster.send(SecureStorage.page_login,
		    nameValuePairs);
	    String line = "";

	    // Display Success
	    if ((line = rd.readLine()).contains("start")) {
		Toast.makeText(getApplicationContext(),
			"Successfully Signed in", 0).show();

		// Getting and saving details
		s.edit().putString("login", "1").commit();
		s.edit().putString("gsid", rd.readLine()).commit();
		s.edit().putString("fname", rd.readLine()).commit();
		s.edit().putString("lname", rd.readLine()).commit();
		s.edit().putString("gender", rd.readLine()).commit();
		s.edit().putString("dd", rd.readLine()).commit();
		s.edit().putString("mm", rd.readLine()).commit();
		s.edit().putString("yy", rd.readLine()).commit();
		s.edit().putString("cell_no", rd.readLine()).commit();
		s.edit().putString("email", rd.readLine()).commit();
		s.edit().putString("blood", rd.readLine()).commit();
		String addr = "";
		String addr1 = "";
		while (!(addr = rd.readLine()).contains("contacts"))
		    addr1 = addr1 + addr;
		s.edit().putString("addr", addr1).commit();
		s.edit().putString("emer1name", rd.readLine()).commit();
		s.edit().putString("emer1contact", rd.readLine()).commit();
		s.edit().putString("emer2name", rd.readLine()).commit();
		s.edit().putString("emer2contact", rd.readLine()).commit();
		s.edit().putString("emer3name", rd.readLine()).commit();
		s.edit().putString("emer3contact", rd.readLine()).commit();
		s.edit().putString("emer4name", rd.readLine()).commit();
		s.edit().putString("emer4contact", rd.readLine()).commit();
		s.edit().putString("emer5name", rd.readLine()).commit();
		s.edit().putString("emer5contact", rd.readLine()).commit();

		// TODO Remove comment on intent and put on startAct
		// Start service to update location and c2dm
		Toast.makeText(getApplicationContext(), "Updating C2DM", 0)
			.show();
		Log.w("C2DM", "start registration process");
		Intent intent = new Intent(
			"com.google.android.c2dm.intent.REGISTER");
		intent.putExtra("app",
			PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		// Sender currently not used
		intent.putExtra("sender", "goodsamsos@gmail.com");
		startService(intent);

		Intent intent1 = new Intent(this, MyService.class);
		startService(intent1);
		startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
		finish();

	    } else if (line.contains("Invalid")) {
		Toast.makeText(getApplicationContext(),
			"Invalid Username/Password", 0).show();

	    } else
		Toast.makeText(getApplicationContext(), "Connection Error", 0)
			.show();
	    while ((line = rd.readLine()) != null) {
		Log.e("HttpResponse", line);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Log.e("LOGIN", e.toString());
	    Toast.makeText(getApplicationContext(), e.toString(), 0).show();

	}
    }

    public void sign(View v) {

	startActivity(new Intent("com.goodsam.goodsamsos.SignupActivity"));
	finish();
    }

}
