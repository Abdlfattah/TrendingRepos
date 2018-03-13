package com.example.abdo.trendingrepos;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

// import univrsal image loader library
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // Declare variable
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listview;
    // Create an array to store data from JSON file
    ArrayList arraylist = new ArrayList<HashMap<String, String>>();
    // URL to get contacts JSON
    String urlStr = "https://api.github.com/search/repositories?q=created:>2017-10-22&sort=stars&order=desc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set listview
        listview = (ListView) findViewById(R.id.listview);
        // UNIVERSAL IMAGE LOADER SETUP
        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(android.R.drawable.stat_sys_download)
                .showImageForEmptyUri(android.R.drawable.ic_dialog_alert)
                .showImageOnFail(android.R.drawable.stat_notify_error)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //filled width
                .build();

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        new GetGithub_rep().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetGithub_rep extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(urlStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonarray = jsonObj.getJSONArray("items");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject jsonobject = jsonarray.getJSONObject(i);

                        // Retrive JSON Objects from items
                        map.put("rep_name", jsonobject.getString("name"));
                        map.put("nb_star", jsonobject.getString("stargazers_count"));
                        map.put("description", jsonobject.getString("description"));

                        // Owner node is JSON Object
                        JSONObject json_owner = jsonobject.getJSONObject("owner");
                        // Retrive JSON Objects from owner
                        map.put("owner_name", json_owner.getString("login"));
                        map.put("avatar", json_owner.getString("avatar_url"));

                        // Set the JSON Objects into the array
                        arraylist.add(map);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            // Pass the results into ListViewAdapter.java
            ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, arraylist);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
        }

    }
}
