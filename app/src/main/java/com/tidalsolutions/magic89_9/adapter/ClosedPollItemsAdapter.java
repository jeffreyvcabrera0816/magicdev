package com.tidalsolutions.magic89_9.adapter;

import android.content.Context;
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

/**
 * Created by Jeffrey on 1/7/2016.
 */
public class ClosedPollItemsAdapter extends android.widget.BaseAdapter {
    private AQuery aq;
    private Context context;
    private String[] name_list, image_list;
    private Integer[] id_list, votes_list;
    private Integer poll_id;
    private String poll_choice_id;
    Animation grid_animation, text_animation;
    Integer isVoted = 0;

    public ClosedPollItemsAdapter(Context context, Integer[] id_list, String[] name_list, String[] image_list, Integer[] votes_list, Integer poll_id, String poll_choice_id) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.polls_active_items_row, parent, false);

        } else {
            v = convertView;
        }
        this.aq = new AQuery(v);

        grid_animation = AnimationUtils.loadAnimation(context, R.anim.grid_animation);
        text_animation = AnimationUtils.loadAnimation(context, R.anim.text_animation);

//        ImageView img = (ImageView) v.findViewById(R.id.poll_item_image);
//        img.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout rootLayout = (LinearLayout) v.findViewById(R.id.root_layout);
        final ImageView checkVote = (ImageView) v.findViewById(R.id.vote_check);
        TextView active_poll_item_name = (TextView) v.findViewById(R.id.active_poll_item_name);
        final TextView active_poll_item_votes = (TextView) v.findViewById(R.id.active_poll_item_votes);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

//        if ((id_list[position].toString()).equals(poll_choice_id)) {
//            checkVote.setImageResource(R.mipmap.check_dark_yellow);
//            isVoted = 1;
//        }

        String imgaq = Settings.base_url + "/assets/images/polls/" + image_list[position];
        aq.id(R.id.poll_item_image).image(imgaq, false, true);
        active_poll_item_name.setText(name_list[position]);
        active_poll_item_votes.setText(votes_list[position].toString());


        return v;
    }

}
