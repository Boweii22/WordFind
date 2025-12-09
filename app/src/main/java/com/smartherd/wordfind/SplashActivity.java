package com.smartherd.wordfind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Duration for the splash screen to be visible
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure you have defined a no-actionbar theme for this activity in your Manifest
        setContentView(R.layout.activity_splash);

        ImageView bulbIcon = findViewById(R.id.bulb_icon);
        TextView meaningText = findViewById(R.id.meaning_text);

        // --- 💡 Create Animations ---

        // 1. **Bulb Icon Animation (Scale Up and Fade In)**
        // Scale from 0.5 to 1.0 (small to normal size)
        ScaleAnimation scaleAnim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(1000);

        // Fade In from 0.0 to 1.0
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(1000);

        // Combine animations
        AnimationSet iconAnimSet = new AnimationSet(true);
        iconAnimSet.setInterpolator(new DecelerateInterpolator());
        iconAnimSet.addAnimation(scaleAnim);
        iconAnimSet.addAnimation(alphaAnim);

        // 2. **Text Animation (Fade In)**
        AlphaAnimation textFadeIn = new AlphaAnimation(0.0f, 1.0f);
        textFadeIn.setDuration(1000);
        textFadeIn.setStartOffset(500); // Start the text fade-in half a second after the icon starts

        // --- 🚀 Start Animations ---
        bulbIcon.startAnimation(iconAnimSet);
        meaningText.startAnimation(textFadeIn);

        // --- ⏳ Transition to Main Activity ---
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the main activity
                Intent mainIntent = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(mainIntent);
                finish(); // Close the splash activity so the user can't go back to it
            }
        }, SPLASH_DURATION);
    }
}