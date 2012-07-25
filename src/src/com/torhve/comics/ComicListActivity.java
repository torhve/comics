package com.torhve.comics;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.torhve.comics.backend.JasonHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

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


        if (APIKEY.equals("")) {
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
    }

    @Override
    public void onItemSelected(String id) {
    	Log.d(TAG, "onItemSelected:"+id);
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ComicDetailFragment.ARG_ITEM_ID, id);
            ComicDetailFragment fragment = new ComicDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.comic_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, ComicDetailActivity.class);
            detailIntent.putExtra(ComicDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
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
