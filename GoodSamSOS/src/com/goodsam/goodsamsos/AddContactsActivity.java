package com.goodsam.goodsamsos;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddContactsActivity extends Activity {
    int i = 0;
    TextView txtContacts[] = new TextView[5];
    TextView txtNumbers[] = new TextView[5];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.addcontacts);

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
	String name = new String();
	super.onActivityResult(reqCode, resultCode, data);
	txtContacts[0] = (TextView) findViewById(R.id.contact1);
	txtContacts[1] = (TextView) findViewById(R.id.contact2);
	txtContacts[2] = (TextView) findViewById(R.id.contact3);
	txtContacts[3] = (TextView) findViewById(R.id.contact4);
	txtContacts[4] = (TextView) findViewById(R.id.contact5);
	txtNumbers[0] = (TextView) findViewById(R.id.numbers1);
	txtNumbers[1] = (TextView) findViewById(R.id.numbers2);
	txtNumbers[2] = (TextView) findViewById(R.id.numbers3);
	txtNumbers[3] = (TextView) findViewById(R.id.numbers4);
	txtNumbers[4] = (TextView) findViewById(R.id.numbers5);
	Uri contactData;
	switch (reqCode) {
	case (8):
	    if (resultCode == Activity.RESULT_OK) {
		contactData = data.getData();
		Cursor c = managedQuery(contactData, null, null, null, null);
		if (c.moveToFirst()) {
		    name = c.getString(c
			    .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
		    ContentResolver cr = getContentResolver();
		    Cursor cursor = cr.query(
			    ContactsContract.Contacts.CONTENT_URI, null,
			    "DISPLAY_NAME = '" + name + "'", null, null);
		    if (cursor.moveToFirst()) {
			String contactId = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts._ID));
			txtContacts[i].setText(name);
			txtNumbers[i].setText(getContactPhoneNumberByPhoneType(
				this, Integer.parseInt(contactId),
				Phone.TYPE_MOBILE));
			if (txtNumbers[i].getText().equals("")) {
			    txtContacts[i].setText("");
			    Toast.makeText(this,
				    "Contact does not have a number",
				    Toast.LENGTH_LONG).show();
			} else
			    i++;
		    }
		    break;
		}
	    }
	}
    }

    public void addContact(View v) {
	if (i < 5) {
	    Intent intent = new Intent(Intent.ACTION_PICK,
		    ContactsContract.Contacts.CONTENT_URI);
	    startActivityForResult(intent, 8);
	}
    }

    public void remove(View v) {
	if (i > 0) {
	    txtContacts[i - 1].setText("");
	    txtNumbers[i - 1].setText("");
	    i--;
	}
    }

    public void password(View v) {
	try {
	    String names[] = { "emer1name", "emer2name", "emer3name",
		    "emer4name", "emer5name", "emer1contact", "emer2contact",
		    "emer3contact", "emer4contact", "emer5contact" };
	    String values[] = new String[10];
	    for (int x = 0; x < 5; x++)
		values[x] = txtContacts[x].getText().toString();
	    for (int x = 0; x < 5; x++)
		values[x + 5] = txtNumbers[x].getText().toString();
	    SharedPreferences s = getSharedPreferences("details", 2);
	    for (int i = 0; i < names.length; i++) {
		s.edit().putString(names[i], values[i]).commit();
	    }
	    startActivity(new Intent("com.goodsam.goodsamsos.PasswordActivity"));
	    finish();
	} catch (Exception e) {
	}
    }

    public static String getContactPhoneNumberByPhoneType(Context context,
	    long contactId, int type) {
	String phoneNumber = null;
	String TAG = "ABCD";
	String[] whereArgs = new String[] { String.valueOf(contactId),
		String.valueOf(type) };
	Log.d(TAG, String.valueOf(contactId));
	Cursor cursor = context.getContentResolver().query(
		ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		null,
		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and "
			+ ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
		whereArgs, null);
	int phoneNumberIndex = cursor
		.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
	Log.d(TAG, String.valueOf(cursor.getCount()));
	if (cursor != null) {
	    Log.v(TAG, "Cursor Not null");
	    try {
		if (cursor.moveToNext()) {
		    Log.v(TAG, "Moved to first");
		    Log.v(TAG, "Cursor Moved to first and checking");
		    phoneNumber = cursor.getString(phoneNumberIndex);
		}
	    } finally {
		Log.v(TAG, "In finally");
		cursor.close();
	    }
	}
	Log.v(TAG, "Returning phone number");
	return phoneNumber;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK)
	    startActivity(new Intent("com.goodsam.goodsamsos.SignupActivity"));
	finish();
	return super.onKeyDown(keyCode, event);
    }

    public void contacts_back(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.SignupActivity"));
	finish();
    }
}