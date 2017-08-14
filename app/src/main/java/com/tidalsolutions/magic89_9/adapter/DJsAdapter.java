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
import com.tidalsolutions.magic89_9.bio.DJDetails;

public class DJsAdapter extends android.widget.BaseAdapter {
    private Context context;
    private String[] dj_alias_list, first_name_list, last_name_list, gender_list, birthday_list, description_list, image_list;
    private Integer[] id_list;
    private AQuery aq;

    public DJsAdapter(Context context, Integer[] id_list, String[] dj_alias_list, String[] first_name_list, String[] last_name_list, String[] gender_list, String[] birthday_list, String[] description_list, String[] image_list) {
        this.context = context;
        this.id_list = id_list;
        this.dj_alias_list = dj_alias_list;
        this.first_name_list = first_name_list;
        this.last_name_list = last_name_list;
        this.gender_list = gender_list;
        this.birthday_list = birthday_list;
        this.description_list = description_list;
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
        LinearLayout dj_details_row;
        ImageView img;
        TextView alias;
        TextView desc;

        ViewHolder(View view) {
            dj_details_row = (LinearLayout) view.findViewById(R.id.dj_details_row);
            img = (ImageView) view.findViewById(R.id.dj_image);
            alias = (TextView) view.findViewById(R.id.dj_alias);
            desc = (TextView) view.findViewById(R.id.dj_desc);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = li.inflate(R.layout.bio_djs_list_row, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }
        this.aq = new AQuery(v);
        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.dj_details_row);

        if (!image_list[position].equals("")) {
            String imgaq = Settings.base_url + "/assets/images/djs/" + image_list[position];
            aq.id(holder.img).image(imgaq, false, true);
        } else {
            holder.img.setImageResource(R.drawable.blank_person);
        }


        holder.alias.setText(dj_alias_list[position]);
        holder.desc.setText(first_name_list[position] + " " + last_name_list[position]);

        holder.dj_details_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DJDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("dj_id", id_list[position]);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return v;
    }

}