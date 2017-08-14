package com.tidalsolutions.magic89_9.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;

public class ForumDetailsAdapter extends android.widget.BaseAdapter {
    private Context context;
    private String[] username;
    private String[] content;
    private String[] date_created;
    private String[] user_image;
    private Integer threadID;
    private AQuery aq;

    public ForumDetailsAdapter(Context context, Integer threadID, String[] username, String[] content, String[] date_created, String[] user_image) {
        this.context = context;
        this.username = username;
        this.content = content;
        this.date_created = date_created;
        this.threadID = threadID;
        this.user_image = user_image;
    }

    @Override
    public int getCount() {
        return username.length;
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
        ImageView user_image;
        TextView comment, user_name, date_created;
        RelativeLayout rootLayout;

        ViewHolder(View v) {

            rootLayout = (RelativeLayout) v.findViewById(R.id.root_layout);
            user_image = (ImageView) v.findViewById(R.id.user_image);
            comment = (TextView) v.findViewById(R.id.comment);
            user_name = (TextView) v.findViewById(R.id.username);
            date_created = (TextView) v.findViewById(R.id.date_created);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.forum_details_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        this.aq = new AQuery(context);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.rootLayout);

        if (!user_image[position].equals("")) {
            String imgaq = Settings.base_url + "/assets/images/users/" + user_image[position];
            aq.id(holder.user_image).image(imgaq, false, true);
        } else {
            holder.user_image.setImageResource(R.mipmap.placeholder_profilephoto);
        }

        holder.date_created.setText(date_created[position]);
        holder.user_name.setText(username[position]);
        holder.comment.setText(content[position]);

        return v;
    }

}
