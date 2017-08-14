package com.tidalsolutions.magic89_9.bio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jeffrey on 1/8/2016.
 */
public class ArtistDetails extends AppCompatActivity {
    private AQuery aq;
    Typeface Boldface, Lightface, Regularface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_artist_details);
        this.aq = new AQuery(ArtistDetails.this);
//        setupActionBar();
        getSupportActionBar().hide();
        Integer artist_id = getIntent().getExtras().getInt("artist_id");

        Boldface = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Lightface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        Regularface = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.otf");

        ScrollView root = (ScrollView) findViewById(R.id.root_layout);
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Light.otf");
        FontUtils.setFont(root);
//        titleHeader.setTypeface(Boldface);
//        btnRegister.setTypeface(Lightface);

        new APIArtistDetails(ArtistDetails.this,  "/api/shows/"+artist_id).execute();

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so longf
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class APIArtistDetails extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url;

        public APIArtistDetails (Activity act, String api_url) {
            this.act = act;
            this.api_url = api_url;

        }
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient;
            HttpPost httppost;
            HttpResponse response;
            HttpEntity entity;
            InputStream isr = null;
            String result = "";
            String test_url = api_url + "/";
            String url = Settings.base_url + test_url;

            try {
                httpclient = new DefaultHttpClient();
                httppost = new HttpPost(url);
                response = httpclient.execute(httppost);
                entity = response.getEntity();
                isr = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                isr.close();

                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result" + e.toString());
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(act);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
//            Toast.makeText(act.getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String  service = jObj.getString("service");


                if (status) {
                    JSONObject artist_data = jObj.getJSONObject("show_data");
                    Integer id = artist_data.getInt("id");
                    String name = artist_data.getString("name");
                    String description = artist_data.getString("description");
                    String time = artist_data.getString("time");
                    String image_cover = artist_data.getString("cover");

                    TextView artist_detail_name = (TextView) findViewById(R.id.artist_detail_name);
                    TextView artist_detail_description = (TextView) findViewById(R.id.artist_detail_description);
                    TextView detail_time = (TextView) findViewById(R.id.show_detail_time);
                    ImageView artist_detail_image = (ImageView) findViewById(R.id.artist_detail_image);

                    if (!image_cover.equals("")) {
                        String imgaq = Settings.base_url + "/assets/images/show_cover/" + image_cover;
                        aq.id(R.id.artist_detail_image).image(imgaq, false, true);
                    } else {
                        artist_detail_image.setImageResource(R.drawable.blank_person);
                    }
                    artist_detail_name.setText(name);
                    detail_time.setText(time);
                    artist_detail_description.setText(description);
                }
                else {
                    String message = jObj.getString("error");
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

}
