package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service implements LocationListener {

    private static final String TAG = "MyService";
    private LocationManager locationManager;
    private String provider;

    Calendar cal;
    SharedPreferences s;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @Override
    public void onCreate() {
	cal = new GregorianCalendar();

	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	if (s.getString("login", "0").equals("0")) {
	    stopSelf();
	}

	Log.d(TAG, "onCreate");
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	Criteria criteria = new Criteria();
	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
		&& s.getBoolean("gps", false)) {
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    Log.w("Provider", "gps");
	} else {
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    Log.w("Provider", "network");
	}
	provider = locationManager.getBestProvider(criteria, false);
	locationManager.requestLocationUpdates(provider,
		SecureStorage.loc_freq, SecureStorage.loc_dist, MyService.this);

    }

    @Override
    public void onStart(Intent intent, int startId) {
	super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
	locationManager.removeUpdates(MyService.this);
	Log.d(TAG, "onDestroy");

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
	if (s.getString("login", "0").equals("0")) {
	    stopSelf();
	}
	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	Criteria criteria = new Criteria();
	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
		&& s.getString("gps", "0").equals("0")) {
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    Log.w("Provider", "gps");
	} else {
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    Log.w("Provider", "network");
	}
	provider = locationManager.getBestProvider(criteria, false);
	locationManager.requestLocationUpdates(provider,
		SecureStorage.loc_freq, SecureStorage.loc_dist, MyService.this);
	Log.d(TAG, "onStart");
	return START_STICKY;

    }

    public void postData(double lat, double lng) {
	if (!(s.getInt("c2dm_date", 0) == cal.get(Calendar.DATE))) {
	    updateC2DM();
	}

	try {
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

	    if (res.readLine().contains("Error")) {
		stopSelf();
		s.edit().clear().commit();
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
    public void onLocationChanged(Location location) {
	double lat = (double) (location.getLatitude());
	double lng = (double) (location.getLongitude());
	s.edit().putString("mylat", Double.toString(lat)).commit();
	s.edit().putString("mylng", Double.toString(lng)).commit();
	if (s.getString("login", "0").equals("0")) {
	    stopSelf();
	}
	if (s.getString("login", "0").equals("1"))
	    postData(lat, lng);
	Log.w("Location", "Changed");

    }

    public void updateC2DM() {

	s.edit().putInt("c2dm_date", cal.get(Calendar.DATE)).commit();
	Log.w("C2DM", "start registration process");
	Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
	intent.putExtra("app",
		PendingIntent.getBroadcast(this, 0, new Intent(), 0));
	intent.putExtra("sender", "goodsamsos@gmail.com");
	startService(intent);

    }

    @Override
    public void onProviderDisabled(String arg0) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}