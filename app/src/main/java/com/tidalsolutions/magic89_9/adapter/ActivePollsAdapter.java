package com.tidalsolutions.magic89_9.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tidalsolutions.magic89_9.ActivePollVote;
import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;

/**
 * Created by Jeffrey on 1/7/2016.
 */
public class ActivePollsAdapter extends android.widget.BaseAdapter{
    private Context context;
    private String[] title_list, date_end_list, poll_choice_id_list, countdown_list;
    private Integer[] id_list, total_votes_list;

    public ActivePollsAdapter(Context context, Integer[] id_list, String[] title_list, String[] date_end_list, Integer[] total_votes_list, String[] poll_choice_id_list, String[] countdown_list ) {
        this.context = context;
        this.title_list = title_list;
        this.date_end_list = date_end_list;
        this.id_list = id_list;
        this.total_votes_list = total_votes_list;
        this.poll_choice_id_list = poll_choice_id_list;
        this.countdown_list = countdown_list;
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

            v = li.inflate(R.layout.polls_active_grid, parent, false);

        } else {
            v = convertView;

        }

        final LinearLayout btn_top_polls = (LinearLayout) v.findViewById(R.id.top_poll);

        btn_top_polls.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, ActivePollVote.class);
                Bundle bundle = new Bundle();
                bundle.putString("active_poll_id", id_list[position].toString());
                bundle.putString("active_poll_title", title_list[position]);
                bundle.putString("active_poll_votes", total_votes_list[position].toString());
                bundle.putString("poll_choice_id", poll_choice_id_list[position].toString());
                bundle.putString("countdown", countdown_list[position]);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

        LinearLayout rootLayout = (LinearLayout) v.findViewById(R.id.top_poll);
        final ImageView checkVote = (ImageView) v.findViewById(R.id.vote_check);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.layout_margin);
        TextView top_poll_title = (TextView) v.findViewById(R.id.top_list_title);
        TextView top_poll_votes = (TextView) v.findViewById(R.id.top_list_votes);
//        TextView top_poll_date_end = (TextView) v.findViewById(R.id.top_list_date_end);
//        ImageView top_poll_image = (ImageView) v.findViewById(R.id.top_polls_image);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)linearLayout.getLayoutParams();

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);

        if ((position % 3) == 0 ) {
            params.setMargins(6, 6, 6, 0);
        } else {
            params.setMargins(0, 6, 6, 0);
        }

        if (!poll_choice_id_list[position].equals("null")) {
//            checkVote.setImageResource(R.mipmap.check_dark_yellow);
        }
        top_poll_title.setText(title_list[position]);
        top_poll_votes.setText(total_votes_list[position].toString());
//        top_poll_date_end.setText(date_end_list[position]);
//        top_poll_image.setImageResource(R.drawable.poll_folder);

        return v;
    }
}
