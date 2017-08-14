package com.tidalsolutions.magic89_9.bio;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;
import com.tidalsolutions.magic89_9.adapter.DJsAdapter;

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
 * Created by Jeffrey on 12/21/2015.
 */
public class DJsListTakeOver extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_djs_list);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        setupActionBar();
        listView = (ListView) findViewById(R.id.djs_list);
        new APIDjsListTO(DJsListTakeOver.this,  "/api/djs/inactive").execute();
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

    @Override
    public void onRefresh() {
        new APIDjsListTO(DJsListTakeOver.this,  "/api/djs/inactive").execute();
    }

    class APIDjsListTO extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIDjsListTO(Context act, String api_url) {
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
                    List<String> dj_alias_list_arr = new ArrayList<String>();
                    List<String> first_name_list_arr = new ArrayList<String>();
                    List<String> last_name_list_arr = new ArrayList<String>();
                    List<String> gender_list_arr = new ArrayList<String>();
                    List<String> birthday_list_arr = new ArrayList<String>();
                    List<String> description_list_arr = new ArrayList<String>();
                    List<String> image_list_arr = new ArrayList<String>();

                    JSONArray djs_data = jObj.optJSONArray("djs_data");

                    for (int i = 0; i < djs_data.length(); i++) {
                        JSONObject dj_data = djs_data.getJSONObject(i);
                        Integer id = dj_data.getInt("id");
                        String dj_alias = dj_data.getString("dj_alias");
                        String first_name = dj_data.getString("first_name");
                        String last_name = dj_data.getString("last_name");
                        String gender = dj_data.getString("gender");
                        String birthday = dj_data.getString("birthday");
                        String description = dj_data.getString("description");
                        String image = dj_data.getString("image");

                        id_list_arr.add(id);
                        dj_alias_list_arr.add(dj_alias);
                        first_name_list_arr.add(first_name);
                        last_name_list_arr.add(last_name);
                        gender_list_arr.add(gender);
                        birthday_list_arr.add(birthday);
                        description_list_arr.add(description);
                        image_list_arr.add(image);
                    }

                    Integer id_list[] = id_list_arr.toArray(new Integer[id_list_arr.size()]);
                    String dj_alias_list[] = dj_alias_list_arr.toArray(new String[dj_alias_list_arr.size()]);
                    String first_name_list[] = first_name_list_arr.toArray(new String[first_name_list_arr.size()]);
                    String last_name_list[] = last_name_list_arr.toArray(new String[last_name_list_arr.size()]);
                    String gender_list[] = gender_list_arr.toArray(new String[gender_list_arr.size()]);
                    String birthday_list[] = birthday_list_arr.toArray(new String[birthday_list_arr.size()]);
                    String description_list[] = description_list_arr.toArray(new String[description_list_arr.size()]);
                    String image_list[] = image_list_arr.toArray(new String[image_list_arr.size()]);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    DJsAdapter djsAdapater = new DJsAdapter(act, id_list, dj_alias_list, first_name_list, last_name_list, gender_list, birthday_list, description_list, image_list);
                    listView.setAdapter(djsAdapater);

                } else {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    String message = jObj.getString("error");
                    if (message.equals("No Records Found")) {
                        listView.setAdapter(null);
                    }
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();


                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }
}
