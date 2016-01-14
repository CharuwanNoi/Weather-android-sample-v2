package com.bananacoding.weather1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bananacoding.weather1.helper.RSSParser;
import com.bananacoding.weather1.model.RSSWeather;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;
    private static String weather_url;
    RSSParser parser = new RSSParser();
    RSSWeather weather;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //create an instance of the view object.
        textView = (TextView) findViewById(R.id.text_view);

        Intent intent = getIntent();
        String description = intent.getStringExtra(Main2Activity.EXTRA_MESSAGE);

        //Log.d("Lat", description);
        //"http://weather.yahooapis.com/forecastrss?w=12756339&u=c"
        String weather_url = ("http://weather.yahooapis.com/forecastrss?w="+ description +"&u=c");
        /**
         * Calling a backgroung thread will loads recent data of a website
         * @param rss url of website
         * */
        new loadRSSFeedItems().execute(weather_url);

    }

    /**
     * Background Async Task to get RSS data from URL
     * */
    class loadRSSFeedItems extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(
                    MainActivity.this);
            pDialog.setMessage("Loading weather...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all recent data and showing them in text view.
         * */
        @Override
        protected String doInBackground(String... args) {
            // rss link url
            String rss_url = args[0];

            // weather object of rss.
            weather = parser.getRSSFeedWeather(rss_url);

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        String description = String.format("title: %s \n pubdate: %s \n temp: %s \n link: %s", weather.getTitle(), weather.getPubdate(), weather.getTemp(), weather.getLink());
                        textView.setText(description);
                    }catch (NullPointerException e){
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

                        alertDialog.setMessage("Country not found");

                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                    }
                }
            });
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }
    }
}
