package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordChange extends AppCompatActivity {
    ScrollView mRootLayout;
    Button btnChangePass;
    Validations validate;
    EditText etPassword, etPasswordConfirm, etPasswordOld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change);

        setupActionBar();

        mRootLayout = (ScrollView) findViewById(R.id.root_layout);
        etPasswordOld = (EditText) findViewById(R.id.et_old_password);
        etPassword = (EditText) findViewById(R.id.et_reg_pass);
        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        etPasswordConfirm = (EditText) findViewById(R.id.et_reg_confirm_pass);
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Regular.otf");
        FontUtils.setFont(mRootLayout);

        validate = new Validations();

        etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
        etPasswordConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);

        etPasswordOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etPasswordOld)) {
                        etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_white, 0, 0, 0);
                    } else {
                        etPasswordOld.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
                    }

                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPassword.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etPassword)) {
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_white, 0, 0, 0);
                    } else {
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
                    }

                }
            }
        });

        etPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPasswordConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etPasswordConfirm)) {
                        etPasswordConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_white, 0, 0, 0);
                    } else {
                        etPasswordConfirm.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
                    }

                }
            }
        });

        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = etPasswordOld.getText().toString().trim();
                String newPass = etPassword.getText().toString().trim();
                String newPassConfirm = etPasswordConfirm.getText().toString().trim();

                if (newPass.equals(newPassConfirm)) {
                    new ChangePass(PasswordChange.this, "/api/change_password/"+UserInfo.getUserID()+"/"+md5(newPass)).execute();
                } else {
                    Toast.makeText(PasswordChange.this, "Passwords do no match", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    class ChangePass extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url;

        public ChangePass (Activity act, String api_url) {
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
                    Intent in = new Intent(PasswordChange.this, LoginActivity.class);
                    startActivity(in);
                    Toast.makeText(act, "Your Password is changed", Toast.LENGTH_LONG).show();
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

    private static final String md5(final String password) {
        try {

            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
