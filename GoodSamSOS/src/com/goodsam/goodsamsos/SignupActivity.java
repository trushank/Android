package com.goodsam.goodsamsos;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends Activity {
    Spinner spinner;
    TextView name;
    private int mYear;
    private int mMonth;
    private int mDay;
    TextView dob;
    static final int DATE_DIALOG_ID = 0;

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
	public void onDateSet(DatePicker view, int year, int monthOfYear,
		int dayOfMonth) {
	    mYear = year;
	    mMonth = monthOfYear;
	    mDay = dayOfMonth;
	    updateDisplay();
	}
    };

    @Override
    protected Dialog onCreateDialog(int id) {
	switch (id) {
	case DATE_DIALOG_ID:
	    return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
		    mDay);
	}
	return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.signup);

	dob = (TextView) findViewById(R.id.dob);
	name = (TextView) findViewById(R.id.name);
	name.requestFocus();
	spinner = (Spinner) findViewById(R.id.bgroup);
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		this, R.array.blood, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(adapter);
	final Calendar c = Calendar.getInstance();
	mYear = c.get(Calendar.YEAR);
	mMonth = c.get(Calendar.MONTH);
	mDay = c.get(Calendar.DAY_OF_MONTH);
	dob.setOnFocusChangeListener(new OnFocusChangeListener() {
	    public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus == true)
		    showDialog(DATE_DIALOG_ID);
	    }
	});

    }

    public void login(View v) {
	startActivity(new Intent("com.goodsam.goodsamsos.LoginActivity"));
	finish();
    }

    public void photo(View v) {

	int x = 0;
	String names[] = { "fname", "lname", "dd", "mm", "yy", "gender",
		"cell_no", "blood", "addr", "email" };
	String values[] = new String[names.length];

	if (name.getText().toString().equals("")) {
	    Toast.makeText(this, "Name required", Toast.LENGTH_LONG).show();
	    return;
	} else if (!name.getText().toString().contains(" ")) {
	    Toast.makeText(this, "Last name required", Toast.LENGTH_LONG)
		    .show();
	    return;
	} else {
	    String n = name.getText().toString();
	    int a = n.indexOf(" ");
	    values[x++] = n.substring(0, a);
	    values[x++] = n.substring(a + 1, n.length());
	}

	values[x++] = Integer.toString(mDay);
	values[x++] = Integer.toString(mMonth + 1);
	values[x++] = Integer.toString(mYear);

	RadioGroup sex = (RadioGroup) findViewById(R.id.sex);
	if (sex.getChildAt(0).getId() == sex.getCheckedRadioButtonId())
	    values[x++] = "M";
	else
	    values[x++] = "F";

	values[x++] = ((TextView) findViewById(R.id.mob)).getText().toString();
	values[x++] = ((Spinner) findViewById(R.id.bgroup)).getSelectedItem()
		.toString();
	values[x++] = ((TextView) findViewById(R.id.address)).getText()
		.toString();
	values[x] = ((TextView) findViewById(R.id.email)).getText().toString();

	if (values[2].equals("")) {
	    Toast.makeText(this, "Check Date of birth", Toast.LENGTH_LONG)
		    .show();
	    return;
	}
	if (values[6].length() < 10 && values[6].length() > 15) {
	    Toast.makeText(this, "Check Number", Toast.LENGTH_LONG).show();
	    return;
	}
	if (values[8].length() < 5) {
	    Toast.makeText(this, "Check Address", Toast.LENGTH_LONG).show();
	    return;
	}
	if (!values[9].contains("@") || !values[9].contains(".")) {
	    Toast.makeText(getApplicationContext(), "Check Email Address",
		    Toast.LENGTH_LONG).show();
	    return;
	}
	SharedPreferences s = getSharedPreferences("details",
		MODE_WORLD_WRITEABLE);
	s.edit().clear().commit();
	for (int i = 0; i < names.length; i++) {
	    s.edit().putString(names[i], values[i]).commit();
	}

	startActivity(new Intent("com.goodsam.goodsamsos.AddContactsActivity"));
	finish();
    }

    public void pick(View v) {
	showDialog(DATE_DIALOG_ID);
    }

    private void updateDisplay() {
	dob.setText(new StringBuilder()

	.append(mDay).append("-").append(mMonth + 1).append("-").append(mYear)
		.append(" "));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    startActivity(new Intent("com.goodsam.goodsamsos.LoginActivity"));
	    finish();
	}
	return super.onKeyDown(keyCode, event);
    }
}