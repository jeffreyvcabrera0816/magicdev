package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.Header;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{
    private AQuery aq;
    private static final int RESULT_LOAD_IMAGE = 1;
    EditText etEmail, etPassword,etPasswordConfirm, etFirstname, etMobile, etBday;
    Button btnRegister, btnRegImage;
    ImageView ivImage;
    TextView terms, titleHeader;
    String etGender,etRegion,etCity, etLastname;
    ScrollView mRootLayout;
    Typeface Boldface, Lightface, Regularface;
    Validations validate;
    CheckBox chkBox;
    String filename;
    String filePath = "";
    String encodedString;
    String firstname, lastname, gender, bday, region, city;
    ProgressDialog prgDialog;
    String mobileNo;
    String mobile, set_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        setupActionBar();

        aq = new AQuery(this);

        Boldface = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Lightface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        Regularface = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.otf");
        FontUtils.loadFont(getApplicationContext(), "Montserrat-Regular.otf");

        chkBox = (CheckBox) findViewById(R.id.chkBox);
        titleHeader = (TextView) findViewById(R.id.title_header);
        mRootLayout = (ScrollView) findViewById(R.id.root_layout);
        etFirstname = (EditText) findViewById(R.id.et_reg_firstname);
        etLastname = "Lastname";
        etGender = "M";
        etBday = (EditText) findViewById(R.id.et_reg_bday);
        etMobile = (EditText) findViewById(R.id.et_reg_mobile);
        etRegion = "region";
        etCity = "city";
        ivImage = (ImageView) findViewById(R.id.img_view_reg);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        FontUtils.setFont(mRootLayout);
        titleHeader.setTypeface(Boldface);
        btnRegister.setTypeface(Lightface);
        validate = new Validations();

        set_mobile = "0" + UserInfo.GetMobile();
        etFirstname.setText(UserInfo.GetFirstname());
        etBday.setText(UserInfo.GetBirthday());
        etMobile.setText(set_mobile);
        filename = UserInfo.GetImage();

        if (!(UserInfo.GetImage()).equals("")) {
            String imgaq = Settings.base_url + "/assets/images/users/" + UserInfo.GetImage();
            aq.id(R.id.img_view_reg).image(imgaq, false, false);
        } else {

        }



        etFirstname.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_name_gray, 0, 0, 0);
        etMobile.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_mobile_gray, 0, 0, 0);
        etBday.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_year_gray, 0, 0, 0);

        etFirstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etFirstname.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_name_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etFirstname)) {
                        etFirstname.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_name_white, 0, 0, 0);
                    } else {
                        etFirstname.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_name_gray, 0, 0, 0);
                    }

                }
            }
        });

        etMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etMobile.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_mobile_yellow, 0, 0, 0);
                } else {
                    if (validate.isEmpty(etMobile)) {
                        etMobile.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_mobile_white, 0, 0, 0);
                    } else {
                        etMobile.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_mobile_gray, 0, 0, 0);
                    }

                }
            }
        });

        etBday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    etBday.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_year_yellow, 0, 0, 0);
                    DateDialog dialog = new DateDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                } else {
                    if (validate.isEmpty(etBday)) {
                        etBday.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_year_white, 0, 0, 0);
                    } else {
                        etBday.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_year_gray, 0, 0, 0);
                    }
                }
            }

        });

        btnRegister.setOnClickListener(this);
        ivImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View sender) {
        if(sender.getId() == R.id.btnRegister){

            firstname = etFirstname.getText().toString().trim();
            lastname = etLastname.trim();
            gender = etGender.trim();
            bday = etBday.getText().toString().trim();
            region = etRegion.trim();
            city = etCity.trim();

            String fname;
            try {
                fname = URLEncoder.encode(firstname, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError("UTF-8 is unknown");
            }

            mobileNo = etMobile.getText().toString().trim();

            if (mobileNo.equals("")) {
                Toast.makeText(EditProfile.this, "Mobile Number is required", Toast.LENGTH_LONG).show();
                return;
            } else {
                mobile = mobileNo.substring(1);
            }

            new API(EditProfile.this, "api/edit_profile/" + UserInfo.getUserID() +  "/" + fname + "/" + lastname + "/"+ gender +"/" + bday + "/" + mobile + "/" + region + "/" + city + "/", filename).execute();
                                                        ///api/edit_profile/17/ firstname/ lastname/M/ 2002-01-01/9428200298/region /city/
        } else if (sender.getId() == R.id.img_view_reg) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            ivImage.setImageURI(selectedImage);
//            filename = getFileName(selectedImage);
//            filePath = ImageFilePath.getPath(getApplicationContext(), selectedImage);

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            // Get the Image's file name
            String fileNameSegments[] = filePath.split("/");
            filename = fileNameSegments[fileNameSegments.length - 1];
            encodeImagetoString();

//            Toast.makeText(RegisterActivity.this, filePath, Toast.LENGTH_SHORT).show();
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                Bitmap bitmap;
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(filePath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    class API extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url, filename, email;

        public API (Activity act, String api_url, String filename) {
            this.act = act;
            this.api_url = api_url;
            this.filename =filename;
        }
        @Override
        protected String doInBackground(String... params) {

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
//            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            HttpClient httpclient;
            HttpPost httppost;
            HttpResponse response;
            HttpEntity entity;
            InputStream isr = null;
            String result = "";
            String final_url = api_url + "/" + filename;
            String test_url = api_url + "/";
            String url = Settings.base_url + final_url;

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
//            Toast.makeText(act.getApplicationContext(), result, Toast.LENGTH_LONG).show();
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String  service = jObj.getString("service");

                if (status) {
                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user_id", UserInfo.getUserID());
                    editor.putString("email", email);
                    editor.putBoolean("isChecked", true);
                    editor.putString("firstname", firstname);
                    editor.putString("gender", gender);
                    editor.putString("birthday", bday);
                    editor.putString("mobile", mobile);
                    editor.putString("image", filename);

                    editor.commit();
                    UserInfo.SetFirstname(firstname);
                    UserInfo.SetGender(gender);
                    UserInfo.SetBirtdate(bday);
                    UserInfo.SetMobile(mobile);
                    UserInfo.SetImage(filename);

                    uploadImage();


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

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void uploadImage() {

        RequestParams params = new RequestParams();
        params.put("filename", filename);
        params.put("image", encodedString);

        String url = Settings.base_url + "/api/upload_image/users/";
        Log.v("url", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(EditProfile.this, MainActivity.class);
                startActivity(in);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {

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
