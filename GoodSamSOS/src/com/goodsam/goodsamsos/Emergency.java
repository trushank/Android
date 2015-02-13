package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Emergency extends Activity {
    ProgressDialog dialog;
    ProgressThread progThread;
    ProgressBar progDialog;
    int typeBar; // Determines type progress bar: 0 = spinner, 1 = horizontal
    int delay = 1000; // Milliseconds of delay in the update loop
    int maxBarValue = 10; // Maximum value of horizontal progress bar
    public static String emerg = "Unknown";
    Boolean done = false;
    SharedPreferences s;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.emergency);
	progDialog = (ProgressBar) findViewById(R.id.progressBar1);
	progDialog.setMax(maxBarValue);
	progThread = new ProgressThread(handler);
	progThread.start();
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);

    }

    final Handler handler = new Handler() {
	public void handleMessage(Message msg) {

	    int total = msg.getData().getInt("total");
	    progDialog.setProgress(total);
	    if (total >= 10) {
		progThread.setState(ProgressThread.DONE);
		if (!done)
		    sendAlert(getBaseContext());
	    }
	}
    };

    // Inner class that performs progress calculations on a second thread.
    // Implement
    // the thread by subclassing Thread and overriding its run() method. Also
    // provide
    // a setState(state) method to stop the thread gracefully.
    private class ProgressThread extends Thread {

	// Class constants defining state of the thread
	final static int DONE = 0;
	final static int RUNNING = 1;

	Handler mHandler;
	int mState;
	int total;

	// Constructor with an argument that specifies Handler on main thread
	// to which messages will be sent by this thread.
	ProgressThread(Handler h) {
	    mHandler = h;
	}

	// Override the run() method that will be invoked automatically when
	// the Thread starts. Do the work required to update the progress bar on
	// this
	// thread but send a message to the Handler on the main UI thread to
	// actually
	// change the visual representation of the progress. In this example we
	// count
	// the index total down to zero, so the horizontal progress bar will
	// start full and
	// count down.@Override
	public void run() {
	    mState = RUNNING;
	    total = 0;
	    while (mState == RUNNING) {
		// The method Thread.sleep throws an InterruptedException if
		// Thread.interrupt()
		// were to be issued while thread is sleeping; the exception
		// must be caught.
		try {
		    // Control speed of update (but precision of delay not
		    // guaranteed)
		    Thread.sleep(delay);
		} catch (InterruptedException e) {
		    Log.e("ERROR", "Thread was Interrupted");
		}

		// Send message (with current value of total as data) to Handler
		// on UI thread
		// so that it can update the progress bar.
		Message msg = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt("total", total);
		msg.setData(b);
		mHandler.sendMessage(msg);

		total++; // Count down
	    }
	}

	// Set current state of thread (use state=ProgressThread.DONE to stop
	// thread)
	public void setState(int state) {
	    mState = state;
	}
    }

    public void emergency_natural(View v) {
	emerg = "Natural";
	done = true;
	progThread.setState(ProgressThread.DONE);
	sendAlert(v.getContext());

    }

    public void emergency_medical(View v) {

	emerg = "Medical";
	done = true;
	progThread.setState(ProgressThread.DONE);
	sendAlert(v.getContext());

    }

    public void emergency_criminal(View v) {
	emerg = "Criminal";
	done = true;
	progThread.setState(ProgressThread.DONE);
	sendAlert(v.getContext());

    }

    public void emergency_other(View v) {
	done = true;
	emerg = "Unknown";
	progThread.setState(ProgressThread.DONE);
	sendAlert(v.getContext());
    }

    public void sendAlert(final Context c) {
	dialog = ProgressDialog.show(Emergency.this, "Raising Alert",
		"Help is on its way...", true);
	Thread thread = new Thread(new Runnable() {

	    public void run() {
		alerting(c);
		runOnUiThread(new Runnable() {

		    @Override
		    public void run() {
			if (dialog.isShowing()) {
			}

		    }

		});
	    }

	});
	thread.start();

    }

    public void alerting(Context c) {

	try {

	    Log.w("Sending Alert", "In");

	    s.edit().putString("emertype", emerg).commit();
	    SecureStorage.sam = false;
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("emer_type", emerg));
	    nameValuePairs.add(new BasicNameValuePair("sam_type", "victim"));

	    final BufferedReader br = Poster.send(SecureStorage.page_emergency,
		    nameValuePairs);

	    runOnUiThread(new Runnable() {
		public void run() {
		    try {
			String res = null;

			if ((res = br.readLine()).contains("provide")) {
			    Toast.makeText(getApplicationContext(),
				    "Help will be provided", 1).show();
			    dialog.dismiss();
			    startActivity(new Intent(
				    "com.goodsam.goodsamsos.VictimScreen"));
			    finish();
			} else
			    while (!(res = br.readLine()).equals("")) {
				Log.w("Response", res);
				Toast.makeText(getApplicationContext(),
					"Error Try again", 1).show();
			    }
		    } catch (Exception e) {
			Log.w("Error in thread", e.toString());
		    }
		}
	    });

	} catch (Exception e) {
	    Log.w("Error", e.toString());

	}

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    Toast.makeText(getApplicationContext(),
		    "Alert Cancelled. Opening Settings Screen", 1).show();
	    done = true;
	    progThread.setState(ProgressThread.DONE);
	    startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
	    try {
		finish();
	    } catch (Exception e) {
		Log.w("Error Cancelling", e.toString());
	    }
	}
	return super.onKeyDown(keyCode, event);
    }

    public void emergency_cancel(View v) {
	Toast.makeText(getApplicationContext(),
		"Alert Cancelled. Opening Settings Screen", 1).show();

	done = true;
	progThread.setState(ProgressThread.DONE);
	startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
	try {
	    finish();
	} catch (Exception e) {
	    Log.w("Error Cancelling", e.toString());
	}
    }

}