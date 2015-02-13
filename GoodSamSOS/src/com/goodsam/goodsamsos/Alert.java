package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

public class Alert extends MapActivity {
    String lat = new String();
    String lng = new String();
    String mylat = new String();
    String mylng = new String();
    Boolean destroy = false;
    SharedPreferences s;
    MapView mapView;
    MapController mc;
    GeoPoint p;
    GeoPoint p1;

    class MapOverlay extends com.google.android.maps.Overlay {

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

	    mapView.getProjection().toPixels(p1, screenPts);

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

	setContentView(R.layout.samscreen);
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	Bundle extras = getIntent().getExtras();
	// s.edit().remove("vic_gsid").commit();
	if (extras != null) {
	    String message = extras.getString("vic_gsid");
	    extras.remove("vic_gsid");
	    if (message != null && message.length() > 0) {
		// s.edit().putString("vic_gsid", message).commit();
	    }
	}
	SecureStorage.sam = true;

	s.edit().putString("screen", "Alert").commit();
	Toast.makeText(getApplicationContext(),
		"Victim: " + s.getString("vic_gsid", "unknown"), 1).show();
	load();

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

    public void samscreen_map(View v) {
	Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		Uri.parse("http://maps.google.com/maps?saddr=" + mylat + ","
			+ mylng + "&daddr=" + lat + "," + lng));
	startActivity(intent);

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

    public void samscreen_accept(View v) {
	try {
	    Log.w("Inside", "In");
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("sam_type", "goodsam"));
	    nameValuePairs.add(new BasicNameValuePair("help", "yes"));
	    nameValuePairs.add(new BasicNameValuePair("vic_gsid", s.getString(
		    "vic_gsid", "3")));

	    BufferedReader br = Poster.send(SecureStorage.page_samhelp,
		    nameValuePairs);
	    String res = null;
	    res = br.readLine();
	    Toast.makeText(getApplicationContext(),
		    "Thank you for being a Good Samaritan ", 1).show();
	    if (res.contains("Thank")) {
		destroy = true;
		Intent intent = new Intent(this, GoodSamScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

		finish();
	    } else if (res.contains("Error")) {
		Toast.makeText(getApplicationContext(), "Emergency Resolved", 1)
			.show();
		sad.text = "Emergency Resolved";
		Intent intent = new Intent(this, sad.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	    } else
		Toast.makeText(getApplicationContext(), "Error Try Again", 1)
			.show();
	} catch (Exception e) {

	    Log.w("Error", e.toString());
	}

    }

    public void samscreen_reject(View v) {
	ask();
    }

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

    public void reject() {
	try {

	    Log.w("Inside", "In");
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("sam_type", "goodsam"));
	    nameValuePairs.add(new BasicNameValuePair("help", "no"));
	    nameValuePairs.add(new BasicNameValuePair("vic_gsid", s.getString(
		    "vic_gsid", "3")));

	    BufferedReader br = Poster.send(SecureStorage.page_samhelp,
		    nameValuePairs);
	    String res = null;
	    res = br.readLine();
	    Toast.makeText(getApplicationContext(), res, 1).show();
	    if (res.contains("Not")) {
		s.edit().putString("screen", "unknown").commit();
		sad.text = "Bad Sam";
		destroy = true;
		Intent intent = new Intent(this, sad.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	    } else
		Toast.makeText(getApplicationContext(), "Error Try Again", 1)
			.show();
	} catch (Exception e) {

	    Log.w("Error", e.toString());
	}
    }

    @Override
    protected boolean isRouteDisplayed() {

	return false;
    }

    public void open_map(View v) {
	setContentView(R.layout.samscreen1);
	load();
	mapView = (MapView) findViewById(R.id.mapView1);
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
	mc.animateTo(p);
	mc.setZoom(15);
	List<Overlay> listOfOverlays = mapView.getOverlays();
	mapView.setSatellite(false);

	listOfOverlays.clear();

	listOfOverlays.add(mapOverlay);

	mapView.invalidate();

    }

    public void open_image(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.Alert"));
	finish();
    }

    public void load() {
	try {
	    Log.w("Inside", "In");
	    ArrayList<NameValuePair> nameValuePairs = new

	    ArrayList<NameValuePair>();

	    nameValuePairs.add(new BasicNameValuePair("gsid", s.getString(
		    "gsid", "2")));
	    nameValuePairs.add(new BasicNameValuePair("vic_gsid", s.getString(
		    "vic_gsid", "3")));

	    BufferedReader br = Poster.send(SecureStorage.page_alert,
		    nameValuePairs);
	    String a = br.readLine();
	    if (a.contains("start")) {
		Log.w("Alert Loading", "Inside Start");
		String url = SecureStorage.SERVER_IP + br.readLine();
		Log.w("Image", url);
		((TextView) findViewById(R.id.samscreen_name)).setText(br
			.readLine());

		lat = br.readLine();
		lng = br.readLine();
		mylat = br.readLine();
		mylng = br.readLine();
		p = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
			(int) (Double.parseDouble(mylng) * 1E6));
		p1 = new GeoPoint((int) (Double.parseDouble(lat) * 1E6),
			(int) (Double.parseDouble(lng) * 1E6));
		((TextView) findViewById(R.id.samscreen_distance)).setText(br
			.readLine());
		((TextView) findViewById(R.id.samscreen_emergency)).setText(br
			.readLine());
		ImageView Image01 = (ImageView) findViewById(R.id.samscreen_photo);

		Drawable image = ImageOperations(this, url);
		Image01.setImageDrawable(image);
	    } else if (a.contains("Invalid")) {
		Log.w("Alert Loading", "Inside Invalid");
		sad.text = "Emergency Resolved";
		startActivity(new Intent("com.goodsam.goodsamsos.sad"));
		finish();
	    } else {
		Toast.makeText(getApplicationContext(), "Connection Error", 1)
			.show();
	    }
	    Log.w("Alert Loading:", a);
	    while (!(a = br.readLine()).equals(""))
		Log.w("Alert Loading:", a);

	} catch (Exception e1) {
	    Log.w("Geo:", e1.toString());
	}
	Geocoder gc = new Geocoder(this, Locale.getDefault());
	try {
	    List<Address> addresses = gc.getFromLocation(
		    Double.parseDouble(lat), Double.parseDouble(lng), 1);
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
		((TextView) findViewById(R.id.samscreen_address)).setText(addr);

	    }

	    Log.w("Geo:", sb.toString());
	} catch (Exception e) {
	    Log.w("Geo121:", e.toString());
	    ((TextView) findViewById(R.id.samscreen_address))
		    .setText("Address Not Available");

	}
    }

    public void gotome1(View v) {
	mc.animateTo(p);
    }

    public void gotosam1(View v) {
	mc.animateTo(p1);
    }
}