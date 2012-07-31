package com.torhve.comics.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ComicMeta {

   	private static final String TAG = "ComicMeta";
	private SharedPreferences prefs;
	
	public ComicMeta(Context cxt) {
		this.prefs =  PreferenceManager.getDefaultSharedPreferences(cxt);
	}
	private static String comicIdtoPrefsKey(String comicId) {
		return "comic_" + comicId;
	}
	public Long getLatest(String comicId) {
		Long value = prefs.getLong(comicIdtoPrefsKey(comicId), 0);
		Log.d(TAG, "getLatest:"+comicId);
		return value;
	}
	public void setLatest(String comicId, Long latest) {
		Log.d(TAG, "setLatest:"+comicId+",latest:"+latest);
        prefs.edit().putLong(comicIdtoPrefsKey(comicId), latest).commit();
	}
}
