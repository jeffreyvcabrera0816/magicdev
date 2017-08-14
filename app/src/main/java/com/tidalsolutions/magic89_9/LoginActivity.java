package com.tidalsolutions.magic89_9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int GOOGLE_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    Integer login_type = 0;

    final Context context = this;
    private Button button;
    private EditText result;

    CallbackManager callbackManager;
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView btnRegister, titleHeader, forgotPassword;
    Validations validate;
    ScrollView mRootLayout;
    Typeface Boldface, Lightface, Regularface;

    String g_fb_email = "", g_fb_name = "";

    AlertDialog ca_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        initGoogleSignInButton();
        initFacebookSignInButton();

        checkKeepLoggedIn();

        Boldface = Typeface.createFromAsset(getAssets(), "Montserrat-Bold.otf");
        Lightface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        Regularface = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.otf");
        FontUtils.loadFont(LoginActivity.this, "Montserrat-Regular.otf");

        titleHeader = (TextView) findViewById(R.id.title_header);
        mRootLayout = (ScrollView) findViewById(R.id.root_layout);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        validate = new Validations();
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister = (TextView)findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        FontUtils.setFont(mRootLayout);
        titleHeader.setTypeface(Boldface);
        btnLogin.setTypeface(Lightface);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icn512_password_gray, 0, 0, 0);
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
    }

    void checkKeepLoggedIn() {
        SharedPreferences settings1 = getSharedPreferences("PREFS_NAME", 0);
        boolean isChecked = settings1.getBoolean("isChecked", false);
        String userEmail = settings1.getString("email","");
        String userPassword = settings1.getString("password","");
        String firstName = settings1.getString("firstname","");
        int login_type = settings1.getInt("login_type", 0);

        Log.d("hey", "isChecked = " + isChecked);
        Log.d("hey", "login_type = " + login_type);
        Log.d("hey", "userEmail = " + userEmail);
        Log.d("hey", "userPassword = " + userPassword);
        Log.d("hey", "firstName = " + firstName);

        if (isChecked) {
            if (login_type == 1) {
                OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                if (opr.isDone()) {
                    GoogleSignInResult result = opr.get();
                    handleGoogleSignInResult(result);
                } else {
                    showProgressDialog();
                    opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(GoogleSignInResult googleSignInResult) {
                            hideProgressDialog();
                            handleGoogleSignInResult(googleSignInResult);
                        }
                    });
                }
            } else if (login_type == 2) {
                handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
            } else if (login_type == 3) {
                gotoNextScreen(3, userEmail, userPassword, firstName);
            } else {
                Log.d(Settings.TAG, "keep logged in error");
            }
        }
    }


    @Override
    public void onClick(View sender) {
        if(sender.getId() == R.id.btnLogin) {
            String email = etEmail.getText().toString().trim(),
                    password = md5(etPassword.getText().toString().trim());

            if (email.toString().equals("") || password.toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter an email and a password", Toast.LENGTH_SHORT).show();
            } else {
                gotoNextScreen(3, email, password, "");
            }
        } else if (sender.getId() == R.id.g_sign_in_button) {
            login_type = 1;
            googleSignIn();
        } else {
            Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(in);
        }

    }

    class API extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url;
        Integer login_type;

        public API (Activity act, String api_url, Integer login_type) {
            this.act = act;
            this.api_url = api_url;
            this.login_type = login_type;
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

            if(login_type == 3 && !act.isFinishing()) {
                pd = new ProgressDialog(act);
                pd.setMessage("Loading...");
                pd.setCancelable(false);
                pd.show();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (login_type == 3 && pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String  service = jObj.getString("service");

                if (status) {
                    String password = md5(etPassword.getText().toString().trim());

                    JSONObject userData = jObj.getJSONObject("user_data");
                    String firstname = userData.getString("first_name");
                    String email = userData.getString("email");
                    String gender = userData.getString("gender");
                    String birthday = userData.getString("birthday");
                    String mobile = userData.getString("mobile");
                    String image = userData.getString("image");
                    String id = userData.getString("id");

                    UserInfo.SetUserID(id);
                    UserInfo.SetFirstname(firstname);
                    UserInfo.SetGender(gender);
                    UserInfo.SetBirtdate(birthday);
                    UserInfo.SetMobile(mobile);
                    UserInfo.SetImage(image);
                    UserInfo.SetLoginType(login_type);

                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("login_type", login_type);
                    editor.putString("user_id", id);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putBoolean("isChecked", true);
                    editor.putString("firstname", firstname);
                    editor.putString("gender", gender);
                    editor.putString("birthday", birthday);
                    editor.putString("mobile", mobile);
                    editor.putString("image", image);
                    editor.commit();

                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }
                else {
                    String message = jObj.getString("error");

                    String dialog_message = "";
                    if (login_type == 1) {
                        dialog_message = "Do you want to switch to Google Sign In?\nNOTE: This action cannot be undone";
                    } else if (login_type == 2){
                        dialog_message = "Do you want to switch to Facebook Login?\nNOTE: This action cannot be undone";
                    }

                    if (message.equals("Account has already been registered using Email")) {
                        new AlertDialog.Builder(context)
                                .setTitle("Account already registered using Email")
                                .setMessage(dialog_message)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        showConfirmChangeLoginTypeDialog(login_type);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (login_type == 1) {
                                            if (mGoogleApiClient.isConnected()) {   //google sign out
                                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                        new ResultCallback<com.google.android.gms.common.api.Status>() {
                                                            @Override
                                                            public void onResult(com.google.android.gms.common.api.Status status) {
                                                                mGoogleApiClient.disconnect();
                                                            }
                                                        });
                                            }
                                        } else if (login_type == 2) {
                                            LoginManager.getInstance().logOut();
                                        }
                                    }
                                })
                                .show();

//
                    }
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    void showConfirmChangeLoginTypeDialog(final int new_login_type) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final TextView dialogMessage = (TextView) promptsView.findViewById(R.id.dialogMessage);
        final EditText userInputEmail = (EditText) promptsView.findViewById(R.id.editTextDialogUserInputEmail);
        final EditText userInputPassword = (EditText) promptsView.findViewById(R.id.editTextDialogUserInputPassword);

        userInputEmail.setText(g_fb_email);
        alertDialogBuilder.setMessage("Please enter your email/password registered to Magic89.9");

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                            }
                        });

        ca_dialog = alertDialogBuilder.create();
        ca_dialog.show();

        ca_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new APIChangeAccount(LoginActivity.this, "api/change_login_type/"+userInputEmail.getText().toString().trim()+"/"+md5(userInputPassword.getText().toString().trim())+"/"+new_login_type, new_login_type, g_fb_email, g_fb_name).execute();
            }
        });
        ca_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (new_login_type == 1) {
                    if (mGoogleApiClient.isConnected()) {   //google sign out
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<com.google.android.gms.common.api.Status>() {
                                    @Override
                                    public void onResult(com.google.android.gms.common.api.Status status) {
                                        mGoogleApiClient.disconnect();
                                    }
                                });
                    }
                } else if (new_login_type == 2) {
                    LoginManager.getInstance().logOut();
                }
                ca_dialog.dismiss();
                hideKeyboard();
            }
        });
    }

    class APIChangeAccount extends AsyncTask<String, Void, String> {
        Activity act;
        ProgressDialog pd;
        String api_url, ca_email, ca_name;
        Integer ca_login_type;

        public APIChangeAccount (Activity act, String api_url, Integer login_type, String email, String name) {
            this.act = act;
            this.api_url = api_url;
            this.ca_login_type = login_type;
            this.ca_email = email;
            this.ca_name = name;
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
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                boolean status = jObj.getBoolean("success");
                String  service = jObj.getString("service");

                if (status) {
                    if (ca_login_type == 1) {
                        Toast.makeText(act, "Account type has been successfully changed to Google Sign In", Toast.LENGTH_LONG).show();
                        gotoNextScreen(1, ca_email, "-", ca_name);
                    } else if (ca_login_type == 2) {
                        Toast.makeText(act, "Account type has been successfully changed to Facebook Login", Toast.LENGTH_LONG).show();
                        gotoNextScreen(2, ca_email, "-", ca_name);
                    } else {
                        Toast.makeText(act, "error", Toast.LENGTH_SHORT).show();
                    }
                    ca_dialog.dismiss();
                } else {
                    String message = jObj.getString("error");

                    Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.e("error", "Error parsing data" + e.toString());
            }
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    void initFacebookSignInButton() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);
//        float fbIconScale = 1.45F;      //increase fb login button icon size
//        Drawable drawable = getResources().getDrawable(com.facebook.R.drawable.com_facebook_button_icon);
        loginButton.setBackgroundResource(R.mipmap.btn_login_with_facebook);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale), (int) (drawable.getIntrinsicHeight() * fbIconScale));
