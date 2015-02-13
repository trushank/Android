package com.goodsam.goodsamsos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class sad extends Activity {
    public static String text = "We are Sorry. It was a False Alarm.";
    SharedPreferences s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.sad);
	((TextView) findViewById(R.id.sad_text)).setText(text);
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	s.edit().putString("screen", "unknown").commit();
	SecureStorage.msg_sent = false;
    }

    public void sad_close(View v) {
	Intent intent = new Intent(Intent.ACTION_MAIN);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		| Intent.FLAG_ACTIVITY_CLEAR_TOP);
	intent.addCategory(Intent.CATEGORY_HOME);
	startActivity(intent);
	System.exit(1);
    }

}