package com.smartherd.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlaceholderFragment extends Fragment {
    private static final String ARG_TITLE = "title";

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(String title) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    // Constructor for quick use in MainActivity
    public PlaceholderFragment(String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Simple TextView to show the title
        TextView textView = new TextView(getContext());
        textView.setTextSize(24);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(50, 50, 50, 50);

        if (getArguments() != null) {
            textView.setText(getArguments().getString(ARG_TITLE));
        }

        return textView;
    }
}