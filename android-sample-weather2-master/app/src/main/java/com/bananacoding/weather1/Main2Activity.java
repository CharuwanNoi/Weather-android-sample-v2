package com.bananacoding.weather1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bananacoding.weather1.helper.RSSParser;
import com.bananacoding.weather1.model.RSSWeather;

public class Main2Activity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static String weather_url;

    RSSParser parser1 = new RSSParser();
    RSSWeather weather2;
    TextView textView;
    public static final String EXTRA_MESSAGE = "ID" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText txt_lat = (EditText) findViewById(R.id.txt_lat);
                String message1 = txt_lat.getText().toString();

                EditText txt_long = (EditText) findViewById(R.id.txt_long);
                String message2 = txt_long.getText().toString();

                double latitudeNumber = 0.0,longitudeNumber = 0.0;

                if ( message2.equals("") || message2.equals("")) {

                    AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
                    alertDialog.setMessage("Please enter your latitude and longitude");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.show();

                }else {

                    try {
                         latitudeNumber = Double.parseDouble(message1);
                         longitudeNumber = Double.parseDouble(message2);

                        if (latitudeNumber < -90 || latitudeNumber >90 || longitudeNumber < -180 || longitudeNumber > 180) {
                            AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
                            alertDialog.setMessage(" latitude -90 to 90 and longitude -180 to 180 only");

                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alertDialog.show();
                        }else {
                            String weather1_url = ("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text=%22"+String.valueOf(latitudeNumber)+","+String.valueOf(longitudeNumber)+"%22%20and%20gflags=%22R%22");

                            new loadRSSFeedItems().execute(weather1_url);
                        }

                    }catch (NumberFormatException e) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Main2Activity.this).create();
                                alertDialog.setMessage("Enter number only ");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                // Showing Alert Message
                                alertDialog.show();

                    }
                }

            }
            class loadRSSFeedItems extends AsyncTask<String, String, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(Main2Activity.this);
                    pDialog.setMessage("Loading weather...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                /**
                 * getting all recent data and showing them in text view.
                 */
                @Override
                protected String doInBackground(String... args) {
                    // rss link url
                    String query_url = args[0];

                    // weather object of rss.
                    weather2 = parser1.getLatLong(query_url);

                    // updating UI from Background Thread
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Intent intent = new Intent(Main2Activity.this, MainActivity.class);

                                String description1 = weather2.getwoeid();
                                intent.putExtra(EXTRA_MESSAGE, description1);
                                startActivity(intent);

                        }
                    });


                    return null;
                }

                /**
                 * After completing background task Dismiss the progress dialog
                 **/
                protected void onPostExecute(String args) {
                    // dismiss the dialog after getting all products
                    pDialog.dismiss();
                }
            }
        });

        }
    }
