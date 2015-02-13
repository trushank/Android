package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class GoodSamScreen extends MapActivity {
    String lat = new String();
    String lng = new String();
    String mylat = new String();
    String mylng = new String();
    String chat = "";
    MapView mapView;
    MapController mc;
    GeoPoint p;
    GeoPoint p1;
    SharedPreferences s;
    Timer timer;
    boolean photo = true;
    Calendar cal;
    int time;

    class MapOverlay extends com.google.android.maps.Overlay {

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
		long when) {
	    super.draw(canvas, mapView, shadow);

	    // ---translate the GeoPoint to screen pixels---
	    Point screenPts = new Point();

	    mapView.getProjection().toPixels(p1, screenPts);

	    // ---add the marker---
	    Bitmap bmp = BitmapFactory.decodeResource(getResources(),
		    R.drawable.reddot);

	    canvas.drawBitmap(bmp, screenPts.x - 5, screenPts.y - 12, null);

	    mapView.getProjection().toPixels(p, screenPts);

	    // ---add the marker---
	    bmp = BitmapFactory.decodeResource(getResources(),
		    R.drawable.yellowdot);

	    canvas.drawBitmap(bmp, screenPts.x - 5, screenPts.y - 12, null);
	    return true;
	}
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.goodsamscreen);
	goodsamscreen_Load();
	cal = Calendar.getInstance();
	time = cal.get(Calendar.MINUTE);
	Log.w("Timer onstart", Integer.toString(time));
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	s.edit().putString("screen", "GoodSamScreen").commit();
	((ImageView) findViewById(R.id.goodsamscreen_photo)).requestFocus();

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
			cal = Calendar.getInstance();

			if ((cal.get(Calendar.MINUTE) - time) >= 1) {
			    goodsamscreen_Load();
			    time = cal.get(Calendar.MINUTE);
			    Log.w("Timer time", Integer.toString(time));
			}

		    }
		});
	    }
	}, 0, 20000);
    }

    public Object fetch(String address) throws MalformedURLException,
	    IOException {
	URL url = new URL(address);
	Object content = url.getContent();
	return content;
    }

    private Drawable ImageOperations(Context ctx, String url) {
	try {
	    InputStream is = (InputStream) this.fetch(url);
	    Drawable d = Drawable.createFromStream(is, "src");
	    return d;
	} catch (MalformedURLException e) {
	    return null;
	} catch (IOException e) {
	    return null;
	}
    }

    public void goodsamscreen_map(View v) {
	Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		Uri.parse("http://maps.google.com/maps?saddr=" + lat + ","
			+ lng + "&daddr=" + mylat + "," + mylng));
	startActivity(intent);
    }

    public void goodsamscreen_refresh(View v) {
	goodsamscreen_Load();
    }

    public void goodsamscreen_save(View v) {
	try {
	    Log.w("Inside", "In");
	    ArrayList<NameValuePair> nameValuePairs = new

	    ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("help_code",
		    ((TextView) findViewById(R.id.goodsamscreen_code))
			    .getText().toString()));

	    BufferedReader br = Poster.send(SecureStorage.page_sam_save,
		    nameValuePairs);
	    String rep = new String();
	    if (br.readLine().contains("provide")) {
		Toast.makeText(getApplicationContext(),
			"Thank you for your help", 0).show();

		timer.cancel();
		startActivity(new Intent("com.goodsam.goodsamsos.Settings"));
		finish();
	    } else {
		Toast.makeText(getApplicationContext(), "Incorrect Code", 0)
			.show();
		while (!(rep = br.readLine()).equals("")) {
		    Log.w("Save Response", rep);
		}
	    }
	} catch (Exception e1) {
	    Log.w("Save:", e1.toString());
	}
    }

    public void goodsamscreen_Load() {
	try {
	    Log.w("Goodsam Load", "In");
	    ArrayList<NameValuePair> nameValuePairs = new

	    ArrayList<NameValuePair>();
	    s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    BufferedReader br = Poster.send(SecureStorage.page_goodsam,
		    nameValuePairs);
	    String b = br.readLine();
	    if (b.contains("start")) {
		String url = SecureStorage.SERVER_IP + br.readLine();
		((TextView) findViewById(R.id.goodsamscreen_name)).setText(br
			.readLine());
		mylat = br.readLine();
		mylng = br.readLine();
		lat = br.readLine();
		lng = br.readLine();
		p = new GeoPoint((int) (Double.parseDouble(lat) * 1E6),
			(int) (Double.parseDouble(lng) * 1E6));
		p1 = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
			(int) (Double.parseDouble(mylng) * 1E6));
		((TextView) findViewById(R.id.goodsamscreen_sex)).setText(br
			.readLine());
		((TextView) findViewById(R.id.goodsamscreen_number)).setText(br
			.readLine());
		((TextView) findViewById(R.id.goodsamscreen_distance))
			.setText(br.readLine());
		((TextView) findViewById(R.id.goodsamscreen_emergency))
			.setText(br.readLine());
		((TextView) findViewById(R.id.goodsamscreen_chat)).setText(br
			.readLine());
		if (photo) {
		    ImageView Image01 = (ImageView) findViewById(R.id.goodsamscreen_photo);
		    Log.w("URL", url);
		    Drawable image = ImageOperations(this, url);
		    Image01.setImageDrawable(image);
		    photo = false;
		}
		if (!chat
			.equals(((TextView) findViewById(R.id.goodsamscreen_chat))
				.getText().toString())) {
		    chat = ((TextView) findViewById(R.id.goodsamscreen_chat))
			    .getText().toString();
		    Toast.makeText(getApplicationContext(), chat, 1).show();
		}

	    } else if (b.contains("Error")) {
		Toast.makeText(getApplicationContext(), "Emergency Resolved", 1)
			.show();
		sad.text = "Emergency Resolved";
		startActivity(new Intent("com.goodsam.goodsamsos.sad"));
		finish();
	    } else {
		String a = new String();
		while (!(a = br.readLine()).equals(""))
		    Log.w("response", a);
		Toast.makeText(getApplicationContext(), "Connection Error", 1)
			.show();

	    }
	} catch (Exception e1) {
	    Log.w("Goodsam Load:", e1.toString());
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
		String addr = new String();
		for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
		    if (i == 0)
			addr = address.getAddressLine(i);
		    else
			addr = addr + " ," + address.getAddressLine(i);

		}
		((TextView) findViewById(R.id.goodsamscreen_address))
			.setText(addr);

	    }
	    Log.w("Geo:", sb.toString());
	} catch (Exception e) {
	    Log.w("Geo121:", e.toString());
	    ((TextView) findViewById(R.id.goodsamscreen_address))
		    .setText("Address Not Available");

	} finally {
	    Log.w("Goodsam Load", "Done");
	}
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
	// TODO Auto-generated method stub
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu, menu);
	return true;

    }

    public void goodsamscreen_chat(View v) {

	startActivity(new Intent("com.goodsam.goodsamsos.Chat"));
	finish();
    }

    public void goodsamscreen_leave(View v) {
	ask();
    }

    public void goodsamscreen_call(View v) {
	String posted_by = ((TextView) findViewById(R.id.goodsamscreen_number))
		.getText().toString();

	String uri = "tel:" + posted_by.trim();
	Intent intent = new Intent(Intent.ACTION_CALL);
	intent.setData(Uri.parse(uri));
	startActivity(intent);
    }

    public void reject() {
	try {
	    Log.w("Leave", "In");

	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    SharedPreferences s = getSharedPreferences("details",
		    MODE_WORLD_READABLE);
	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "unknown")));
	    Log.w("GSID", s.getString("gsid", "unknown"));
	    BufferedReader br = Poster.send(SecureStorage.page_sam_cancel,
		    nameValuePairs);
	    String rep = br.readLine();
	    if (rep.contains("success")) {
		timer.cancel();
		s.edit().putString("screen", "unknown").commit();
		sad.text = "Bad Samaritan";
		startActivity(new Intent("com.goodsam.goodsamsos.sad"));
		finish();
	    } else
		while (!(rep = br.readLine()).equals("")) {
		    Log.w("Leave Response", rep);

		}
	} catch (Exception e1) {
	    Log.w("Leave:", "1234");
	}
    }

    public void ask() {
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
		    // Yes button clicked
		    reject();
		    break;

		case DialogInterface.BUTTON_NEGATIVE:
		    // No button clicked
		    break;
		}
	    }
	};

	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage("Are you sure?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    ask();
	} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    startActivity(new Intent("com.goodsam.goodsamsos.Search"));
	}
	return super.onKeyDown(keyCode, event);

    }

    @Override
    protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
    }

    public void open_map1(View v) {
	setContentView(R.layout.goodsamscreen1);
	goodsamscreen_Load();

	mapView = (MapView) findViewById(R.id.mapView2);

	mapView.requestFocus();
	LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
	@SuppressWarnings("deprecation")
	View zoomView = mapView.getZoomControls();

	zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	mapView.displayZoomControls(true);

	// ---Add a location marker---
	MapOverlay mapOverlay = new MapOverlay();
	p = new GeoPoint((int) (Double.parseDouble(lat) * 1E6),
		(int) (Double.parseDouble(lng) * 1E6));
	p1 = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
		(int) (Double.parseDouble(mylng) * 1E6));
	mc = mapView.getController();
	mc.animateTo(p1);
	mc.setZoom(15);
	List<Overlay> listOfOverlays = mapView.getOverlays();
	mapView.setSatellite(false);

	listOfOverlays.clear();

	listOfOverlays.add(mapOverlay);

	mapView.invalidate();

    }

    public void open_image1(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.GoodSamScreen"));
	finish();
    }

    public void gotome2(View v) {
	mc.animateTo(p1);
    }

    public void gotosam2(View v) {
	mc.animateTo(p);
    }

    @Override
    protected void onDestroy() {
	timer.cancel();
	super.onDestroy();
    }

}