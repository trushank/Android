package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Activity2 extends Activity {
    static String status;
    static Uri target = null;
    static String targetpath = null;
    static ImageView b1;
    static ProgressDialog dialog;
    static Bitmap bitmapOrg = null;
    static String user = "unknown";
    boolean once = false;
    SharedPreferences s;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity2);
	b1 = (ImageView) findViewById(R.id.imageView1);
	s = getSharedPreferences("details", MODE_WORLD_WRITEABLE);
	s.edit().putString("screen", "Activity2").commit();
	Toast.makeText(getApplicationContext(), "Tap icon to select image", 1)
		.show();

    }

    public void selectImg(View v) {
	Intent intent = new Intent(Intent.ACTION_PICK,
		android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	startActivityForResult(intent, 0);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	// TODO Auto-generated method stub
	super.onActivityResult(requestCode, resultCode, data);

	if (resultCode == RESULT_OK) {

	    Uri targetUri = data.getData();
	    target = targetUri;
	    targetpath = getRealPathFromURI(targetUri);
	    SharedPreferences s = getSharedPreferences("details",
		    MODE_WORLD_READABLE);
	    user = s.getString("gsid", "unknown");
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(Activity2.targetpath, o);

	    // The new size we want to scale to
	    final int REQUIRED_SIZE = 1024;

	    // Find the correct scale value. It should be the power of 2.
	    int width_tmp = o.outWidth, height_tmp = o.outHeight;
	    int scale = 1;
	    while (true) {
		if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
		    break;
		width_tmp /= 2;
		height_tmp /= 2;
		scale *= 2;
	    }

	    // Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize = scale;
	    bitmapOrg = BitmapFactory.decodeFile(Activity2.targetpath, o2);

	    b1.setImageBitmap(bitmapOrg);
	    dialog = ProgressDialog.show(this, "Uploading", "Please wait...",
		    true);

	    new ImageUploadTask().execute();
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new MyTimerTask(this), 0, 3000);

	}
    }

    public String getRealPathFromURI(Uri contentUri) {
	String[] proj = { MediaStore.Images.Media.DATA };
	Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	int column_index = cursor
		.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	cursor.moveToFirst();
	return cursor.getString(column_index);
    }

    public void next(View v) {
	if (Activity2.status.contains("Success")) {
	    {
		startActivity(new Intent("com.goodsam.goodsamsos.LoginActivity"));
		s.edit().putString("screen", "unknown").commit();

	    }
	    finish();
	} else
	    Toast.makeText(getApplicationContext(),
		    "Image not uploaded. Try again.", 0).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK)
	    Toast.makeText(getApplicationContext(),
		    "Please Upload image to complete registration", 0).show();
	return true;

    }

    public void image_back(View v) {
	Toast.makeText(getApplicationContext(),
		"Please Upload image to complete registration", 0).show();

    }

    class ImageUploadTask extends AsyncTask<Void, Void, String> {

	@Override
	protected String doInBackground(Void... unsued) {

	    ArrayList<NameValuePair> nameValuePairs = null;
	    try {

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		Activity2.bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 30,
			bao);
		byte[] ba = bao.toByteArray();
		String ba1 = Base64.encodeBytes(ba);
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("image", ba1));
		nameValuePairs.add(new BasicNameValuePair("name",
			Activity2.user));
	    } catch (Exception e) {
		Log.w("Error", e.toString());
	    }

	    try {
		BufferedReader br = Poster.send(SecureStorage.page_image,
			nameValuePairs);
		String res = br.readLine();
		if (res.contains("Done")) {
		    Activity2.status = "Upload Successful";
		} else {
		    Activity2.status = "Error Try Again";
		    while ((res = br.readLine()) != null)
			Log.w("Response", res);
		}
		Activity2.dialog.dismiss();

	    } catch (Exception e) {

		Log.e("log_tag", "Error in http connection " + e.toString());

	    }
	    return null;

	}

    }

    class MyTimerTask extends TimerTask {

	private Context context;

	public MyTimerTask(Context pContext) {

	    this.context = pContext;
	}

	@Override
	public void run() {
	    updateUI.sendEmptyMessage(0);
	}

	private Handler updateUI = new Handler() {
	    @Override
	    public void dispatchMessage(Message msg) {
		super.dispatchMessage(msg);
		if (!Activity2.dialog.isShowing()) {
		    if (!once) {
			Toast.makeText(context, Activity2.status, 0).show();
			once = true;
		    }
		    if (Activity2.status.contains("Success"))
			MyTimerTask.this.cancel();

		}

	    }
	};

    }
}