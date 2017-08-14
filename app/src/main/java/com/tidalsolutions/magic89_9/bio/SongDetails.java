package com.tidalsolutions.magic89_9.bio;

import android.app.Activity;
import android.app.ProgressDialog;
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
public class SongDetails extends AppCompatActivity {
    private AQuery aq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_song_details);

        this.aq = new AQuery(SongDetails.this);
        Integer song_id = getIntent().getExtras().getInt("song_id");
//        setupActionBar();
        getSupportActionBar().hide();
        ScrollView root = (ScrollView) findViewById(R.id.root_layout);
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Light.otf");
        FontUtils.setFont(root);

        new APIArtistDetails(SongDetails.this,  "/api/songs/"+song_id).execute();

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
//                NavUtils.navigateUpFromSameTask(this);
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
                    JSONObject song_data = jObj.getJSONObject("song_data");
                    Integer id = song_data.getInt("id");
                    String title = song_data.getString("title");
                    String artist_name = song_data.getString("artist_name");
                    String album_name = song_data.getString("album_name");
                    String album_image = song_data.getString("album_image");
                    String likes = song_data.getString("likes");
                    String lyrics = song_data.getString("lyrics");
                    String cover = song_data.getString("lyrics");

                    TextView song_detail_title = (TextView) findViewById(R.id.song_detail_title);
                    ImageView song_detail_album_image = (ImageView) findViewById(R.id.song_detail_album_image);
                    TextView song_detail_album_name = (TextView) findViewById(R.id.song_detail_album_name);
//                    TextView song_detail_likes = (TextView) findViewById(R.id.song_detail_likes);
                    TextView song_detail_lyrics = (TextView) findViewById(R.id.song_detail_lyrics);
                    TextView song_detail_artist = (TextView) findViewById(R.id.song_detail_artist);

                    if (!album_image.equals("")) {
                        String imgaq = Settings.base_url + "/assets/images/song_cover/" + album_image;
                        aq.id(R.id.song_detail_album_image).image(imgaq, false, true);
                    } else {
                        song_detail_album_image.setImageResource(R.drawable.blank_person);
                    }

                    song_detail_title.setText(title);
//                    song_detail_album_image.setText("album image: "+album_image);
                    song_detail_album_name.setText(album_name);
//                    song_detail_likes.setText(likes + " Likes");
                    song_detail_lyrics.setText(lyrics);
                    song_detail_artist.setText(artist_name);
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
