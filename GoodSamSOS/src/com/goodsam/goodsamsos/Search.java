package com.goodsam.goodsamsos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class Search extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.search);
	final ListView listView = (ListView) findViewById(R.id.search_list);
	String[] values = new String[] { "Call Police", "Call Ambulance",
		"Call Firebrigade", "Find Police Station", "Find Hospital",
		"Find Fire Station", "Find on Map", "Google Search",
		"Survival Tips" };
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_1, android.R.id.text1, values);
	listView.setAdapter(adapter);
	listView.setOnItemClickListener(new OnItemClickListener() {

	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		if (listView.getItemAtPosition(position).toString()
			.contains("Call Police")) {
		    String uri = "tel:100";
		    Intent intent = new Intent(Intent.ACTION_CALL);
		    intent.setData(Uri.parse(uri));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Call Ambulance")) {
		    String uri = "tel:102";
		    Intent intent = new Intent(Intent.ACTION_CALL);
		    intent.setData(Uri.parse(uri));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Call Firebrigade")) {
		    String uri = "tel:101";
		    Intent intent = new Intent(Intent.ACTION_CALL);
		    intent.setData(Uri.parse(uri));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Find Police Station")) {
		    Intent intent = new Intent(
			    android.content.Intent.ACTION_VIEW, Uri
				    .parse("geo:0,0?q=police"));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Find Hospital")) {
		    Intent intent = new Intent(
			    android.content.Intent.ACTION_VIEW, Uri
				    .parse("geo:0,0?q=hospital"));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Find Fire Station")) {
		    Intent intent = new Intent(
			    android.content.Intent.ACTION_VIEW, Uri
				    .parse("geo:0,0?q=fire station"));
		    startActivity(intent);

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Find on Map")) {
		    final EditText input = new EditText(Search.this);
		    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    switch (which) {
			    case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				Intent intent = new Intent(
					android.content.Intent.ACTION_VIEW, Uri
						.parse("geo:0,0?q="
							+ input.getText()
								.toString()));
				startActivity(intent);
				break;

			    case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			    }
			}
		    };

		    AlertDialog.Builder builder = new AlertDialog.Builder(
			    Search.this);
		    builder.setView(input);
		    builder.setMessage("Search on Map")
			    .setPositiveButton("Search", dialogClickListener)
			    .setNegativeButton("Cancel", dialogClickListener)
			    .show();

		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Google Search")) {
		    final EditText input = new EditText(Search.this);
		    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			    switch (which) {
			    case DialogInterface.BUTTON_POSITIVE:
				// Yes button clicked
				Intent intent = new Intent(
					android.content.Intent.ACTION_VIEW,
					Uri.parse("http://www.google.co.in/search?q="
						+ input.getText().toString()));
				startActivity(intent);
				break;

			    case DialogInterface.BUTTON_NEGATIVE:
				// No button clicked
				break;
			    }
			}
		    };

		    AlertDialog.Builder builder = new AlertDialog.Builder(
			    Search.this);
		    builder.setView(input);
		    builder.setMessage("Google Search")
			    .setPositiveButton("Search", dialogClickListener)
			    .setNegativeButton("Cancel", dialogClickListener)
			    .show();
		}
		if (listView.getItemAtPosition(position).toString()
			.contains("Survival Tips")) {
		    startActivity(new Intent("com.goodsam.goodsamsos.HowTo"));
		}
	    }

	});
    }
}