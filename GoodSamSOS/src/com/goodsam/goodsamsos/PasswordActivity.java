package com.goodsam.goodsamsos;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.password);
	SharedPreferences s = getSharedPreferences("details", 2);

	TextView email = (TextView) findViewById(R.id.email1);
	email.setText(s.getString("email", "not found"));

    }

    public void loc(View v) {

	String names[] = { "email", "pwd1" };
	String values[] = new String[2];
	values[0] = ((TextView) findViewById(R.id.email1)).getText().toString();
	values[1] = ((TextView) findViewById(R.id.pass)).getText().toString();
	TextView pass1 = (TextView) findViewById(R.id.pass1);
	if (values[0].length() < 3 || !values[0].contains("@")
		|| !values[0].contains(".com")) {
	    Toast.makeText(this, "Check Email Id", Toast.LENGTH_LONG).show();
	    return;
	}
	if (values[1].length() < 6 || values[1].length() < 6) {
	    Toast.makeText(this, "Check Password", Toast.LENGTH_LONG).show();
	    return;
	}
	if (!values[1].toString().equals(pass1.getText().toString())) {
	    Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG)
		    .show();
	    return;
	}
	SharedPreferences s = getSharedPreferences("details", 2);
	for (int i = 0; i < names.length; i++) {
	    s.edit().putString(names[i], values[i]).commit();
	}

	startActivity(new Intent("com.goodsam.goodsamsos.CheckDetailsActivity"));
	finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK)

	    startActivity(new Intent(
		    "com.goodsam.goodsamsos.AddContactsActivity"));
	finish();
	return super.onKeyDown(keyCode, event);
    }

    public void password_back(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.AddContactsActivity"));

	finish();
    }

}