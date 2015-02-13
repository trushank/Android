package com.goodsam.goodsamsos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CheckDetailsActivity extends Activity {
    SharedPreferences s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.checkdetails);
	s = getSharedPreferences("details", 2);
	((TextView) findViewById(R.id.name12)).setText(s.getString("fname",
		"not found") + " " + s.getString("lname", "not found"));
	((TextView) findViewById(R.id.dob12)).setText(s.getString("dd",
		"not found")
		+ "/"
		+ s.getString("mm", "not found")
		+ "/"
		+ s.getString("yy", "not found"));
	((TextView) findViewById(R.id.sex12)).setText(s.getString("gender",
		"not found"));
	((TextView) findViewById(R.id.mob12)).setText(s.getString("cell_no",
		"not found"));
	((TextView) findViewById(R.id.bgroup12)).setText(s.getString("blood",
		"not found"));
	((TextView) findViewById(R.id.address12)).setText(s.getString("addr",
		"not found"));
	((TextView) findViewById(R.id.email12)).setText(s.getString("email",
		"not found"));
	((TextView) findViewById(R.id.con112)).setText(s.getString("emer1name",
		"not found"));
	((TextView) findViewById(R.id.con212)).setText(s.getString("emer2name",
		"not found"));
	((TextView) findViewById(R.id.con312)).setText(s.getString("emer3name",
		"not found"));
	((TextView) findViewById(R.id.con412)).setText(s.getString("emer4name",
		"not found"));
	((TextView) findViewById(R.id.con512)).setText(s.getString("emer5name",
		"not found"));
	((TextView) findViewById(R.id.num112)).setText(s.getString(
		"emer1contact", "not found"));
	((TextView) findViewById(R.id.num212)).setText(s.getString(
		"emer2contact", "not found"));
	((TextView) findViewById(R.id.num312)).setText(s.getString(
		"emer3contact", "not found"));
	((TextView) findViewById(R.id.num412)).setText(s.getString(
		"emer4contact", "not found"));
	((TextView) findViewById(R.id.num512)).setText(s.getString(
		"emer5contact", "not found"));

    }

    public void check_back(View v) {
	if (s.getString("login", "0").equals("0"))
	    startActivity(new Intent("com.goodsam.goodsamsos.PasswordActivity"));
	else
	    startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
	finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK)
	    if (s.getString("login", "0").equals("0"))
		startActivity(new Intent(
			"com.goodsam.goodsamsos.PasswordActivity"));
	    else
		startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
	finish();
	return super.onKeyDown(keyCode, event);
    }

    public void confirm(View v) {
	SharedPreferences s = getSharedPreferences("details", 2);
	if (s.getString("login", "unknown").equals("1")) {
	    Toast.makeText(getApplicationContext(),
		    "GSID: " + s.getString("gsid", "unknown"), 0);
	    startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
	    finish();
	} else {
	    String names[] = { "fname", "lname", "dd", "mm", "yy", "gender",
		    "cell_no", "email", "pwd1", "blood", "addr", "emer1name",
		    "emer1contact", "emer2name", "emer2contact", "emer3name",
		    "emer3contact", "emer4name", "emer4contact", "emer5name",
		    "emer5contact" };
	    String values[] = new String[names.length];
	    for (int i = 0; i < names.length; i++)
		values[i] = s.getString(names[i], "error");
	    String gsid = Poster.postData(names, values);

	    if (!gsid.contains("error")) {
		s.edit().putString("gsid", gsid).commit();
		startActivity(new Intent("com.goodsam.goodsamsos.Activity2"));
		finish();
	    } else
		Toast.makeText(getApplicationContext(), "Error, Try Again.", 1)
			.show();
	}
    }
}