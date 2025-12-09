package com.smartherd.wordfind;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_result);

        ImageView back = findViewById(R.id.icon_back);
        TextView retryBtn = findViewById(R.id.btn_retry);

        back.setOnClickListener(v -> finish());   // go back to search

        retryBtn.setOnClickListener(v -> finish()); // also go back
    }
}
