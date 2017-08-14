package com.tidalsolutions.magic89_9.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Created by Jeffrey on 1/7/2016.
 */
public class ActivePollItemsAdapter extends android.widget.BaseAdapter {
    private AQuery aq;
    private Context context;
    private String[] name_list, image_list;
    private Integer[] id_list, votes_list;
    private Integer poll_id;
    private String poll_choice_id;
    Animation grid_animation, text_animation;
    Integer isVoted = 0;

    public ActivePollItemsAdapter(Context context, Integer[] id_list, String[] name_list, String[] image_list, Integer[] votes_list, Integer poll_id, String poll_choice_id) {
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

    class ViewHolder {
        LinearLayout rootLayout;
        ImageView checkVote, poll_item_image;
        TextView active_poll_item_name;
        TextView active_poll_item_votes;

        ViewHolder(View v) {
            poll_item_image = (ImageView) v.findViewById(R.id.poll_item_image);
            rootLayout = (LinearLayout) v.findViewById(R.id.root_layout);
            checkVote = (ImageView) v.findViewById(R.id.vote_check);
            active_poll_item_name = (TextView) v.findViewById(R.id.active_poll_item_name);
            active_poll_item_votes = (TextView) v.findViewById(R.id.active_poll_item_votes);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.polls_active_items_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        this.aq = new AQuery(v);

        grid_animation = AnimationUtils.loadAnimation(context, R.anim.grid_animation);
        text_animation = AnimationUtils.loadAnimation(context, R.anim.text_animation);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.rootLayout);

        if (id_list[position].toString().equals(poll_choice_id)) {
            holder.checkVote.setImageResource(R.mipmap.check_dark_yellow);
        } else {
            holder.checkVote.setImageResource(0);
        }

        String imgaq = Settings.base_url + "/assets/images/polls/" + image_list[position];
        aq.id(holder.poll_item_image).image(imgaq, false, true);
        holder.active_poll_item_name.setText(name_list[position]);
        holder.active_poll_item_votes.setText(votes_list[position].toString());

        return v;
    }
}
