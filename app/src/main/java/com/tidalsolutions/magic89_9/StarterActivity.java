package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Jeffrey on 12/7/2015.
 */
public class StarterActivity extends Activity {
//    boolean isChecked;
//    String userEmail;
//    String userPassword;
//    String firstName;

    private final int SPLASH_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(StarterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);

//        SharedPreferences settings1 = getSharedPreferences("PREFS_NAME", 0);
//        isChecked = settings1.getBoolean("isChecked", false);
//        userEmail = settings1.getString("email","");
//        userPassword = settings1.getString("password","");
//        firstName = settings1.getString("firstname","");
//
//        Log.d("hey", "is checked = " + isChecked);
//        Log.d("hey", "userEmail = " + userEmail);
//        Log.d("hey", "userPassword = " + userPassword);

//        if (isChecked) {
//            new ReCheckUserDetails(StarterActivity.this, "api/login/" + userEmail + "/" + userPassword).execute();
//        } else {
//            Intent intent = new Intent(StarterActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

//    class ReCheckUserDetails extends AsyncTask<String, Void, String> {
//        Activity act;
//        ProgressDialog pd;
//        String api_url;
//
//        public ReCheckUserDetails (Activity act, String api_url) {
//            this.act = act;
//            this.api_url = api_url;
//        }
//        @Override
//        protected String doInBackground(String... params) {
//            HttpClient httpclient;
//            HttpPost httppost;
//            HttpResponse response;
//            HttpEntity entity;
//            InputStream isr = null;
//            String result = "";
//
//            String url = Settings.base_url + api_url;
//
//            try {
//                httpclient = new DefaultHttpClient();
//                httppost = new HttpPost(url);
//                response = httpclient.execute(httppost);
//                entity = response.getEntity();
//                isr = entity.getContent();
//            } catch (Exception e) {
//                Log.e("log_tag", "Error in http connection " + e.toString());
//            }
//
//            //convert response to string
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                isr.close();
//
//                result = sb.toString();
//            } catch (Exception e) {
//                Log.e("log_tag", "Error converting result" + e.toString());
//            }
//
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if (pd != null) {
//                pd.dismiss();
//            }
//            try {
//                JSONObject jObj = new JSONObject(result);
//                boolean status = jObj.getBoolean("success");
//                String  service = jObj.getString("service");
//
//                if (status) {
//
//                    JSONObject userData = jObj.getJSONObject("user_data");
//                    String firstname = userData.getString("first_name");
//                    String email = userData.getString("email");
//                    String gender = userData.getString("gender");
//                    String birthday = userData.getString("birthday");
//                    String mobile = userData.getString("mobile");
//                    String image = userData.getString("image");
//                    String id = userData.getString("id");
//
//                    UserInfo.SetUserID(id);
//                    UserInfo.SetFirstname(firstname);
//                    UserInfo.SetGender(gender);
//                    UserInfo.SetBirtdate(birthday);
//                    UserInfo.SetMobile(mobile);
//                    UserInfo.SetImage(image);
//
//                    Intent in = new Intent(StarterActivity.this, MainActivity.class);
//                    startActivity(in);
//                }
//                else {
//                    Intent intent = new Intent(StarterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
//            } catch (JSONException e) {
//                Log.e("error", "Error parsing data" + e.toString());
//            }
//        }
//    }
}