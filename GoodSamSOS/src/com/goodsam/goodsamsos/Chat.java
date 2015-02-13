package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Chat extends Activity {
    SharedPreferences s;
    Timer timer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.chat);
	((EditText) findViewById(R.id.chat_box)).setKeyListener(null);
	load();
	s = getSharedPreferences("details", MODE_WORLD_READABLE);
	int z = ((TextView) findViewById(R.id.chat_box)).getText().toString()
		.lastIndexOf("***");
	if (z == -1)
	    z = 0;
	Log.w("Index", Integer.toString(z));
	((EditText) findViewById(R.id.chat_box)).setSelection(z);
	timer = new Timer();
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {

		TimerMethod();

	    }

	    private void TimerMethod() {

		runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
			Log.w("Timer", "Tick");
			load();

		    }
		});

	    }
	}, 0, 10000);
    }

    public void chat_exit(View v) {
	if (SecureStorage.sam)
	    startActivity(new Intent("com.goodsam.goodsamsos.GoodSamScreen"));
	else
	    startActivity(new Intent("com.goodsam.goodsamsos.VictimScreen"));
	timer.cancel();
	finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    if (SecureStorage.sam)
		startActivity(new Intent("com.goodsam.goodsamsos.GoodSamScreen"));
	    else
		startActivity(new Intent("com.goodsam.goodsamsos.VictimScreen"));
	    timer.cancel();
	    finish();
	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    startActivity(new Intent("com.goodsam.goodsamsos.Search"));
	}
	return super.onKeyDown(keyCode, event);

    }

    public void load() {
	try {
	    Log.w("Leave", "In");
	    ArrayList<NameValuePair> nameValuePairs = new

	    ArrayList<NameValuePair>();

	    if (SecureStorage.sam)
		nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
			"vic_gsid", "unknown")));
	    else
		nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
			"gsid", "unknown")));

	    BufferedReader br = Poster.send(SecureStorage.page_chat,
		    nameValuePairs);

	    String rep = new String();
	    ((TextView) findViewById(R.id.chat_box)).setText(" ");
	    while (!(rep = br.readLine()).equals("done")) {
		((TextView) findViewById(R.id.chat_box)).append("\n" + rep);
		((TextView) findViewById(R.id.chat_box))
			.bringPointIntoView(((TextView) findViewById(R.id.chat_box))
				.getText().toString().indexOf("***"));

	    }

	} catch (Exception e1) {
	    Log.w("Load Chat", e1.toString());
	}
    }

    public void chat_send(View v) {
	send_chat();
    }

    public void send_chat() {
	String msg = ((TextView) findViewById(R.id.chat_text)).getText()
		.toString();
	((TextView) findViewById(R.id.chat_text)).setText("");
	if (msg.length() > 0) {
	    try {
		Log.w("Leave", "In");
		ArrayList<NameValuePair> nameValuePairs = new

		ArrayList<NameValuePair>();

		SharedPreferences s = getSharedPreferences("details",
			MODE_WORLD_READABLE);

		nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
			"gsid", "unknown")));
		nameValuePairs.add(new BasicNameValuePair("chat", s.getString(
			"fname", "unknown") + ": " + msg));

		BufferedReader br = Poster.send(SecureStorage.page_chat_add,
			nameValuePairs);
		String rep = new String();

		if (br.readLine().contains("Chat"))
		    load();
		else
		    while (!(rep = br.readLine()).equals(""))
			Log.w("CHAT", rep);

	    } catch (Exception e1) {
		Log.w("Chat", e1.toString());
	    }
	}
    }

}