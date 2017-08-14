package com.tidalsolutions.magic89_9.polls;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;
import com.tidalsolutions.magic89_9.UserInfo;
import com.tidalsolutions.magic89_9.adapter.ClosedPollsAdapter;

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

public class ClosedPolls extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    GridView gridView;
    LinearLayout rootLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polls_closed_grid);
        setupActionBar();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeActivity);
        swipeRefreshLayout.setOnRefreshListener(this);

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        gridView = (GridView) findViewById(R.id.list_closed_polls);

        FontUtils.loadFont(ClosedPolls.this, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        new ClosedPollsAPI(ClosedPolls.this, "/api/polls/closed/user/"+UserInfo.getUserID()).execute();
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

    @Override
    public void onRefresh() {
        new ClosedPollsAPI(ClosedPolls.this, "/api/polls/closed/user/"+UserInfo.getUserID()).execute();
    }

    class ClosedPollsAPI extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public ClosedPollsAPI(Context act, String api_url) {
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
                    List<Integer> id_list_arr = new ArrayList<Integer>();
                    List<String> title_list_arr = new ArrayList<String>();
                    List<String> date_end_list_arr = new ArrayList<String>();
                    List<Integer> total_votes_list_arr = new ArrayList<Integer>();
                    List<String> poll_choice_id_arr = new ArrayList<String>();

                    JSONArray polls_data = jObj.optJSONArray("polls_data");

                    for (int i = 0; i < polls_data.length(); i++) {
                        JSONObject poll_data = polls_data.getJSONObject(i);
                        Integer id = poll_data.getInt("id");
                        String title = poll_data.getString("title");
                        String date_end = poll_data.getString("date_end");
                        Integer total_votes = poll_data.getInt("total_votes");
                        String poll_choice_id = poll_data.getString("poll_choice_id");

                        id_list_arr.add(id);
                        title_list_arr.add(title);
                        date_end_list_arr.add(date_end);
                        total_votes_list_arr.add(total_votes);
                        poll_choice_id_arr.add(poll_choice_id);

                    }

                    Integer id_list[] = id_list_arr.toArray(new Integer[id_list_arr.size()]);
                    String title_list[] = title_list_arr.toArray(new String[title_list_arr.size()]);
                    String date_end_list[] = date_end_list_arr.toArray(new String[date_end_list_arr.size()]);
                    Integer total_votes_list[] = total_votes_list_arr.toArray(new Integer[total_votes_list_arr.size()]);
                    String poll_choice_id_list[] = poll_choice_id_arr.toArray(new String[poll_choice_id_arr.size()]);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    ClosedPollsAdapter closedPollsAdapter = new ClosedPollsAdapter(act, id_list, title_list, date_end_list, total_votes_list, poll_choice_id_list);
                    gridView.setAdapter(closedPollsAdapter);

                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();
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
