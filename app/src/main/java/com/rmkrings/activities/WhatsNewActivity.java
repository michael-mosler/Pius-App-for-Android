package com.rmkrings.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.rmkrings.helper.Config;

/**
 * Shows what's new in current version. This activity is started only once per
 * each version update.
 */
public class WhatsNewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Config.canUseDashboard()) {
            TextView tv = findViewById(R.id.welcome_intro);
            View v = findViewById(R.id.welcome_divider2);
            tv.setVisibility(View.GONE);
            v.setVisibility(View.GONE);
        }

        Button b = findViewById(R.id.welcome_lets_start);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
