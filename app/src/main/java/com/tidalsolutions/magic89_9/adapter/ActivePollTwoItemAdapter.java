package com.tidalsolutions.magic89_9.adapter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;
import com.tidalsolutions.magic89_9.UserInfo;

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
 * Created by Jeffrey on 2/18/2016.
 */
public class ActivePollTwoItemAdapter extends android.widget.BaseAdapter {
    private AQuery aq;
    private Context context;
    private String[] name_list, image_list;
    private Integer[] id_list, votes_list;
    private Integer poll_id;
    private String poll_choice_id;
    Animation grid_animation, text_animation;
    Integer isVoted = 0;

    public ActivePollTwoItemAdapter(Context context, Integer[] id_list, String[] name_list, String[] image_list, Integer[] votes_list, Integer poll_id, String poll_choice_id) {
        this.context = context;
        this.name_list = name_list;
        this.image_list = image_list;
        this.id_list = id_list;
        this.votes_list = votes_list;
        this.poll_id = poll_id;
        this.poll_choice_id = poll_choice_id;
    }

    @Override
    public int getCount() {
        return id_list.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.polls_active_two_item_row, parent, false);

        } else {
            v = convertView;
        }
        this.aq = new AQuery(v);

        grid_animation = AnimationUtils.loadAnimation(context, R.anim.grid_animation);
        text_animation = AnimationUtils.loadAnimation(context, R.anim.text_animation);

        LinearLayout rootLayout = (LinearLayout) v.findViewById(R.id.root_layout);
        final ImageView checkVote = (ImageView) v.findViewById(R.id.vote_check);
        TextView active_poll_item_name = (TextView) v.findViewById(R.id.active_poll_item_name);
        final TextView active_poll_item_votes = (TextView) v.findViewById(R.id.active_poll_item_votes);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        if ((id_list[position].toString()).equals(poll_choice_id)) {
            checkVote.setImageResource(R.mipmap.check_dark_yellow);
            isVoted = 1;
        }

        String imgaq = Settings.base_url + "/assets/images/polls/" + image_list[position];
        aq.id(R.id.poll_item_image).image(imgaq, false, true);
        active_poll_item_name.setText(name_list[position]);
        active_poll_item_votes.setText(votes_list[position].toString());

        v.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isVoted.equals(1)) {
                    Toast.makeText(context, "You already voted in this Poll", Toast.LENGTH_LONG).show();
                } else {
                    v.startAnimation(grid_animation);
                    Integer votes = votes_list[position] + 1;
                    active_poll_item_votes.setText(votes.toString());
                    checkVote.setImageResource(R.mipmap.check_dark_yellow);
                    new APIPollItemVote(context, "/api/polls/"+poll_id+"/vote/"+ UserInfo.getUserID()+"/"+id_list[position]).execute();
                    isVoted = 1;
                }

            }
        });

        return v;
    }

    class APIPollItemVote extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIPollItemVote(Context act, String api_url) {
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
                    isVoted = 1;
//                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } else {
                    isVoted = 1;
//                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }
}
