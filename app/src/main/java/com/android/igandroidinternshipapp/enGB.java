package com.android.igandroidinternshipapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public  class enGB extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;

    Animation slide_in_left, slide_out_right;
    Animation slide_in_right, slide_out_left;

    String JSONString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_gb);

        gestureDetector = new GestureDetectorCompat(this, new SwipeGestureDetector());
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(gestureDetector.onTouchEvent(event)){
            return true;
        }
        return super.onTouchEvent(event);
    }

//Implementing changing location (activities) by swiping functionality
//--------------------------------------------------------------

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ){
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                //LEFT SWIPE
                if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    enGB.this.onLeft();

                }

                //RIGHT SWIPE
                else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    enGB.this.onRight();

                }
            }
            catch (Exception e){
                Log.e("enGB", "Error on gestures");
            }
            return true;

        }
    }

    private void onLeft(){
        Intent myIntent = new Intent(getApplicationContext(), deDE.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        enGB.this.finish();
    }

    private void onRight(){
        Intent myIntent = new Intent(getApplicationContext(), frFR.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        enGB.this.finish();
    }

//Refreshing from menu button
//--------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//Fetching URL and getting data from JSON
//--------------------------------------------------------------

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL ("https://api.ig.com/deal/samples/markets/ANDROID_PHONE/en_GB/igi");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSONString = bufferedReader.readLine()) != null){
                stringBuilder.append(JSONString+ "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

//Sending information to lisview with CustomListAdapter
//--------------------------------------------------------------

        @Override
        protected void onPostExecute(String result)  {
            try {

                ListView listView = findViewById(R.id.listview);

                ArrayList<NewsItem> items = new ArrayList<>();
                JSONObject root = new JSONObject(result);

                JSONArray array = root.getJSONArray("markets");

                for (int i = 0; i < array.length(); i++) {
                    NewsItem item =  new NewsItem();
                    JSONObject object = array.getJSONObject(i);
                    item.setInstrumentName(object.getString("instrumentName"));
                    item.setDisplayOffer(object.getString("displayOffer"));
                    items.add(item);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(items, Comparator.comparing(NewsItem::getInstrumentName));
                }


                CustomListAdapter adapter = new CustomListAdapter(enGB.this, items);
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

//Creating an object with data from JSON
//--------------------------------------------------------------

    public class NewsItem {
        private String instrumentName;
        private String displayOffer;

        public String getInstrumentName() {
            return instrumentName;
        }

        public void setInstrumentName(String instrumentName) {
            this.instrumentName = instrumentName;
        }

        public String getDisplayOffer() {
            return displayOffer;
        }

        public void setDisplayOffer(String displayOffer) {
            this.displayOffer = displayOffer;
        }
}

//CustomListAdapter to send Array of objects to listview
//--------------------------------------------------------------

    public class CustomListAdapter extends BaseAdapter {
        private ArrayList<NewsItem> listData;
        private LayoutInflater layoutInflater;

        public CustomListAdapter(Context aContext, ArrayList<NewsItem> listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(aContext);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
                holder = new ViewHolder();
                holder.instrumentNameView = convertView.findViewById(R.id.instrumentName);
                holder.displayOfferView = convertView.findViewById(R.id.displayOffer);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.instrumentNameView.setText(listData.get(position).getInstrumentName());
            holder.displayOfferView.setText(listData.get(position).getDisplayOffer());
            return convertView;
        }

         public class ViewHolder {
            TextView instrumentNameView;
            TextView displayOfferView;
        }
    }
}










