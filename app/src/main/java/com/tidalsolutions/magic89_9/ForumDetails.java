package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.tidalsolutions.magic89_9.adapter.ForumDetailsAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey on 12/15/2015.
 */
public class ForumDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private AQuery aq;
    TextView thread_id, detail_title, date_created, contents, username, likes;
    EditText commentText;
    Button commentBtn;
    ImageView thread_user_image, btnLike, btnShare;
    private ListView listview;
    String threadUserImage, threadTitle, threadDetail;
    Integer threadID, total_likes;
    Animation animation, text_animation;
    RelativeLayout rootLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    Boolean reload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_details);
        getSupportActionBar().hide();

        listview = (ListView) findViewById(R.id.forum_detail_list);
        commentText = (EditText) findViewById(R.id.comment_et);
        commentBtn = (Button) findViewById(R.id.comment_btn);

        animation = AnimationUtils.loadAnimation(this, R.anim.like_button_animate);
        text_animation = AnimationUtils.loadAnimation(this, R.anim.text_animation);

        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.forum_details_header, listview,false);
        rootLayout = (RelativeLayout) headerView.findViewById(R.id.root_layout);
        FontUtils.loadFont(ForumDetails.this, "Montserrat-Light.otf");
        FontUtils.setFont(rootLayout);
        detail_title = (TextView) headerView.findViewById(R.id.detail_title);
        date_created = (TextView) headerView.findViewById(R.id.date_created);
        contents = (TextView) headerView.findViewById(R.id.contents);
        username = (TextView) headerView.findViewById(R.id.username);
        likes = (TextView) headerView.findViewById(R.id.likes);
        btnLike = (ImageView) headerView.findViewById(R.id.btnLike);
        btnShare = (ImageView) headerView.findViewById(R.id.btnShare);
        thread_user_image = (ImageView) headerView.findViewById(R.id.thread_user_image);

