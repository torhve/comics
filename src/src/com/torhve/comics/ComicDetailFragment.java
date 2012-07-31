package com.torhve.comics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.torhve.comics.backend.ComicMeta;
import com.torhve.comics.backend.JasonHandler;

public class ComicDetailFragment extends Fragment implements OnClickListener, OnPageChangeListener {

    public static final String ARG_ITEM_ID = "item_id";
    private int currentPosition = 0;
	public static final String TAG = "ComicDetailFragment";
	public ArrayList<HashMap<String, String>> comiclist;
	public String nextUrl;
	protected String baseurl;

	//protected View imageView;
	//protected View rootView;
	//private TextView titleView;
	//private TextView infoView;
	//private TextView dateView;
	
    private ViewPager awesomePager;
    private Context cxt;
    private AwesomePagerAdapter awesomeAdapter;
	private View rootView;
	private ComicMeta meta;


    public ComicDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String APIKEY = ((ComicListActivity) getActivity()).getApiKey();
            baseurl = ((ComicListActivity) getActivity()).getBaseUrl();
            String itemId = getArguments().getString(ARG_ITEM_ID);
            meta = new ComicMeta(getActivity().getBaseContext());
            if(APIKEY!=null && baseurl != null) {
            	new FetchAndUpdate().execute(baseurl + "/api/v1/releases/?format=json&my=true&key=" + APIKEY + "&comic=" + itemId);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        cxt = getActivity().getApplicationContext();
        awesomePager = (ViewPager)rootView.findViewById(R.id.pager);
        awesomePager.setOnPageChangeListener(this);
        Button firstButton = ((Button) rootView.findViewById(R.id.goto_first));
        firstButton.setVisibility(View.INVISIBLE);
        firstButton.setOnClickListener(this);
        Button lastButton = ((Button) rootView.findViewById(R.id.goto_last));
        lastButton.setOnClickListener(this);
        ((Button) rootView.findViewById(R.id.goto_latest)).setOnClickListener(this);
        

        
        return rootView;
    }
    private class AwesomePagerAdapter extends PagerAdapter{

        
        @Override
        public int getCount() {
                return comiclist.size();
        }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate()}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
        @Override
        public Object instantiateItem(View collection, int position) {
        	Log.d(TAG, "instantiateItem, position:" + position);
			
        	HashMap<String, String> hm = comiclist.get(position);
			String imgurl = hm.get("file");

	        LayoutInflater inflater = (LayoutInflater) cxt
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View layout = inflater.inflate(R.layout.fragment_comic_detail, null);
            ((ViewPager) collection).addView(layout,0);

            ImageView imageView = ((ImageView) layout.findViewById(R.id.comic_image));
			
            TextView titleView = ((TextView) layout.findViewById(R.id.comic_title));
            TextView dateView = ((TextView) layout.findViewById(R.id.comic_date));
            TextView infoView = ((TextView) layout.findViewById(R.id.comic_info));
            
		    titleView.setText(hm.get("title"));        
		    infoView.setText(hm.get("text"));
		    dateView.setText(hm.get("pub_date"));
		   
			// Fetch image and update image view
			new DownloadImageTask((ImageView) imageView, getArguments().getString(ARG_ITEM_ID), Long.parseLong(hm.get("id"))).execute(imgurl);
            return layout;
        }
        

    /**
     * Remove a page for the given position.  The adapter is responsible
     * for removing the view from its container, although it only must ensure
     * this is done by the time it returns from {@link #finishUpdate()}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed.
     * @param object The same object that was returned by
     * {@link #instantiateItem(View, int)}.
     */
        @Override
        public void destroyItem(View collection, int position, Object view) {
                ((ViewPager) collection).removeView((View) view);
        }

        
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
                return view==(View)object;
        }

        
    /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
        @Override
        public void finishUpdate(View arg0) {}
        

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}

        @Override
        public Parcelable saveState() {
                return null;
        }

        @Override
        public void startUpdate(View arg0) {}

}

    private class FetchAndUpdate extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			Log.d("JSON fetching URL:", params[0]);
        	
        	comiclist = new ArrayList<HashMap<String, String>>();
        	// Get the the current comic ID
        	String comicId = getArguments().getString(ARG_ITEM_ID);
        	// Get the last read comic
        	Long latest = meta.getLatest(comicId);

        	//Get the data (see above)
        	JSONObject json =
        		JasonHandler.getJSONfromURL(params[0]);

           try{
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
        	        	
        	        	// Handle latest
        	        	if(latest == 0) {
        	        		latest = Long.parseLong(c.getString("id"));
        	        		meta.setLatest(comicId, latest);
        	        	}
        	        	
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
        	//ComicDetailFragment.this.getAndSetNextImage(mylist);
            awesomeAdapter = new AwesomePagerAdapter();
            awesomePager.setAdapter(awesomeAdapter);
            awesomeAdapter.notifyDataSetChanged();
        }
    }
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		String comicId;
		Long stripId;

    	public DownloadImageTask(ImageView bmImage, String comicId, Long stripId) {
	    	this.bmImage = bmImage;
	    	this.comicId = comicId;
	    	this.stripId = stripId;
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
    		// Handle latest
    		Long storedLatest = meta.getLatest(comicId);
    		if(stripId > storedLatest) {
    			meta.setLatest(comicId, stripId);
    		}
    	}
	   	
    }
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.goto_latest) {
	    	// Find the latest ID and select it
        	
        	// Get the the current comic ID
        	String comicId = getArguments().getString(ARG_ITEM_ID);
        	// Get the last read comic
        	Long latest = meta.getLatest(comicId);
        	Log.d(TAG, "latest:"+latest);
        	for(int i=0;i<comiclist.size();i++) {
        		Log.d(TAG, "comcic:"+comiclist.get(i).get("id"));
        		if(comiclist.get(i).get("id").equals(Long.toString(latest))){
        			awesomePager.setCurrentItem(i,true);
        			return;
        		}      			
        	}
	    }
		else if(view.getId() == R.id.goto_first) {
			awesomePager.setCurrentItem(0, true);
		}
		else if (view.getId() == R.id.goto_last) {
			if (awesomePager.getCurrentItem() == awesomeAdapter.getCount()-1) {
				new FetchAndUpdate().execute(baseurl + nextUrl);
			}else {
				awesomePager.setCurrentItem(awesomeAdapter.getCount()-1,true);
			}
	    }
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int page) {
		Log.d(TAG, "OnPageSelected:" + page + ",pageCount:"+awesomeAdapter.getCount());
		if(page == 0) {
	        Button firstButton = ((Button) rootView.findViewById(R.id.goto_first));
	        firstButton.setVisibility(View.INVISIBLE);
		}else {
			Button firstButton = ((Button) rootView.findViewById(R.id.goto_first));
            firstButton.setVisibility(View.VISIBLE);
	     }
		if (page == awesomeAdapter.getCount()-1) {
			Button lastButton = ((Button) rootView.findViewById(R.id.goto_last));
	        lastButton.setText(R.string.fetch_more);
		}else {
			Button lastButton = ((Button) rootView.findViewById(R.id.goto_last));
	        lastButton.setText(R.string.last);			
		}
		
	}
 }

