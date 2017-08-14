package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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

public class ForgotPassword extends AppCompatActivity {
    EditText etEmail;
    Validations validate;
    Button btnResetPass;
    ScrollView mRootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        validate = new Validations();
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Regular.otf");

        etEmail = (EditText) findViewById(R.id.et_email);
        btnResetPass = (Button) findViewById(R.id.btnForgotPassword);
        mRootLayout = (ScrollView) findViewById(R.id.root_layout);

        FontUtils.setFont(mRootLayout);
        etEmail.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_email_gray, 0, 0, 0);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etEmail.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_email_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etEmail)) {
                        etEmail.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_email_white, 0, 0, 0);
                    } else {
                        etEmail.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_email_gray, 0, 0, 0);
                    }

                }
            }
        });
    ///api/forgot_password/email
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                new ResetPasswordAPI(ForgotPassword.this, "api/forgot_password/"+email).execute();
            }
        });
    }

    class ResetPasswordAPI extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url;

        public ResetPasswordAPI (Activity act, String api_url) {
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
            super.onPreExecute();
            pd = new ProgressDialog(act);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
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
                String  service = jObj.getString("service");
                if (status) {
                    Intent in = new Intent(ForgotPassword.this, LoginActivity.class);
                    startActivity(in);
                    Toast.makeText(act.getApplicationContext(), "Your new password is sent to your Email", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    String message = jObj.getString("error");
                    Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }
}
