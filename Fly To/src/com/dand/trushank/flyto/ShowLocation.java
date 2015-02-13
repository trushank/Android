package com.dand.trushank.flyto;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.dand.trushank.flyto.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ShowLocation extends MapActivity {

    // RIT
    double RITlat = 43.084671;
    double RITlong = -77.674778;

    // Home
    double Homelat = 43.101224;
    double Homelong = -77.632171;

    // Xerox
    double Xeroxlat = 43.154503;
    double Xeroxlong = -77.604332;

    // Statue of Liberty
    double Statuelat = 40.689462;
    double Statuelong = -74.044493;

    // Taj Mahal
    double Tajlat = 27.175131;
    double Tajlong = 78.042264;

    // Debug Key= AIzaSyCWfEsdEN9JE_ZeDvmwXKnM97PkHktn-Yc
    // App Key=0D5uOIVoo_vsny5paO1nxB-khIhBlQfesI08hDA
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_show_location);
	MapView mapView = (MapView) findViewById(R.id.mapview);
	final MapController mc = mapView.getController();
	mc.setZoom(20);
	final EditText txt = (EditText) findViewById(R.id.editText1);
	mc.animateTo(new GeoPoint((int) (Homelat * 1e6), (int) (Homelong * 1e6)));
	mapView.setSatellite(true);

	txt.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {
		if (s.toString().contains("RIT")) {
		    mc.animateTo(new GeoPoint((int) (RITlat * 1e6),
			    (int) (RITlong * 1e6)));
		} else if (s.toString().contains("Xerox")) {
		    mc.animateTo(new GeoPoint((int) (Xeroxlat * 1e6),
			    (int) (Xeroxlong * 1e6)));
		} else if (s.toString().contains("Liberty")) {
		    mc.animateTo(new GeoPoint((int) (Statuelat * 1e6),
			    (int) (Statuelong * 1e6)));
		} else if (s.toString().contains("Taj")) {
		    mc.animateTo(new GeoPoint((int) (Tajlat * 1e6),
			    (int) (Tajlong * 1e6)));
		} else {
		    mc.animateTo(new GeoPoint((int) (Homelat * 1e6),
			    (int) (Homelong * 1e6)));
		}

	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {

	    }

	    @Override
	    public void afterTextChanged(Editable s) {

	    }
	});
    }

    @Override
    protected boolean isRouteDisplayed() {
	return false;
    }

}
