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

import com.androidquery.AQuery;
import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.ForumDetails;
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;

public class ForumAdapter extends android.widget.BaseAdapter {
    private AQuery aq;
    private Context context;
    private String[] threadTitle, threadDetail, threadImage, userFirstname, userLastname, threadDateCreated;
    private Integer[] userReplies, threadID, threadLike;

    public ForumAdapter(Context context, String[] threadTitle, String[] threadDetail, String[] threadImage, String[] userFirstname, String[] userLastname, String[] threadDateCreated, Integer[] userReplies, Integer[] threadID, Integer[] threadLike) {
        this.context = context;
        this.threadTitle = threadTitle;
        this.threadDetail = threadDetail;
        this.threadImage = threadImage;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.threadDateCreated = threadDateCreated;
        this.userReplies = userReplies;
        this.threadID = threadID;
        this.threadLike = threadLike;
    }

    @Override
    public int getCount() {
        return threadTitle.length;
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
        ImageView img;
        TextView txtTitle;
        TextView threadBy;
        TextView dateCreated;
        TextView replies;
        ImageView msg_img;
        LinearLayout row;

        ViewHolder(View v) {
            img = (ImageView) v.findViewById(R.id.forumImage);
            txtTitle = (TextView) v.findViewById(R.id.forum_titles);
            threadBy = (TextView) v.findViewById(R.id.threadBy);
            dateCreated = (TextView) v.findViewById(R.id.date_created);
            replies = (TextView) v.findViewById(R.id.replies);
            msg_img = (ImageView) v.findViewById(R.id.msg_img);
            row = (LinearLayout) v.findViewById(R.id.forum_row);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.forum_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        this.aq = new AQuery(v);
        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.row);

        if (!threadImage[position].equals("")) {
            String imgaq = Settings.base_url + "/assets/images/users/" + threadImage[position];
            aq.id(holder.img).image(imgaq, false, true);
        } else {
            holder.img.setImageResource(R.mipmap.placeholder_profilephoto);
        }

        String reply;
        if (userReplies[position] > 0) {
            reply = userReplies[position].toString();
        } else {
            reply = "";
        }

        holder.threadBy.setText((userFirstname[position] + " " + userLastname[position]).toString());
        holder.txtTitle.setText(threadTitle[position]);
        holder.dateCreated.setText(threadDateCreated[position]);
        holder.replies.setText(reply);

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ForumDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("threadID", threadID[position]);
                bundle.putInt("likes", threadLike[position]);
                bundle.putString("threadUserImage", threadImage[position]);
                bundle.putString("threadTitle", threadTitle[position]);
                bundle.putString("threadDetail", threadDetail[position]);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        return v;
    }

}