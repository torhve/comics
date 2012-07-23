package com.torhve.comics;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.torhve.comics.backend.ComicBackend;
import com.torhve.comics.backend.JasonHandler;
import com.torhve.comics.dummy.DummyContent;

public class ComicListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks = sComicCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    

    public interface Callbacks {

        public void onItemSelected(String id);
    }

    private static Callbacks sComicCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    public ComicListFragment() {	
        

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        new FetchAndUpdate().execute("https://comics.io/api/v1/comics/?format=json&my=true&"+ ((ComicListActivity)getActivity()).getApiKey());

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }        

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sComicCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        //mCallbacks.onItemSelected(ComicBackend..get(position).id);
        mCallbacks.onItemSelected(ComicBackend.get(id));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
    private class FetchAndUpdate extends AsyncTask<String, Void, ArrayList<String>> {
		protected ArrayList<String> doInBackground(String... params) {
        	
        	
        	 /*
            setListAdapter(new ArrayAdapter<ComicContent.ComicItem>(getActivity(),
                    R.layout.simple_list_item_activated_1,
                    R.id.text1,
                    ComicContent.ITEMS));
                    */
        	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        	ArrayList<String> mylist2 = new ArrayList<String>();

        	//Get the data (see above)
        	JSONObject json =
        		JasonHandler.getJSONfromURL(params[0]);

           try{
        		//Get the element that holds the earthquakes ( JSONArray )
        	   Log.d("JSON", json.toString());
               //JSONArray parsedArray = new JSONArray(json.toString());
               //JSONArray pics = parsedArray.getJSONObject(0).getJSONObject("data").getJSONArray("children");
        	   JSONArray comics = json.getJSONArray("objects");

        		   //Loop the Array
        	        for(int i=0;i < comics.length();i++){						
        	        	JSONObject c = comics.getJSONObject(i);

        	        	mylist2.add(c.getString("name"));

        	        	/*HashMap<String, String> map = new HashMap<String, String>();
        	        	JSONObject e = earthquakes.getJSONObject(i);

        	        	map.put("id",  String.valueOf(i));
        	        	map.put("name", "Earthquake name:" + e.getString("eqid"));
        	        	map.put("magnitude", "Magnitude: " +  e.getString("magnitude"));
        	        	mylist.add(map);*/
        	        }

        	       }catch(JSONException e)        {
        	       	 Log.e("log_tag", "Error parsing data "+e.toString());
        	       }catch(NullPointerException e) {
        	    	   Log.e("log_tag", "null from json "+e.toString());
        	       }
		return mylist2;

    		    /*ListAdapter adapter = new SimpleAdapter(this, mylist , R.layout.simple_list_item_activated_1,
    		            new String[] { "name" },
    		            new int[] { R.id.text1 });
        	       
    			setListAdapter(adapter);
    						*/

    			//final ListView lv = getListView();
    			/*lv.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				@SuppressWarnings("unchecked")
    				HashMap<String, String> o = (HashMap<String, String>) lv.getItemAtPosition(position);
    				Toast.makeText(Main.this, "ID '" + o.get("id") + "' was clicked.", Toast.LENGTH_SHORT).show(); 
    			
    			}
    			});*/      
           }


        /*protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }*/

        protected void onPostExecute(ArrayList<String> mylist2) {
                    Context context = ComicListFragment.this.getActivity().getApplicationContext();
            	    setListAdapter(new ArrayAdapter<String>(context,
        	                android.R.layout.simple_list_item_activated_1,
        	                android.R.id.text1,
        	                mylist2));

        }
    }

}
