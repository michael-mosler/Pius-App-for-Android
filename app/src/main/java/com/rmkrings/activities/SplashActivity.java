package com.rmkrings.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash Screen activity shows logo and then starts main activity.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Temporarily delay for 1 second, just to make sure that we can see splash screem.
        // In simulator otherwise app start is too fast.
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Start home activity
        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        // close splash activity
        finish();
    }
}