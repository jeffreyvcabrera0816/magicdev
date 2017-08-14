package com.tidalsolutions.magic89_9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
//    PullToRefreshWebView mPullRefreshWebView;
    WebView webview;
    MediaPlayer mediaPlayer;
    LinearLayout mRootLayout;
    Typeface Lightface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

//        Button btn_sign_out = (Button)findViewById(R.id.sign_out);
//        btn_sign_out.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });

//        Lightface = Typeface.createFromAsset(getAssets(), "Montserrat-Light.otf");
        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
//        FontUtils.loadFont(getApplicationContext(), "Montserrat-Regular.otf");
//        FontUtils.setFont(mRootLayout);

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

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().hide();

        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_settings) {

                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);

                    Integer login_type = settings.getInt("login_type", 0);
                    if (login_type == 2 || login_type == 1) {
                        Toast.makeText(MainActivity.this, "You cannot change your password.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, PasswordChange.class);
                        startActivity(intent);
                    }


//                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.containerView,new SentFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.edit_profile) {
                    Intent intent = new Intent(MainActivity.this, EditProfile.class);
                    startActivity(intent);
                }

                if (menuItem.getItemId() == R.id.nav_help) {
                    Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(intent);

//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView,new HelpFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_logout) {
                    SharedPreferences.Editor editor;
                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);

                    Integer login_type = settings.getInt("login_type", 0);

                    if (login_type == 1) {
                        if (mGoogleApiClient.isConnected()) {   //google sign out
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            mGoogleApiClient.disconnect();
                                        }
                                    });
                        }
                    } else if (login_type == 2) {
                        LoginManager.getInstance().logOut();    //facebook sign out
                    }

                    editor = settings.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);

                    webview = (WebView) findViewById(R.id.webview);
//                    webView.setVisibility(GONE);

                    if(webview != null) {
                        webview.removeAllViews();
                        webview.destroy();
//                        webview.setRefreshing();
                    }
//                    webView.destroy();
                    startActivity(intent);
                    finish();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        FontUtils.loadFont(MainActivity.this, "Montserrat-Light.otf");
        FontUtils.setFont(toolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    @Override
    public void onBackPressed() {

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

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d("error", "onConnectionFailed:" + connectionResult);
    }
}