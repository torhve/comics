/**
 */

package com.torhve.comics.backend;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JasonHandler {
	private static final String TAG = "JasonHandler";

	public static JSONObject getJSONfromURL(URL url) {

		// initialize
		JSONObject jArray = null;

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			Log.d(TAG, "Error reading data " + e1.toString());
			e1.printStackTrace();
		}
		try {
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			// try parse the string to a JSON object
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			jArray = new JSONObject(sb.toString());
		} catch (JSONException e) {
			Log.d(TAG, "Error parsing data " + e.toString());
		} catch (IOException e) {
			Log.d(TAG, "Error reading data " + e.toString());
		} finally {
			urlConnection.disconnect();
		}
		return jArray;
	}
}