package com.torhve.comics;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.torhve.comics.backend.JasonHandler;

public class ComicDetailFragment extends Fragment implements OnClickListener {

    public static final String ARG_ITEM_ID = "item_id";
    private int currentPosition = 0;
	public static final String TAG = "ComicDetailFragment";
	public ArrayList<HashMap<String, String>> comiclist;
	public String nextUrl;
	protected String baseurl;

	protected View imageView;
	protected View rootView;
	private TextView titleView;
	private TextView infoView;
	private TextView dateView;

    public ComicDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String APIKEY = ((ComicListActivity) getActivity()).getApiKey();
            baseurl = ((ComicListActivity) getActivity()).getBaseUrl();
            String itemId = getArguments().getString(ARG_ITEM_ID);
            if(APIKEY!=null && baseurl != null)
            	new FetchAndUpdate().execute(baseurl + "/api/v1/releases/?format=json&my=true&key=" + APIKEY + "&comic=" + itemId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comic_detail, container, false);
        imageView = ((ImageView) rootView.findViewById(R.id.comic_image));
        titleView = ((TextView) rootView.findViewById(R.id.comic_title));
        dateView = ((TextView) rootView.findViewById(R.id.comic_date));
        infoView = ((TextView) rootView.findViewById(R.id.comic_info));
        
        // Empty the sample strings
        titleView.setText("");
        infoView.setText("");
        dateView.setText("");

        imageView.setOnClickListener(this);
        return rootView;
    }
    private class FetchAndUpdate extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			Log.d("JSON fetching URL:", params[0]);
			URL url = null;
			   try {
				url = new URL(params[0]);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				Log.d("MalformedRL:", e1.toString());
			}

        	
        	comiclist = new ArrayList<HashMap<String, String>>();
        	//ArrayList<long<String>> mylist2 = new ArrayList<long<String>>();

        	//Get the data (see above)
        	JSONObject json =
        		JasonHandler.getJSONfromURL(url);

           try{
        		//Get the element that holds the earthquakes ( JSONArray )
        	   Log.d("JSON", json.toString());
        	   ComicDetailFragment.this.nextUrl = json.getJSONObject("meta").getString("next");
        	   JSONArray comics = json.getJSONArray("objects");

        		   //Loop the Array
        	        for(int i=0;i < comics.length();i++){						
        	        	JSONObject c = comics.getJSONObject(i);
        	        	JSONObject iobj = c.getJSONArray("images").getJSONObject(0);

        	        	HashMap<String, String> map = new HashMap<String, String>();
        	        	map.put("id",  c.getString("id"));
        	        	map.put("pub_date", c.getString("pub_date"));
        	        	
        	        	map.put("file", iobj.getString("file"));
        	        	map.put("title", iobj.getString("title"));
        	        	map.put("text", iobj.getString("text"));

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
        	ComicDetailFragment.this.getAndSetNextImage(mylist);
                
        }
    }
    public void getAndSetNextImage(ArrayList<HashMap<String, String>> mylist) {
    	// Get the current HashMap
    	try {
			HashMap<String, String> hm = mylist.get(currentPosition);
			String imgurl = hm.get("file");
		
			// Fetch image and update image view
			new DownloadImageTask((ImageView) imageView).execute(imgurl);
		
		    titleView.setText(hm.get("title"));        
		    infoView.setText(hm.get("text"));
		    dateView.setText(hm.get("pub_date"));
		    
			if(currentPosition+1 < mylist.size()) {
				// TODO fetch more
				this.currentPosition++;
			}else { 
		    	new FetchAndUpdate().execute(baseurl + nextUrl);
		    	currentPosition = 0;
		    }
    	}catch(IndexOutOfBoundsException e) {
    		// Probably during update of new list
    		Log.d(TAG, e.toString());
    		Log.d(TAG, "Position:"+currentPosition+",size:"+mylist.size());
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
	@Override
	public void onClick(View arg0) {
		this.getAndSetNextImage(comiclist);
	}
 }

