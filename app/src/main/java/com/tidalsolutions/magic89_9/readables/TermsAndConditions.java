package com.tidalsolutions.magic89_9.readables;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.tidalsolutions.magic89_9.FontUtils;
import com.tidalsolutions.magic89_9.R;

public class TermsAndConditions extends Activity {
    ScrollView mRootLayout;
    Button terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        terms = (Button) findViewById(R.id.terms_btn);
        mRootLayout = (ScrollView) findViewById(R.id.root_layout);

        FontUtils.loadFont(getApplicationContext(), "Montserrat-Regular.otf");
        FontUtils.setFont(mRootLayout);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }
}
