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
public class DJDetails extends AppCompatActivity {
    private AQuery aq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_dj_details);
//        setupActionBar();
        getSupportActionBar().hide();
        this.aq = new AQuery(this);
        Integer dj_id = getIntent().getExtras().getInt("dj_id");

        ScrollView root = (ScrollView) findViewById(R.id.root_layout);
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Light.otf");
        FontUtils.setFont(root);

        new APIArtistDetails(DJDetails.this,  "/api/djs/"+dj_id).execute();

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
                    JSONObject dj_data = jObj.getJSONObject("dj_data");
                    Integer id = dj_data.getInt("id");
                    String alias = dj_data.getString("dj_alias");
                    String first_name = dj_data.getString("first_name");
                    String last_name = dj_data.getString("last_name");
                    String birthday = dj_data.getString("birthday");
                    String gender = dj_data.getString("gender");
                    String description = dj_data.getString("description");
                    String image = dj_data.getString("image");
                    String cover = dj_data.getString("cover");

                    TextView dj_detail_name = (TextView) findViewById(R.id.dj_detail_name);
                    TextView dj_detail_description = (TextView) findViewById(R.id.dj_detail_description);
//                    TextView dj_detail_alias = (TextView) findViewById(R.id.dj_detail_alias);
//                    TextView dj_detail_bday = (TextView) findViewById(R.id.dj_detail_bday);
//                    TextView dj_detail_gender = (TextView) findViewById(R.id.dj_detail_gender);
                    ImageView dj_detail_image = (ImageView) findViewById(R.id.dj_detail_image);

                    if (!cover.equals("")) {
                        String imgaq = Settings.base_url + "/assets/images/dj_cover/" + cover;
                        aq.id(dj_detail_image).image(imgaq, false, true);
                    } else {
                        dj_detail_image.setImageResource(R.drawable.blank_person);
                    }

                    dj_detail_name.setText(first_name + " " + last_name);
//                    dj_detail_alias.setText(alias);
//                    dj_detail_bday.setText(birthday);
//                    dj_detail_gender.setText(gender);
                    dj_detail_description.setText(description);
                }
                else {
                    String message = jObj.getString("error");
                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

}
