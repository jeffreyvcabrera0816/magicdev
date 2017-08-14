package com.tidalsolutions.magic89_9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.adapter.ActivePollItemsAdapter;
import com.tidalsolutions.magic89_9.polls.StaticPollVote;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey on 1/6/2016.
 */
public class ActivePollVote extends AppCompatActivity{

    TextView poll_id, poll_title, poll_votes, remaining;
    GridView listView;
    RelativeLayout rootLayout;
    Animation grid_animation, text_animation;
    String active_poll_votes, active_poll_title, active_poll_id, poll_choice_id, countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polls_active_item);
        grid_animation = AnimationUtils.loadAnimation(ActivePollVote.this, R.anim.grid_animation);
        active_poll_id = getIntent().getExtras().getString("active_poll_id");
        active_poll_title = getIntent().getExtras().getString("active_poll_title");
        active_poll_votes = getIntent().getExtras().getString("active_poll_votes");
        poll_choice_id = getIntent().getExtras().getString("poll_choice_id");
        countdown = getIntent().getExtras().getString("countdown");

        StaticPollVote.SetVoteID(poll_choice_id);

        getSupportActionBar().hide();
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        listView = (GridView) findViewById(R.id.active_polls_list_items);
        poll_title = (TextView) findViewById(R.id.active_poll_title);
        remaining = (TextView) findViewById(R.id.countdown);
        findViewById(R.id.or).setVisibility(View.GONE);

//        String str = "Hello I'm your String";
        String[] separated = countdown.toString().trim().split("\\s+");
        String splitted_string;

        if (Integer.parseInt(separated[0].toString()) > 0) {
            if (Integer.parseInt(separated[0].toString()) == 1) {
                splitted_string = separated[0] + " month";
            } else {
                splitted_string = separated[0] + " " + separated[1];
            }

        } else {
            if (Integer.parseInt(separated[2].toString()) == 1) {
                splitted_string = separated[2] + " day";
            } else {
                splitted_string = separated[2] + " " + separated[3];
            }
        }

        remaining.setText(splitted_string);
        poll_title.setText(active_poll_title);

        FontUtils.loadFont(ActivePollVote.this, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        new APIActivePollItems(ActivePollVote.this,  "/api/polls/"+active_poll_id).execute();

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

    class APIActivePollItems extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIActivePollItems(Context act, String api_url) {
            this.act = act;
            this.api_url = api_url;
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
//            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");

                if (status) {

                    JSONObject poll_data = jObj.getJSONObject("poll_data");
                    final Integer poll_id = poll_data.getInt("id");

                    List<Integer> id_list_arr = new ArrayList<Integer>();
                    List<String> name_list_arr = new ArrayList<String>();
                    List<String> image_list_arr = new ArrayList<String>();
                    List<Integer> votes_list_arr = new ArrayList<Integer>();

                    JSONArray choices = poll_data.getJSONArray("choices");

                    for (int i = 0; i < choices.length(); i++) {
                        JSONObject poll_items = choices.getJSONObject(i);
                        Integer id = poll_items.getInt("id");
                        String name = poll_items.getString("name");
                        String image = poll_items.getString("image");
                        Integer votes = poll_items.getInt("votes");

                        id_list_arr.add(id);
                        name_list_arr.add(name);
                        image_list_arr.add(image);
                        votes_list_arr.add(votes);

                    }

                    final Integer id_list[] = id_list_arr.toArray(new Integer[id_list_arr.size()]);
                    String name_list[] = name_list_arr.toArray(new String[name_list_arr.size()]);
                    String image_list[] = image_list_arr.toArray(new String[image_list_arr.size()]);
                    Integer votes_list[] = votes_list_arr.toArray(new Integer[votes_list_arr.size()]);

                    if (choices.length() < 3) {
                        findViewById(R.id.or).setVisibility(View.VISIBLE);
                    }
                    ActivePollItemsAdapter activePollItemsAdapter = new ActivePollItemsAdapter(ActivePollVote.this, id_list, name_list, image_list, votes_list, poll_id, StaticPollVote.getVoteID());
                    listView.setAdapter(activePollItemsAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            view.startAnimation(grid_animation);
                            new APIPollItemVote(act, "/api/polls/"+poll_id+"/vote/"+ UserInfo.getUserID()+"/"+id_list[position], id_list[position]).execute();
                        }
                    });

                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    class APIPollItemVote extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;
        Integer poll_choice_id;

        public APIPollItemVote(Context act, String api_url, Integer poll_choice_id) {
            this.act = act;
            this.api_url = api_url;
            this.poll_choice_id = poll_choice_id;
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
//            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");
                String message = jObj.getString("message");

                if (status) {
                    StaticPollVote.SetVoteID(poll_choice_id.toString());
                    new APIActivePollItems(ActivePollVote.this,  "/api/polls/"+active_poll_id).execute();
                } else {
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings1 = getSharedPreferences("PREFS_NAME", 0);
        String id = settings1.getString("user_id", "");
        String image = settings1.getString("image", "");
        String firstname = settings1.getString("firstname", "");
        String mobile = settings1.getString("mobile", "");
        String birthday = settings1.getString("birthday", "");
        UserInfo.SetUserID(id);
        UserInfo.SetFirstname(firstname);
        UserInfo.SetBirtdate(birthday);
        UserInfo.SetMobile(mobile);
        UserInfo.SetImage(image);
    }

}
