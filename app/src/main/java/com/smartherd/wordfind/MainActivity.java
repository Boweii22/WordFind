package com.smartherd.wordfind;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
// 🌟 NEW IMPORTS FOR UNIQUE FRAGMENTS
import com.smartherd.fragments.FavouritesFragment;
import com.smartherd.fragments.HistoryFragment;
import com.smartherd.fragments.PlaceholderFragment;
import com.smartherd.fragments.SearchFragment;
import com.smartherd.fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Status Bar Setup (White background and dark icons)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        }
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Load the initial fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new SearchFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Back Press Handling (OnBackPressedDispatcher)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof SearchFragment) {
                        finish();
                    } else {
                        loadFragment(new SearchFragment());
                        NavigationView navView = findViewById(R.id.nav_view);
                        navView.setCheckedItem(R.id.nav_home);
                    }
                }
            }
        });
    }

    /**
     * Opens the navigation drawer. Called from the Fragment via the hamburger icon.
     */
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START, true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        // 🌟 CHECKING ITEM IDS AND LOADING UNIQUE FRAGMENTS 🌟
        if (itemId == R.id.nav_home) {
            selectedFragment = new SearchFragment();
        } else if (itemId == R.id.nav_favourites) {
            selectedFragment = new FavouritesFragment(); // 🌟 UNIQUE FRAGMENT
        } else if (itemId == R.id.nav_history) {
            selectedFragment = new HistoryFragment();     // 🌟 UNIQUE FRAGMENT
        } else if (itemId == R.id.nav_learning) {
            selectedFragment = PlaceholderFragment.newInstance("Learning");
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();  // 🌟 UNIQUE FRAGMENT
        } else if (itemId == R.id.nav_about) {
            selectedFragment = PlaceholderFragment.newInstance("About");
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        final Fragment finalSelectedFragment = selectedFragment;
        new Handler().postDelayed(() -> {
            if (finalSelectedFragment != null) {
                loadFragment(finalSelectedFragment);
            }
        }, 300);

        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}