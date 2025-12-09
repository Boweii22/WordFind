package com.smartherd.wordfind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.smartherd.wordfind.MainActivity;
import com.smartherd.wordfind.R;
import com.smartherd.wordfind.ResultsActivity;
import com.smartherd.wordfind.api.DatamuseService;
import com.smartherd.wordfind.model.DatamuseWord;
import com.smartherd.wordfind.model.WordResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText editSearch;
    private ChipGroup chipGroup;
    private AppCompatButton btnSearch;

    private DatamuseService datamuseService;
    private Call<List<DatamuseWord>> currentCall;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editSearch = view.findViewById(R.id.edit_search);
        chipGroup = view.findViewById(R.id.chip_group);
        btnSearch = view.findViewById(R.id.btn_start_search);

        ImageView menuIcon = view.findViewById(R.id.icon_menu);
        ImageView searchIcon = view.findViewById(R.id.icon_search_magnify);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DatamuseService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        datamuseService = retrofit.create(DatamuseService.class);

        // Drawer
        menuIcon.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        // Button click search
        btnSearch.setOnClickListener(v ->
                startSearch(editSearch.getText().toString().trim())
        );

        // Magnifying glass search
        searchIcon.setOnClickListener(v ->
                startSearch(editSearch.getText().toString().trim())
        );

        setupChipListeners();
        setupSearchListener();

        return view;
    }

    // --------------------------------------------------------
    // CHIP CLICK → ONLY SET TEXT (no search!)
    // --------------------------------------------------------
    private void setupChipListeners() {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);

            chip.setOnClickListener(v -> {
                String phrase = chip.getText().toString();
                editSearch.setText(phrase);

                // Move cursor to end
                editSearch.setSelection(phrase.length());
            });
        }
    }

    // --------------------------------------------------------
    // SEARCH BAR TEXT CHANGE (debounced typing)
    // --------------------------------------------------------
    private void setupSearchListener() {
        editSearch.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (currentCall != null && !currentCall.isCanceled()) {
                    currentCall.cancel();
                }
                handler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String txt = s.toString().trim();
                if (txt.length() < 3) return;

                searchRunnable = () -> startSearch(txt);
                handler.postDelayed(searchRunnable, 500);
            }
        });
    }

    // --------------------------------------------------------
    private void startSearch(String phrase) {
        if (phrase.isEmpty()) {
            Toast.makeText(getContext(), "Enter a meaning or phrase.", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchReverseResults(phrase);
    }

    private void fetchReverseResults(String phrase) {

        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }

        currentCall = datamuseService.getMeaningLikeWords(phrase.replace(" ", "+"));

        currentCall.enqueue(new Callback<List<DatamuseWord>>() {
            @Override
            public void onResponse(Call<List<DatamuseWord>> call,
                                   Response<List<DatamuseWord>> response) {

                if (!isFragmentAlive()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Error fetching words.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<DatamuseWord> list = response.body();

                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "No words found.", Toast.LENGTH_LONG).show();
                    return;
                }

                List<WordResult> results = categorize(list);

                Intent intent = new Intent(getContext(), ResultsActivity.class);
                intent.putExtra(ResultsActivity.EXTRA_RESULTS_LIST, (Serializable) results);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<DatamuseWord>> call, Throwable t) {
                if (call.isCanceled()) return;
                if (!isFragmentAlive()) return;

                Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------------------------------------
    private List<WordResult> categorize(List<DatamuseWord> list) {
        List<WordResult> results = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            WordResult.MatchCategory category =
                    (i == 0) ? WordResult.MatchCategory.BEST_MATCH :
                            (i == 1) ? WordResult.MatchCategory.GOOD_MATCH :
                                    WordResult.MatchCategory.SUGGESTION;

            results.add(new WordResult(list.get(i), category));
        }

        return results;
    }

    private boolean isFragmentAlive() {
        return getContext() != null && isAdded() && !isDetached();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}
