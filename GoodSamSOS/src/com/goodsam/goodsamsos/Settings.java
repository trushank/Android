package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Settings extends Activity {
    SharedPreferences s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.settings);
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);

	final ListView listView = (ListView) findViewById(R.id.listView1);
	String[] values = new String[] { "View details", "Logout", "View Help",
		"Update C2DM_ID", "View my location", "Exit" };
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_1, android.R.id.text1, values);

	// Assign adapter to ListView
	listView.setAdapter(adapter);
	listView.setOnItemClickListener(new OnItemClickListener() {

	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		if (listView.getItemAtPosition(position).toString()
			.contains("Log")) {
		    askLog();
		} else if (listView.getItemAtPosition(position).toString()
			.contains("detail")) {
		    startActivity(new Intent(
			    "com.goodsam.goodsamsos.CheckDetailsActivity"));
		} else if (listView.getItemAtPosition(position).toString()
			.contains("C2DM")) {
		    Toast.makeText(getApplicationContext(), "Updating C2DM", 0);
		    Log.w("C2DM", "start registration process");
		    Intent intent = new Intent(
			    "com.google.android.c2dm.intent.REGISTER");
		    intent.putExtra("app", PendingIntent.getBroadcast(
			    Settings.this, 0, new Intent(), 0));

		    intent.putExtra("sender", "goodsamsos@gmail.com");
		    startService(intent);
		} else if (listView.getItemAtPosition(position).toString()
			.contains("location")) {
		    startActivity(new Intent("com.goodsam.goodsamsos.Map"));
		} else if (listView.getItemAtPosition(position).toString()
			.contains("Exit")) {
		    ask();

		} else if (listView.getItemAtPosition(position).toString()
			.contains("View Help")) {
		    Intent intent = new Intent(
			    android.content.Intent.ACTION_VIEW, Uri
				    .parse("http://goodsam.in/howitworks.php"));
		    startActivity(intent);

		}

	    }
	});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {

	case R.id.text:
	    startActivity(new Intent("com.goodsam.goodsamsos.Search"));
	    break;

	}
	return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu, menu);
	return true;

    }

    public void loggout() {
	try {
	    Log.w("Inside", "In");
	    SecureStorage.sam = false;
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));

	    BufferedReader br = Poster.send(SecureStorage.page_logout,
		    nameValuePairs);
	    String res = null;

	    if ((res = br.readLine()).contains("success")) {
		Toast.makeText(getApplicationContext(), res, 1).show();
		s.edit().clear().commit();
		startActivity(new Intent("com.goodsam.goodsamsos.LoginActivity"));
		finish();
	    } else
		while (!(res = br.readLine()).equals("")) {
		    Log.w("Response", res);
		}

	} catch (Exception e) {
	    Log.w("Loggout", e.toString());

	}
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {

	    ask();

	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    startActivity(new Intent("com.goodsam.goodsamsos.Search"));
	}
	return super.onKeyDown(keyCode, event);
    }

    public void ask() {
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
		    // Yes button clicked
		    Intent intent = new Intent(Intent.ACTION_MAIN);
		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    intent.addCategory(Intent.CATEGORY_HOME);
		    startActivity(intent);
		    System.exit(1);
		    break;

		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage("Exit App?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
    }

    public void askLog() {
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
		    // Yes button clicked
		    loggout();
		    break;

		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage("Logout?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
    }
}