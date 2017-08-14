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
import com.tidalsolutions.magic89_9.R;
import com.tidalsolutions.magic89_9.Settings;
import com.tidalsolutions.magic89_9.bio.ArtistDetails;

public class ArtistsAdapter extends android.widget.BaseAdapter {
    private Context context;
    private String[] name_list, time_list, image_list;
    private Integer[] id_list;
    private AQuery aq;

    public ArtistsAdapter(Context context, Integer[] id_list, String[] name_list, String[] time_list, String[] image_list) {
        this.context = context;
        this.id_list = id_list;
        this.name_list = name_list;
        this.time_list = time_list;
        this.image_list = image_list;
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
        LinearLayout artist_details_row;
        ImageView img;
        TextView name, time;

        ViewHolder(View view) {
            artist_details_row = (LinearLayout) view.findViewById(R.id.artist_details_row);
            img = (ImageView) view.findViewById(R.id.artist_image);
            name = (TextView) view.findViewById(R.id.artist_name);
            time = (TextView) view.findViewById(R.id.show_time);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.bio_artists_list_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        this.aq = new AQuery(v);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.artist_details_row);

        if (!image_list[position].equals("")) {
            String imgaq = Settings.base_url + "/assets/images/shows/" + image_list[position];
            aq.id(holder.img).image(imgaq, false, true);
        } else {
            holder.img.setImageResource(R.drawable.blank_person);
        }

//        img.setImageResource(R.mipmap.ariana);
        holder.time.setText(time_list[position]);
        holder.name.setText(name_list[position]);

        holder.artist_details_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("artist_id", id_list[position]);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return v;
    }

}