package com.goodsam.goodsamsos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class Poster {
    public static BufferedReader send(String page,
	    List<NameValuePair> nameValuePairs) {
	HttpClient client = new DefaultHttpClient();
	HttpPost post = new HttpPost(SecureStorage.SERVER_IP + page);

	try {

	    // Add attributes to post msg
	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	    HttpParams params = new BasicHttpParams();

	    // Change timeout if required
	    HttpConnectionParams.setConnectionTimeout(params,
		    SecureStorage.timeout);
	    post.setParams(params);

	    // Execute Post
	    HttpResponse response = client.execute(post);

	    // Getting response
	    return new BufferedReader(new InputStreamReader(response
		    .getEntity().getContent()));
	} catch (Exception e) {
	    Log.e("Posting:", e.toString());
	}
	return null;
    }

    public static String postData(String name[], String value[]) {

	try {
	    // Add your data
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

	    for (int i = 0; i < name.length; i++) {
		nameValuePairs.add(new BasicNameValuePair(name[i], value[i]));
	    }

	    // Execute HTTP Post Request
	    BufferedReader response = send(SecureStorage.page_register,
		    nameValuePairs);

	    if (response.readLine().contains("Thank")) {
		String gsid = response.readLine();
		Log.w("GSID", gsid);
		return gsid;
	    }

	    else {
		String a;
		while (!(a = response.readLine()).equals("")) {
		    Log.w("REGISTER", a);
		}
	    }

	} catch (ClientProtocolException e) {
	    Log.w("REGISTER", e.toString());

	} catch (Exception e) {
	    Log.w("REGISTER", e.toString());
	}

	return "error";

    }

}