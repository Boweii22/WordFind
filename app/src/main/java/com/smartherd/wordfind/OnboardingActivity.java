package com.smartherd.wordfind;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.smartherd.wordfind.adapters.OnboardingAdapter;
import com.smartherd.wordfind.model.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutDots;
    private Button buttonAction;
    private TextView textSkip;

    // --- Auto-Scroll Variables ---
    private final Handler autoScrollHandler = new Handler();
    // Time in milliseconds before changing to the next slide
    private static final long SCROLL_DELAY_MS = 3500; // 3.5 seconds
    private final int LAST_SCREEN_INDEX = 2;

    // Runnable to handle the page change logic
    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = onboardingViewPager.getCurrentItem();
            int nextItem = currentItem + 1;

            if (nextItem < onboardingAdapter.getItemCount()) {
                // Scroll to the next page
                onboardingViewPager.setCurrentItem(nextItem, true);
                // Re-schedule the runnable for the next scroll
                autoScrollHandler.postDelayed(this, SCROLL_DELAY_MS);
            } else {
                // Stop scrolling on the last page, or optionally loop back to the first page (0)
                // To loop: onboardingViewPager.setCurrentItem(0, true);
                // autoScrollHandler.postDelayed(this, SCROLL_DELAY_MS);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        layoutDots = findViewById(R.id.layout_dots);
        buttonAction = findViewById(R.id.button_action);
        textSkip = findViewById(R.id.text_skip);

        setupOnboardingItems();
        onboardingViewPager = findViewById(R.id.onboarding_view_pager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        // This creates a smooth slide animation between pages
        onboardingViewPager.setPageTransformer(new DepthPageTransformer());

        setupIndicators();
        setCurrentIndicator(0); // Initialize first dot

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                updateButton(position); // Update button text and action

                // When the user swipes, restart the auto-scroll timer
                if (position < LAST_SCREEN_INDEX) {
                    stopAutoScrolling();
                    startAutoScrolling();
                } else {
                    stopAutoScrolling();
                }
            }
        });

        buttonAction.setOnClickListener(v -> {
            int nextItem = onboardingViewPager.getCurrentItem() + 1;
            if (nextItem < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(nextItem, true);
            } else {
                navigateToMainScreen(); // Last screen, go to main
            }
        });
    }

    // --- Lifecycle Methods to manage Auto-Scrolling ---

    @Override
    protected void onResume() {
        super.onResume();
        // Start scrolling when the activity is visible
        if (onboardingViewPager.getCurrentItem() < LAST_SCREEN_INDEX) {
            startAutoScrolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop scrolling when the activity is not visible
        stopAutoScrolling();
    }

    // --- Auto-Scroll Control Methods ---

    private void startAutoScrolling() {
        // Post the runnable to the handler with the delay
        autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY_MS);
    }

    private void stopAutoScrolling() {
        // Remove any pending posts of the runnable
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    // --- Navigation Handlers (Includes the fix for the previous error) ---

    public void skipOnboarding(View view) {
        navigateToMainScreen();
    }

    private void navigateToMainScreen() {
        // Stop scrolling immediately before transition
        stopAutoScrolling();

        // Assume MainActivity is the next screen after onboarding
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    // --- Other methods (setupOnboardingItems, setupIndicators, etc.) remain unchanged ---

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();

        items.add(new OnboardingItem(
                R.drawable.image_onboarding_1,
                getString(R.string.onboarding_1_title),
                getString(R.string.onboarding_1_desc)
        ));

        items.add(new OnboardingItem(
                R.drawable.image_onboarding_2,
                getString(R.string.onboarding_2_title),
                getString(R.string.onboarding_2_desc)
        ));

        items.add(new OnboardingItem(
                R.drawable.image_onboarding_3,
                getString(R.string.onboarding_3_title),
                getString(R.string.onboarding_3_desc)
        ));

        onboardingAdapter = new OnboardingAdapter(getApplicationContext(), items);
    }

    private void setupIndicators() {
        // ... (existing code for setupIndicators) ...
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutDots.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        // ... (existing code for setCurrentIndicator) ...
        int childCount = layoutDots.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutDots.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
            }
        }
    }

    private void updateButton(int position) {
        // ... (existing code for updateButton) ...
        if (position == LAST_SCREEN_INDEX) {
            // Last screen
            buttonAction.setText(getString(R.string.button_get_started));
        } else {
            // Other screens
            buttonAction.setText(getString(R.string.button_continue));
        }
    }
}