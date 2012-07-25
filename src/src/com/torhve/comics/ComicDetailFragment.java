package com.torhve.comics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.torhve.comics.backend.JasonHandler;

public class ComicDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

	public static final String TAG = "ComicDetailFragment";

    JSONObject mItem;

	public ArrayList<HashMap<String, String>> comiclist;

	

	
	public View imageView;

    public ComicDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String APIKEY = ((ComicListActivity) getActivity()).getApiKey();
            String baseurl = ((ComicListActivity) getActivity()).getBaseUrl();
            String itemId = getArguments().getString(ARG_ITEM_ID);
            if(APIKEY!=null && baseurl != null)
            	new FetchAndUpdate().execute(baseurl + "/api/v1/releases/?format=json&my=true&key=" + APIKEY + "&comic=" + itemId);

            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comic_detail, container, false);
        imageView = ((ImageView) rootView.findViewById(R.id.comic_detail));      
        return rootView;
    }
    private class FetchAndUpdate extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			Log.d("JSON fetching URL:", params[0]);
        	
        	comiclist = new ArrayList<HashMap<String, String>>();
        	//ArrayList<long<String>> mylist2 = new ArrayList<long<String>>();

        	//Get the data (see above)
        	JSONObject json =
        		JasonHandler.getJSONfromURL(params[0]);

           try{
        		//Get the element that holds the earthquakes ( JSONArray )
        	   Log.d("JSON", json.toString());
        	   JSONArray comics = json.getJSONArray("objects");

        		   //Loop the Array
        	        for(int i=0;i < comics.length();i++){						
        	        	JSONObject c = comics.getJSONObject(i);
        	        	JSONObject iobj = c.getJSONArray("images").getJSONObject(0);

        	        	HashMap<String, String> map = new HashMap<String, String>();
        	        	map.put("id",  c.getString("id"));
        	        	map.put("pub_date", c.getString("pub_date"));
        	        	map.put("file", iobj.getString("file"));
        	        	comiclist.add(map);
        	        }

        	       }catch(JSONException e)        {
        	       	 Log.e(TAG, "Error parsing data "+e.toString());
        	       }catch(NullPointerException e) {
        	    	 Log.e(TAG, "null from json "+e.toString());
        	       }
           		return comiclist;
           }


        /*protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }*/

        protected void onPostExecute(ArrayList<HashMap<String, String>> mylist) {
                new DownloadImageTask((ImageView) imageView).execute(mylist.get(0).get("file"));
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

    	public DownloadImageTask(ImageView bmImage) {
	    	this.bmImage = bmImage;
    	}

    	protected Bitmap doInBackground(String... urls) {
	    	String urldisplay = urls[0];
	    	Bitmap mIcon11 = null;
	    	try {
	    		InputStream in = new java.net.URL(urldisplay).openStream();
	    		mIcon11 = BitmapFactory.decodeStream(in);
	    	} catch (Exception e) {
	    		Log.e(TAG, e.getMessage());
	    		e.printStackTrace();
	    	}
	    	return mIcon11;
	    }
	
    	protected void onPostExecute(Bitmap result) {
    		bmImage.setImageBitmap(result);
    	}
	   	
    }
 }

