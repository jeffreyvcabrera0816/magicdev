package com.tidalsolutions.magic89_9;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tidalsolutions.magic89_9.adapter.TopPollsAdapter;
import com.tidalsolutions.magic89_9.polls.ClosedPolls;

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
 * {@link PollsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PollsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PollsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View view;
    GridView listview;
    LinearLayout goto_active_polls, rootLayout, goto_closed_polls;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PollsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_polls, container, false);
        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        view.findViewById(R.id.top_polls_layout).setVisibility(View.GONE);
        listview = (GridView) view.findViewById(R.id.list_top_polls);
        goto_active_polls = (LinearLayout) view.findViewById(R.id.goto_active_polls);
        goto_closed_polls = (LinearLayout) view.findViewById(R.id.goto_closed_polls);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipePolls);
        swipeRefreshLayout.setOnRefreshListener(this);

        FontUtils.loadFont(getActivity(), "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        new TopPolls(getActivity(), "/api/polls/top/open/user/"+UserInfo.getUserID()).execute();

        goto_active_polls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivePollsGrid.class);
                getActivity().startActivity(intent);
            }
        });

        goto_closed_polls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClosedPolls.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        new TopPolls(getActivity(), "/api/polls/top/open/user/"+UserInfo.getUserID()).execute();
    }

    class TopPolls extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public TopPolls(Context act, String api_url) {
            this.act = act;
            this.api_url = api_url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.findViewById(R.id.top_polls_layout).setVisibility(View.GONE);
            view.findViewById(R.id.top_polls_progress).setVisibility(View.VISIBLE);
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
            view.findViewById(R.id.top_polls_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.top_polls_progress).setVisibility(View.GONE);
            if (pd != null) {
                pd.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");

                if (status) {
                    List<Integer> id_list_arr = new ArrayList<Integer>();
                    List<String> title_list_arr = new ArrayList<String>();
                    List<String> date_end_list_arr = new ArrayList<String>();
                    List<Integer> total_votes_list_arr = new ArrayList<Integer>();
                    List<String> poll_choice_id_list_arr = new ArrayList<String>();
                    List<String> countdown_list_arr = new ArrayList<String>();

                    JSONArray polls_data = jObj.optJSONArray("polls_data");

                    for (int i = 0; i < polls_data.length(); i++) {
                        JSONObject poll_data = polls_data.getJSONObject(i);
                        Integer id = poll_data.getInt("id");
                        Integer done = poll_data.getInt("done");
                        String title = poll_data.getString("title");
                        String date_end = poll_data.getString("date_end");
                        Integer total_votes = poll_data.getInt("total_votes");
                        String poll_choice_id = poll_data.getString("poll_choice_id");
                        String countdown;
                        if (done.equals(0)) {
                            countdown = poll_data.getString("countdown");
                        } else {
                            countdown = "0 months 0 days 0 hours 0 minutes 0 seconds";
                        }

                        id_list_arr.add(id);
                        countdown_list_arr.add(countdown);
                        title_list_arr.add(title);
                        date_end_list_arr.add(date_end);
                        total_votes_list_arr.add(total_votes);
                        poll_choice_id_list_arr.add(poll_choice_id);

                    }

                    Integer id_list[] = id_list_arr.toArray(new Integer[id_list_arr.size()]);
                    String title_list[] = title_list_arr.toArray(new String[title_list_arr.size()]);
                    String date_end_list[] = date_end_list_arr.toArray(new String[date_end_list_arr.size()]);
                    String poll_choice_id_list[] = poll_choice_id_list_arr.toArray(new String[poll_choice_id_list_arr.size()]);
                    Integer total_votes_list[] = total_votes_list_arr.toArray(new Integer[total_votes_list_arr.size()]);
                    String countdown_list[] = countdown_list_arr.toArray(new String[countdown_list_arr.size()]);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
//                    Toast.makeText(act, countdown_list_arr.toString(), Toast.LENGTH_SHORT).show();
                    TopPollsAdapter topPollsAdapter = new TopPollsAdapter(act, id_list, title_list, date_end_list, total_votes_list, poll_choice_id_list, countdown_list);
                    listview.setAdapter(topPollsAdapter);

                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

}


