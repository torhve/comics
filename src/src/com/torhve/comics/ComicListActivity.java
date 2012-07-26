package com.torhve.comics;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class ComicListActivity extends FragmentActivity
        implements ComicListFragment.Callbacks {

	private boolean mTwoPane;
	private String APIKEY;
    private String BASEURL;

	private final String TAG = "ComicListActivity";
	protected SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        APIKEY = prefs.getString("apikey", null);
        BASEURL = prefs.getString("urlbase", null);

        Log.d(TAG, "APIKEY:" + APIKEY); 
        Log.d(TAG, "BASEURL:" + BASEURL); 


        if (APIKEY == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please enter your API key");
            final EditText text = new EditText(this);
            builder.setView(text);	
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    APIKEY = text.getText().toString();
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.putString("apikey", APIKEY);
                    editor.commit();

                    Log.d(TAG, "APIKEY:" + APIKEY); 


                }
            });
            builder.create().show();

        }
        setContentView(R.layout.activity_comic_list);

        
        if (findViewById(R.id.comic_detail_container) != null) {
            mTwoPane = true;
            ((ComicListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.comic_list))
                    .setActivateOnItemClick(true);
        }
        disableConnectionReuseIfNecessary();
        enableHttpResponseCache();
        
        
    }
    
    @SuppressWarnings("deprecation")
	private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                .getMethod("install", File.class, long.class)
                .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
        }
    }


    @Override
    public void onItemSelected(String id) {
    	Log.d(TAG, "onItemSelected:"+id);
        Bundle arguments = new Bundle();
        arguments.putString(ComicDetailFragment.ARG_ITEM_ID, id);

        if (mTwoPane) {
            ComicDetailFragment fragment = new ComicDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comic_detail_container, fragment)
                    .commit();

        } else {
            //Intent detailIntent = new Intent(this, ComicDetailActivity.class);
            //detailIntent.putExtra(ComicDetailFragment.ARG_ITEM_ID, id);
            //startActivity(detailIntent);
        	ComicDetailFragment fragment = new ComicDetailFragment();
            fragment.setArguments(arguments);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.comic_list, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, PrefsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public String getApiKey() { return this.APIKEY; }

	public String getBaseUrl() {
		return this.BASEURL;
	}


}