//        loginButton.setCompoundDrawables(drawable, null, null, null);
        loginButton.setText("");
        loginButton.setReadPermissions(Arrays.asList(new String[]{"email", "public_profile"}));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(Settings.TAG, "Facebook Login Canceled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(Settings.TAG, "Facebook Login Error " + e.toString());
            }
        });

//        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
//                handleFacebookAccessToken(newAccessToken);
//            }
//        };
    }

    private void handleFacebookAccessToken(AccessToken currentAccessToken) {
//        Log.d(Settings.TAG, "current access token" +currentAccessToken.toString());
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            GraphRequest request = GraphRequest.newMeRequest(
                    currentAccessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String fb_mail, fb_first_name;
                            try {

                                fb_mail = object.getString("email").trim();
                                fb_first_name = object.getString("first_name").trim();

                                g_fb_email = fb_mail;
                                g_fb_name = fb_first_name;

                                gotoNextScreen(2, fb_mail, "-", fb_first_name);

                            } catch (JSONException e) {
                                Log.d(Settings.TAG, e.toString());
                            }
                        }

                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,middle_name,last_name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            Log.d(Settings.TAG, "Facebook Login Failed / Facebook Signed Out / Null access token");
        }
    }

    void initGoogleSignInButton() {
        findViewById(R.id.g_sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        Button signInButton = (Button) findViewById(R.id.g_sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_WIDE);
//        signInButton.setScopes(gso.getScopeArray());
//
//        for (int i = 0; i < signInButton.getChildCount(); i++) {    //modify google sign in textview padding
//            View v = signInButton.getChildAt(i);//
//            if (v instanceof TextView) {
//                TextView tv = (TextView) v;
//                tv.setPadding(15, 0, 15, 0);
//                return;
//            }
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {    // Facebook Sign In
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            g_fb_email = acct.getEmail();
            g_fb_name =  acct.getDisplayName();
            gotoNextScreen(1, acct.getEmail(), "-", acct.getDisplayName());
        } else {
            Log.d(Settings.TAG, "Google Login Failed / Google Signed Out");
        }
    }

    void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(Settings.TAG, "onConnectionFailed:" + connectionResult);
    }

    void gotoNextScreen(Integer type, String email, String password, String name) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new API(LoginActivity.this, "api/login2/"+ type +"/"+ email + "/" + password + "/" + name, type).execute();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    public void keyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.tidalsolutions.magic89_9",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Toast.makeText(this, Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}