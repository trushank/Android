package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class Map extends MapActivity {
    boolean change_loc = false;
    MapView mapView;
    MapController mc;
    GeoPoint p;
    GeoPoint p1;
    String mylat = new String();
    String mylng = new String();
    SharedPreferences s;

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
			    Map.this);
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

	    return true;
	}
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.map);

	Toast.makeText(getApplicationContext(),
		"Press Menu Button to change location", 1).show();

	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	mylat = s.getString("mylat", "19.016921");
	mylng = s.getString("mylng", "72.858412");

	mapView = (MapView) findViewById(R.id.mapView);

	@SuppressWarnings("deprecation")
	View zoomView = mapView.getZoomControls();

	mapView.addView(zoomView, new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		MapView.LayoutParams.BOTTOM_CENTER));
	mapView.displayZoomControls(true);
	p = new GeoPoint((int) (Double.parseDouble(mylat) * 1E6),
		(int) (Double.parseDouble(mylng) * 1E6));
	MapOverlay mapOverlay = new MapOverlay();
	Log.w("vic", "4");
	mc = mapView.getController();
	mc.animateTo(p);
	mc.setZoom(15);

	List<Overlay> listOfOverlays = mapView.getOverlays();

	listOfOverlays.clear();

	listOfOverlays.add(mapOverlay);

	mapView.invalidate();
	// TODO Auto-generated method stub
    }

    @Override
    protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
    }

    public void postData(double lat, double lng) {

	Toast.makeText(this, "Updating", Toast.LENGTH_LONG).show();

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) {
	    change_loc = true;
	    Toast.makeText(getApplicationContext(),
		    "Tap your correct location on the map", 0).show();
	    Log.w("Loc", "called");
	}
	return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
	finish();
	super.onPause();
    }
}