//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
//        swipeRefreshLayout.setOnRefreshListener(this);

        listview.addHeaderView(headerView);

        setupActionBar();
        threadID = getIntent().getExtras().getInt("threadID");
        total_likes = getIntent().getExtras().getInt("likes");
        threadUserImage = getIntent().getExtras().getString("threadUserImage");
        threadTitle = getIntent().getExtras().getString("threadTitle");
        threadDetail = getIntent().getExtras().getString("threadDetail");

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload = false;
                String comment = commentText.getText().toString().trim();
                String stringComment;
                try {
                    stringComment = URLEncoder.encode(comment, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new AssertionError("UTF-8 is unknown");
                }

                new MakeComment(ForumDetails.this, "/api/threads/"+threadID+"/reply/"+UserInfo.getUserID()+"/"+stringComment).execute();
                commentText.setText("");
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLike.startAnimation(animation);
                btnLike.setImageResource(R.mipmap.icn512_like_yellow);
                new APIForumCommentLikeBtn(ForumDetails.this, "/api/threads/" + threadID + "/like/" + UserInfo.getUserID()).execute();

                if (total_likes < 1) {
                    likes.startAnimation(text_animation);
                    likes.setText("1 Like");
                } else {
                    Integer like = total_likes + 1;
                    likes.startAnimation(text_animation);
                    likes.setText(like + " Likes");
                }

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);

                String share = threadTitle + "\n" + threadDetail + "\n\nhttps://play.google.com/store/apps/details?id=com.tidalsolutions.magic89_9";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "\n\n");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share );
                startActivity(Intent.createChooser(sharingIntent, "Share"));
            }
        });

        this.aq = new AQuery(this);
        new APIForumDetails(ForumDetails.this,  "/api/threads/" + threadID + "/user/" + UserInfo.getUserID()).execute();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so longf
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        reload = false;
        new APIForumDetails(ForumDetails.this,  "/api/threads/" + threadID + "/user/" + UserInfo.getUserID()).execute();
    }

    class APIForumDetails extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url;

        public APIForumDetails (Activity act, String api_url) {
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
            String url = Settings.base_url + api_url;

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
        protected void onPreExecute() {
            if (reload) {
                super.onPreExecute();
                pd = new ProgressDialog(act);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
//            Toast.makeText(act.getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String  service = jObj.getString("service");


                if (status) {

                    JSONObject threadData = jObj.getJSONObject("thread_data");
                    Integer id = threadData.getInt("id");
                    String title = threadData.getString("title");
                    String detail_contents = threadData.getString("contents");
                    String detail_username = threadData.getString("user_first_name");
                    String dateCreated = threadData.getString("date_created");
                    Integer detail_likes = threadData.getInt("likes");
                    String is_liked = threadData.getString("is_liked");

                    List<String> comment_usernames_list = new ArrayList<String>();
                    List<String> comment_contents_list = new ArrayList<String>();
                    List<String> comment_dates_created_list = new ArrayList<String>();
                    List<String> user_image_list = new ArrayList<String>();

                    JSONArray replies_data = threadData.getJSONArray("replies");

                    for (int i = 0; i < replies_data.length(); i++) {
                        JSONObject reply_data = replies_data.getJSONObject(i);
                        String comment_date_created = reply_data.getString("date_created");
                        String comment_username = reply_data.getString("user_first_name");
                        String user_image = reply_data.getString("user_image");
                        String comment_content = reply_data.getString("contents");

                        comment_dates_created_list.add(comment_date_created);
                        comment_usernames_list.add(comment_username);
                        comment_contents_list.add(comment_content);
                        user_image_list.add(user_image);
                    }

                    String like_append;

                    if (detail_likes > 1) {
                        like_append = "Likes";
                    } else {
                        like_append = "Like";
                    }

                    if (!threadUserImage.equals("")) {
                        String imgaq = Settings.base_url + "/assets/images/users/" + threadUserImage;
                        aq.id(R.id.thread_user_image).image(imgaq, false, false);
                    } else {
                        thread_user_image.setImageResource(R.drawable.blank_person);
                    }
                    detail_title.setText(title);
                    date_created.setText(dateCreated);
                    contents.setText(detail_contents);
                    username.setText(detail_username);
                    likes.setText(detail_likes.toString() + " " + like_append);
                    if (is_liked.equals("1")) {
                        btnLike.setImageResource(R.mipmap.icn512_like_yellow);
                    }

                    String comment_date_created_list[] = comment_dates_created_list.toArray(new String[comment_dates_created_list.size()]);
                    String comment_username_list[] = comment_usernames_list.toArray(new String[comment_usernames_list.size()]);
                    String comment_content_list[] = comment_contents_list.toArray(new String[comment_contents_list.size()]);
                    String user_image_arr[] = user_image_list.toArray(new String[user_image_list.size()]);

//                    if (swipeRefreshLayout.isRefreshing()) {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }

                    ForumDetailsAdapter forumDetailsAdapter = new ForumDetailsAdapter(ForumDetails.this, threadID, comment_username_list, comment_content_list, comment_date_created_list, user_image_arr);
                    listview.setAdapter(forumDetailsAdapter);
                }
                else {
                    String message = jObj.getString("error");
                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    class APIForumCommentLikeBtn extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public APIForumCommentLikeBtn(Context act, String api_url) {
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

                if (status) {
//                    Toast.makeText(act.getApplicationContext(), "liked", Toast.LENGTH_LONG).show();
                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();


                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    class MakeComment extends AsyncTask<String, Void, String> {
        Context act;
        ProgressDialog pd;
        String api_url;

        public MakeComment(Context act, String api_url) {
            this.act = act;
            this.api_url = api_url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(act);
            pd.setMessage("");
            pd.setCancelable(false);
            pd.show();
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

                if (status) {
                    new APIForumDetails(ForumDetails.this,  "/api/threads/" + threadID + "/user/" + UserInfo.getUserID()).execute();
                } else {
                    String message = jObj.getString("error");
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();


                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings1 = getSharedPreferences("PREFS_NAME", 0);
        String id = settings1.getString("user_id", "");
        String image = settings1.getString("image", "");
        String firstname = settings1.getString("firstname", "");
        String mobile = settings1.getString("mobile", "");
        String birthday = settings1.getString("birthday", "");
        UserInfo.SetUserID(id);
        UserInfo.SetFirstname(firstname);
        UserInfo.SetBirtdate(birthday);
        UserInfo.SetMobile(mobile);
        UserInfo.SetImage(image);
    }
}
