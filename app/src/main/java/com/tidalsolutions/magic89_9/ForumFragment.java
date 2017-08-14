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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.adapter.ForumAdapter;

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
 * Activities that contain this fragment must implement the
 * {@link ForumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class ForumFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    View view;
    RelativeLayout rootLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ForumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_forum, container, false);

        rootLayout = (RelativeLayout) view.findViewById(R.id.root_layout);
        listView = (ListView) view.findViewById(R.id.tile_title);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeThreads);
        swipeRefreshLayout.setOnRefreshListener(this);
        view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        FontUtils.loadFont(getActivity(), "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        new APIforum(getActivity(),  "/api/threads").execute();
        return view;
    }

    @Override
    public void onRefresh() {
        new APIforum(getActivity(),  "/api/threads").execute();
    }

    class APIforum extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIforum(Context act, String api_url) {
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
                    List<String> threadsTitles = new ArrayList<String>();
                    List<String> threadsDetails = new ArrayList<String>();
                    List<String> threadsImages = new ArrayList<String>();
                    List<Integer> threadsIDs = new ArrayList<Integer>();
                    List<Integer> threadsLikes = new ArrayList<Integer>();
                    List<String> threadsDateCreated = new ArrayList<String>();
                    List<String> usersFirstname = new ArrayList<String>();
                    List<String> usersLastname = new ArrayList<String>();
                    List<Integer> usersReplies = new ArrayList<Integer>();

                    JSONArray threads_data = jObj.optJSONArray("threads_data");

                    for (int i = 0; i < threads_data.length(); i++) {
                        JSONObject thread_data = threads_data.getJSONObject(i);
                        String thread_title = thread_data.optString("title");
                        String thread_detail = thread_data.optString("contents");
                        String thread_image = thread_data.optString("user_image");
                        String first_name = thread_data.optString("user_first_name");
                        String last_name = thread_data.optString("user_last_name");
                        String date_created = thread_data.optString("date_created");
                        Integer replies = thread_data.optInt("replies");
                        Integer thread_id = thread_data.optInt("id");
                        Integer thread_likes = thread_data.optInt("likes");

                        threadsTitles.add(thread_title);
                        threadsDetails.add(thread_detail);
                        threadsImages.add(thread_image);
                        usersFirstname.add(first_name);
                        usersLastname.add(last_name);
                        threadsDateCreated.add(date_created);
                        usersReplies.add(replies);
                        threadsIDs.add(thread_id);
                        threadsLikes.add(thread_likes);
                    }

                    String threadTitle[] = threadsTitles.toArray(new String[threadsTitles.size()]);
                    String threadDetail[] = threadsDetails.toArray(new String[threadsDetails.size()]);
                    String threadImage[] = threadsImages.toArray(new String[threadsImages.size()]);
                    String userFirstname[] = usersFirstname.toArray(new String[usersFirstname.size()]);
                    String userLastname[] = usersLastname.toArray(new String[usersLastname.size()]);
                    String threadDateCreated[] = threadsDateCreated.toArray(new String[threadsDateCreated.size()]);
                    Integer userReplies[] = usersReplies.toArray(new Integer[usersReplies.size()]);
                    Integer threadID[] = threadsIDs.toArray(new Integer[threadsIDs.size()]);
                    Integer threadLike[] = threadsLikes.toArray(new Integer[threadsLikes.size()]);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    ForumAdapter forumAdapter = new ForumAdapter(act, threadTitle, threadDetail, threadImage, userFirstname, userLastname, threadDateCreated, userReplies, threadID, threadLike);
                    listView.setAdapter(forumAdapter);

                } else {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    String message = jObj.getString("error");
                    if (message.equals("No Records Found")) {
                        listView.setAdapter(null);
//                        Toast.makeText(act, "No Threads Found", Toast.LENGTH_LONG).show();
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
