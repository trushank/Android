package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class VictimScreen extends MapActivity {
    String lat = new String();
    String lng = new String();
    String mylat = new String();
    String mylng = new String();
    String addr = "unknown";
    String sams = "";
    String chat = "";
    SharedPreferences s;
    Boolean change_loc = false;
    boolean helper = false;
    Timer timer;
    MapView mapView;
    MapController mc;
    GeoPoint p;
    GeoPoint p1;
    Calendar cal;
    int time;

    class MapOverlay extends com.google.android.maps.Overlay {
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
	    if (event.getAction() == 1) {
		if (change_loc) {
		    p = mapView.getProjection().fromPixels((int) event.getX(),
			    (int) event.getY());
		    mc.animateTo(p);
		    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    switch (which) {
			    case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				change_loc = false;
				postData(p.getLatitudeE6() / 1E6,
					p.getLongitudeE6() / 1E6);
				mylat = Double
					.toString(p.getLatitudeE6() / 1E6);
				mylng = Double
					.toString(p.getLongitudeE6() / 1E6);
				break;

			    case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			    }
			}
		    };

		    AlertDialog.Builder builder = new AlertDialog.Builder(
			    VictimScreen.this);

		    builder.setMessage("Set Location?")
			    .setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener)
			    .show();

		    return true;
		} else
		    return false;
	    } else
		return false;

	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
		long when) {
	    super.draw(canvas, mapView, shadow);

	    // ---translate the GeoPoint to screen pixels---
	    Point screenPts = new Point();

	    mapView.getProjection().toPixels(p, screenPts);

	    // ---add the marker---
	    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
		    R.drawable.reddot);

	    canvas.drawBitmap(bmp, screenPts.x - 5, screenPts.y - 12, null);
	    if (helper) {
		mapView.getProjection().toPixels(p1, screenPts);

		// ---add the marker---
		bmp = BitmapFactory.decodeResource(getResources(),
			R.drawable.yellowdot);

		canvas.drawBitmap(bmp, screenPts.x - 5, screenPts.y - 12, null);
	    }
	    return true;
	}
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.victim);
	try {
	    mapView = (MapView) findViewById(R.id.mapView);
	    LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
	    @SuppressWarnings("deprecation")
	    View zoomView = mapView.getZoomControls();

	    zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
		    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    mapView.displayZoomControls(true);

	    s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	    s.edit().putString("screen", "VictimScreen").commit();
	    // ---Add a location marker---
	    MapOverlay mapOverlay = new MapOverlay();
	    cal = Calendar.getInstance();
	    time = cal.get(Calendar.MINUTE);
	    load();
	    if (SecureStorage.msg_sent == false) {
		SecureStorage.msg_sent = true;
		Log.w("Victim",
			"Sending Msg" + s.getString("emer1contact", "unknown"));
		// sendMsg("5556");

		sendMsg(s.getString("emer1contact", "unknown"));
		sendMsg(s.getString("emer2contact", "unknown"));
		sendMsg(s.getString("emer3contact", "unknown"));
		sendMsg(s.getString("emer4contact", "unknown"));
		sendMsg(s.getString("emer5contact", "unknown"));
	    }
	    p = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
		    (int) (Double.parseDouble(mylng) * 1E6));
	    if (helper)
		p1 = new GeoPoint((int) (Double.parseDouble(lat) * 1E6),
			(int) (Double.parseDouble(lng) * 1E6));
	    Log.w("vic", "4");
	    mc = mapView.getController();
	    mc.animateTo(p);
	    mc.setZoom(15);

	    List<Overlay> listOfOverlays = mapView.getOverlays();

	    listOfOverlays.clear();

	    listOfOverlays.add(mapOverlay);

	    mapView.invalidate();

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
			    cal = Calendar.getInstance();

			    if ((cal.get(Calendar.MINUTE) - time) >= 1) {
				load();
				time = cal.get(Calendar.MINUTE);
				Log.w("Timer time", Integer.toString(time));
			    }

			}
		    });

		}
	    }, 0, 20000);

	} catch (Exception e) {
	    Log.w("Victim Error", e.toString());
	}
    }

    @Override
    protected void onDestroy() {
	try {
	    timer.cancel();
	} catch (Exception e) {
	}
	super.onDestroy();
    }

    public void victim_map(View v) {
	Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		Uri.parse("http://maps.google.com/maps?saddr=" + mylat + ","
			+ mylng + "&daddr=" + lat + "," + lng));
	startActivity(intent);

    }

    public void victim_cancel(View v) {
	ask();
    }

    public void victim_chat(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.Chat"));
	finish();
    }

    public void victim_refresh(View v) {
	load();
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

    public void load() {
	try {

	    Log.w("Loading", "In");

	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));

	    BufferedReader br = Poster.send(SecureStorage.page_victim_screen,
		    nameValuePairs);
	    String b = br.readLine();
	    if (b.contains("start")) {
		Log.w("Vic", "getting details");
		((TextView) findViewById(R.id.victim_code)).setText(br
			.readLine());
		mylat = br.readLine();
		mylng = br.readLine();
		lat = br.readLine();
		lng = br.readLine();

		p = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
			(int) (Double.parseDouble(mylng) * 1E6));

		((TextView) findViewById(R.id.victim_sams)).setText(br
			.readLine());
		((TextView) findViewById(R.id.victim_distance)).setText(br
			.readLine());
		((TextView) findViewById(R.id.victim_number)).setText(br
			.readLine());
		((TextView) findViewById(R.id.victim_chat)).setText(br
			.readLine());
		if (!chat.equals(((TextView) findViewById(R.id.victim_chat))
			.getText().toString())) {
		    chat = ((TextView) findViewById(R.id.victim_chat))
			    .getText().toString();
		    Toast.makeText(getApplicationContext(), chat, 1).show();
		}
		if (!sams.equals(((TextView) findViewById(R.id.victim_sams))
			.getText().toString())) {
		    sams = ((TextView) findViewById(R.id.victim_sams))
			    .getText().toString();
		    Toast.makeText(getApplicationContext(),
			    "Number of Sams is now: " + sams, 1).show();
		}
		if (!(((TextView) findViewById(R.id.victim_sams)).getText()
			.equals("0")))
		    helper = true;
		else
		    helper = false;
		if (!helper)
		    ((ImageButton) findViewById(R.id.victim_map))
			    .setEnabled(false);
		else {
		    ((ImageButton) findViewById(R.id.victim_map))
			    .setEnabled(true);
		    p1 = new GeoPoint((int) (Double.parseDouble(lat) * 1E6),
			    (int) (Double.parseDouble(lng) * 1E6));
		}
	    } else if (b.contains("Error")) {
		try {
		    Toast.makeText(getApplicationContext(),
			    "Emergency Resolved", 1).show();
		    sad.text = "Emergency Resolved";
		    startActivity(new Intent("com.goodsam.goodsamsos.sad"));
		    s.edit().putString("screen", "unknown").commit();
		    finish();
		} catch (Exception e) {
		    Log.w("Victim No emer Error", e.toString());
		}
	    } else {
		Toast.makeText(getApplicationContext(), "Connection Error", 1)
			.show();
		String res = new String();
		while (!(res = br.readLine()).equals(""))
		    Log.w("VicResponse", res);
	    }

	} catch (Exception e1) {
	    Log.w("Victim Screen Loading", e1.toString());

	}
	Geocoder gc = new Geocoder(this, Locale.getDefault());
	try {
	    List<Address> addresses = gc.getFromLocation(
		    Double.parseDouble(mylat), Double.parseDouble(mylng), 1);
	    StringBuilder sb = new StringBuilder();
	    if (addresses.size() > 0) {
		Address address = addresses.get(0);

		sb.append(address.getLocality()).append("\n");
		sb.append(address.getCountryName());
		addr = new String();
		for (int i = 0; i < 2; i++) {
		    if (i == 0)
			addr = address.getAddressLine(i);
		    else
			addr = addr + " ," + address.getAddressLine(i);

		}
		((TextView) findViewById(R.id.victim_address)).setText(addr);

	    }

	    Log.w("Geo:", sb.toString());
	} catch (Exception e) {
	    ((TextView) findViewById(R.id.victim_address))
		    .setText("Address Not Available");
	    Log.w("Geo121:", e.toString());
	}
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    ask();
	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    startActivity(new Intent("com.goodsam.goodsamsos.Search"));
	}
	return super.onKeyDown(keyCode, event);

    }

    public void victim_call(View v) {
	String posted_by = ((TextView) findViewById(R.id.victim_number))
		.getText().toString();

	String uri = "tel:" + posted_by.trim();
	Intent intent = new Intent(Intent.ACTION_CALL);
	intent.setData(Uri.parse(uri));
	startActivity(intent);

    }

    public void ask() {
	final EditText input = new EditText(this);
	input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
		    // Yes button clicked
		    try {
			Log.w("Inside", "In");
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("gsid", s
				.getString("gsid", "unknown")));

			nameValuePairs.add(new BasicNameValuePair("pwd", input
				.getText().toString()));
			BufferedReader br = Poster.send(
				SecureStorage.page_vic_cancel, nameValuePairs);

			String a;
			if ((a = br.readLine()).contains("Incorrect")) {
			    Toast.makeText(getApplicationContext(),
				    "Incorrect Password", 0).show();
			    ask();
			} else if (a.contains("Correct")
				|| a.contains("delete")) {
			    Toast.makeText(getApplicationContext(),
				    "Emergency Canceled", 0).show();
			    timer.cancel();
			    s.edit().putString("screen", "unknown").commit();
			    Intent intent = new Intent(VictimScreen.this,
				    Settings.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);

			    try {
				VictimScreen.this.finish();
			    } catch (Exception e) {
				Log.w("Error", e.toString());
			    }
			}

			else
			    while (!(a = br.readLine()).equals(""))
				Log.w("CANCEL EMER", a);
		    } catch (Exception e) {
			Log.w("VIC_CANCEL", e.toString());
		    }

		    break;

		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	builder.setView(input);

	builder.setMessage("Enter Password to Cancel Alert")
		.setPositiveButton("Done", dialogClickListener)
		.setNegativeButton("Cancel", dialogClickListener).show();
    }

    public void change_loc(View v) {
	change_loc = true;
	Toast.makeText(getApplicationContext(),
		"Tap your correct location on the map", 0).show();
	Log.w("Loc", "called");
    }

    @Override
    protected boolean isRouteDisplayed() {

	return false;
    }

    public void postData(double lat, double lng) {

	Toast.makeText(this, "Posting", Toast.LENGTH_LONG).show();

	try {
	    s.edit().putString("mylat", Double.toString(lat)).commit();
	    s.edit().putString("mylng", Double.toString(lng)).commit();
	    // Add your data
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("lat", Double
		    .toString(lat)));
	    nameValuePairs.add(new BasicNameValuePair("lng", Double
		    .toString(lng)));

	    BufferedReader res = Poster.send(SecureStorage.page_update_loc,
		    nameValuePairs);

	    if (res.readLine().contains("Done")) {
		Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();

	    } else {
		String a;
		while (!(a = res.readLine()).equals("")) {
		    Log.w("LOCATION", a);
		}
	    }
	} catch (ClientProtocolException e) {
	    Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG)
		    .show();
	    Log.w("EXCEPTION1", e.toString());

	} catch (Exception e) {

	    Log.w("EXCEPTION", e.toString());

	}

	Log.w("POST", "POST COMPLETE");

    }

    public void gotome(View v) {
	mc.animateTo(p);
    }

    public void gotosam(View v) {
	if (helper)
	    mc.animateTo(p1);
    }

    public void sendMsg(String number) {
	try {
	    String addr1 = new String();
	    if (addr.length() > 30)
		addr1 = addr.substring(0, 30);
	    else
		addr1 = addr;
	    Log.w("MSG", "Called");
	    Log.w("Number", "MOB:" + number);
	    if ((!number.equalsIgnoreCase("unknown")) && number.length() >= 10) {
		Log.w("MSG", "Entered");
		SmsManager sms = SmsManager.getDefault();

		Log.w("MSG", "Entered1");
		String message = "This is " + s.getString("fname", "unknown")
			+ ". I am in a " + s.getString("emertype", "unknown")
			+ " emergency. I am at " + addr1;
		message = message
			+ ". For details go to http://goo.gl/uUDzI with code: "
			+ Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);
		/*
		 * ArrayList<String> msg = new ArrayList<String>();
		 * msg.add(message);
		 * msg.add("For more info go to http://goo.gl/uUDzI with code: "
		 * + Secure.getString(getContentResolver(), Secure.ANDROID_ID));
		 */
		Log.w("Number", number);
		Log.w("MSG:", message);
		Log.w("MSG LEN", Integer.toString(message.length()));
		// sms.sendMultipartTextMessage(number, null, msg, null, null);
		sms.sendTextMessage(number, null, message, null, null);
		Log.w("MSG", "Entered2");
	    } else
		Log.w("MSG", "UNKNOWN");
	} catch (Exception e) {
	    Log.w("MSG EXCEPTION", e.toString());
	}
    }
}