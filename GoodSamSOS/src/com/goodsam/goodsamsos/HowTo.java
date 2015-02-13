package com.goodsam.goodsamsos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class HowTo extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.howto);
	    final ListView listView = (ListView) findViewById(R.id.howtolist);
	    final ArrayAdapter < CharSequence > links = ArrayAdapter.createFromResource(
	            this, R.array.links, android.R.layout.simple_spinner_item);
	
	    listView.setOnItemClickListener(new OnItemClickListener() {
	    	    	
	    	    	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	
	    	    		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
			        			Uri.parse(links.getItem(position).toString()));
			        			startActivity(intent);
	    	    	}
	    });
	}

}
