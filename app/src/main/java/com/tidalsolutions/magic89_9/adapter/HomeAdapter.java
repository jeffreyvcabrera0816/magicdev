package com.tidalsolutions.magic89_9.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;

public class HomeAdapter extends android.widget.BaseAdapter {
    private AQuery aq;
    private Context context;
    private String[] title;
    private String[] detail;
    private String[] isLiked;
    private String[] image;
    private Integer[] id, likes;
    private int selectedItem = -1;
    private LayoutInflater inflater;
    Animation animation, text_animation;
    ArrayList<Integer> liked_positions = new ArrayList<Integer>();

    public HomeAdapter(Context context, String[] title, String[] detail, String[] image, Integer[] id, Integer[] likes, String[] isLiked) {
        this.context = context;
        this.title = title;
        this.detail = detail;
        this.image = image;
        this.id = id;
        this.likes = likes;
        this.isLiked = isLiked;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (image[position].equals("")) {
            return 0;
        } else if (title[position].equals("") && detail[position].equals("")) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelection(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    class ViewHolder {
        RelativeLayout rootLayout;
        TextView activityLikes;
        TextView activityTitle;
        TextView activityDetail;
        ImageView btnLike, activityImage, btnShare;

        ViewHolder(View view) {
            rootLayout = (RelativeLayout) view.findViewById(R.id.root_layout);
            activityLikes = (TextView)view.findViewById(R.id.activity_likes);
            activityTitle = (TextView)view.findViewById(R.id.activity_title);
            activityImage = (ImageView) view.findViewById(R.id.activity_image);
            activityDetail = (TextView)view.findViewById(R.id.activity_detail);
            btnLike = (ImageView) view.findViewById(R.id.btnLike);
            btnShare = (ImageView) view.findViewById(R.id.btnShare);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        View v;
        this.aq = new AQuery(convertView);
        Integer type = getItemViewType(position);
        final ViewHolder holder;

        if (convertView == null) {

            if (type == 0) {
                convertView = inflater.inflate(R.layout.home_row_no_image, parent, false);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.home_row_image_only, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.home_row, parent, false);
            }

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        animation = AnimationUtils.loadAnimation(context, R.anim.like_button_animate);
        text_animation = AnimationUtils.loadAnimation(context, R.anim.text_animation);

        FontUtils.loadFont(context, "Montserrat-Light.otf");
        FontUtils.setFont(holder.rootLayout);

        if (type == 2) {
            String imgaq = Settings.base_url + "/assets/images/activities/" + image[position];
            aq.id(holder.activityImage).image(imgaq, false, true);
        } else {

            if (type == 1) {
                String imgaq = Settings.base_url + "/assets/images/activities/" + image[position];
                aq.id(holder.activityImage).image(imgaq, false, true);
            }

            if (isLiked[position].equals("1")) {

                holder.btnLike.setImageResource(R.mipmap.icn512_like_yellow);
                if (likes[position] < 2) {
                    holder.activityLikes.startAnimation(text_animation);
                    holder.activityLikes.setText(likes[position] + " Like");
                } else {
    //                    Integer like = likes[position];
                    holder.activityLikes.startAnimation(text_animation);
                    holder.activityLikes.setText(likes[position] + " Likes");
                }

            } else {

                if (liked_positions.contains(position)) {
                    holder.btnLike.setImageResource(R.mipmap.icn512_like_yellow);
                    if (likes[position] < 1) {
                        holder.activityLikes.startAnimation(text_animation);
                        holder.activityLikes.setText("1 Like");
                    } else {
                        Integer like = likes[position] + 1;
                        holder.activityLikes.startAnimation(text_animation);
                        holder.activityLikes.setText(like + " Likes");
                    }
                } else {
                    holder.btnLike.setImageResource(R.mipmap.icn512_like_white);
                    if (likes[position] <= 2) {
                        holder.activityLikes.setText(likes[position].toString() + " Like");
                    } else {
                        holder.activityLikes.setText(likes[position].toString() + " Likes");
                    }
                }

            }

            holder.activityDetail.setText(detail[position]);
            holder.activityTitle.setText(title[position]);

            holder.btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
                    holder.activityLikes.startAnimation(text_animation);
                    Integer like = likes[position] + 1;
                    if (!isLiked[position].equals("1")) {
                        if (!liked_positions.contains(position)) {
                            new APIactivityLikeBtn(context, "/api/activities/" + id[position] + "/like/" + UserInfo.getUserID()).execute();
                            holder.activityLikes.setText(like + " Likes");
                            holder.btnLike.setImageResource(R.mipmap.icn512_like_yellow);
                            liked_positions.add(position);
                        }
                    }
                }
            });

            holder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(animation);
                    String share = title[position] + "\n" + detail[position] + "\n\nhttps://play.google.com/store/apps/details?id=com.tidalsolutions.magic89_9";

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share"));
                }
            });

        }

        return convertView;
    }

    class APIactivityUnlikeBtn extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIactivityUnlikeBtn(Context act, String api_url) {
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

            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");

                if (status) {
                    Toast.makeText(act, "unliked", Toast.LENGTH_LONG).show();
                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();


                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    class APIactivityLikeBtn extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIactivityLikeBtn(Context act, String api_url) {
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

            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String service = jObj.getString("service");

                if (status) {

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