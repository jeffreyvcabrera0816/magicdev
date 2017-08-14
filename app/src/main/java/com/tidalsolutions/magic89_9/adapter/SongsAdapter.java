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
import com.tidalsolutions.magic89_9.bio.SongDetails;

public class SongsAdapter extends android.widget.BaseAdapter {
    private Context context;
    private String[] song_title_list, artist_name_list, album_name_list, album_image_list, likes_list, lyrics_list;
    private Integer[] id_list, artist_id_list, album_id_list;
    private AQuery aq;

    public SongsAdapter(Context context, Integer[] id_list, String[] song_title_list, Integer[] artist_id_list, String[] artist_name_list, Integer[] album_id_list, String[] album_name_list, String[] album_image_list, String[] likes_list, String[] lyrics_list) {
        this.context = context;
        this.id_list = id_list;
        this.song_title_list = song_title_list;
        this.artist_name_list = artist_name_list;
        this.album_name_list = album_name_list;
        this.album_image_list = album_image_list;
        this.likes_list = likes_list;
        this.lyrics_list = lyrics_list;
        this.artist_id_list = artist_id_list;
        this.album_id_list = album_id_list;
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
        LinearLayout song_details_row;
        ImageView img;
        TextView title;
        TextView artist_name;

        ViewHolder(View view) {
            song_details_row = (LinearLayout) view.findViewById(R.id.song_details_row);
            img = (ImageView) view.findViewById(R.id.album_image);
            title = (TextView) view.findViewById(R.id.song_title);
            artist_name = (TextView) view.findViewById(R.id.artist);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.bio_songs_list_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();

        }

        this.aq = new AQuery(v);
        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.song_details_row);

        if (!album_image_list[position].equals("")) {
            String imgaq = Settings.base_url + "/assets/images/songs/" + album_image_list[position];
            aq.id(holder.img).image(imgaq, false, true);
        } else {
            holder.img.setImageResource(R.drawable.blank_person);
        }

        holder.title.setText(song_title_list[position]);
        holder.artist_name.setText(artist_name_list[position]);

        holder.song_details_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SongDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("song_id", id_list[position]);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return v;
    }

}