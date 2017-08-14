package com.tidalsolutions.magic89_9;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.adapter.HomeAdapter;

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
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) view.findViewById(R.id.activity_list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeActivity);
        swipeRefreshLayout.setOnRefreshListener(this);
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        new APIactivity(getActivity(),  "/api/activities/user/"+UserInfo.getUserID()).execute();
        return view;
    }

    @Override
    public void onRefresh() {
        new APIactivity(getActivity(),  "/api/activities/user/"+UserInfo.getUserID()).execute();
    }

    class APIactivity extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;
        boolean isZero;
        public APIactivity(Context act, String api_url) {
            this.act = act;
            this.api_url = api_url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
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
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if (pd != null) {
                pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");

                if (status) {
                    List<String> activityTitles = new ArrayList<String>();
                    List<String> activityDetails = new ArrayList<String>();
                    List<String> activityImages = new ArrayList<String>();
                    List<Integer> activityIDs = new ArrayList<Integer>();
                    List<Integer> activityLikes = new ArrayList<Integer>();
                    List<String> activityIsLiked = new ArrayList<String>();

                    JSONArray activities_data = jObj.optJSONArray("activities_data");

                    for (int i = 0; i < activities_data.length(); i++) {
                        JSONObject activity_data = activities_data.getJSONObject(i);
                        String activity_title = activity_data.optString("title");
                        String activity_detail = activity_data.optString("contents");
                        String activity_image = activity_data.optString("image");
                        Integer activity_id = activity_data.optInt("id");
                        Integer activity_likes = activity_data.optInt("likes");
                        String activity_is_liked = activity_data.optString("is_liked");
                        activityTitles.add(activity_title);
                        activityDetails.add(activity_detail);
                        activityImages.add(activity_image);
                        activityIDs.add(activity_id);
                        activityLikes.add(activity_likes);
                        activityIsLiked.add(activity_is_liked);
                    }

                    String activityTitle[] = activityTitles.toArray(new String[activityTitles.size()]);
                    String activityDetail[] = activityDetails.toArray(new String[activityDetails.size()]);
                    String activityImage[] = activityImages.toArray(new String[activityImages.size()]);
                    Integer acttivityID[] = activityIDs.toArray(new Integer[activityIDs.size()]);
                    Integer activityLike[] = activityLikes.toArray(new Integer[activityLikes.size()]);
                    String activityIsLike[] = activityIsLiked.toArray(new String[activityIsLiked.size()]);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    HomeAdapter homeAdapter = new HomeAdapter(act ,activityTitle, activityDetail, activityImage, acttivityID, activityLike, activityIsLike);
                    listView.setAdapter(homeAdapter);
                } else {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    String message = jObj.getString("error");
                    if (message.equals("No Records Found")) {
                        listView.setAdapter(null);
//                        Toast.makeText(act, "No Activity Found", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }
}
