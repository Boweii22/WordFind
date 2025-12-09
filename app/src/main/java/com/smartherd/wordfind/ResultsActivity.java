package com.smartherd.wordfind;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smartherd.wordfind.adapters.ShimmerAdapter;
import com.smartherd.wordfind.adapters.WordResultAdapter;
import com.smartherd.wordfind.api.DictionaryService;
import com.smartherd.wordfind.model.DefinitionResponse;
import com.smartherd.wordfind.model.Meaning;
import com.smartherd.wordfind.model.WordResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {

    public static final String EXTRA_RESULTS_LIST = "extra_results_list";

    // UI elements
    private RecyclerView shimmerRecycler;
    private RecyclerView resultsRecycler;
    private View layoutNoResults;

    // Adapters
    private ShimmerAdapter shimmerAdapter;
    private WordResultAdapter wordAdapter;

    // Data
    private List<WordResult> resultsList;

    // Dictionary API
    private DictionaryService dictionaryService;

    private static final String TAG = "ResultsActivity";

    // Track how many definition calls are pending
    private final AtomicInteger definitionCallCounter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_results);

        // Back button
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

        // UI references
        shimmerRecycler = findViewById(R.id.recycler_shimmer);
        resultsRecycler = findViewById(R.id.recycler_results);
        layoutNoResults = findViewById(R.id.layout_no_results);

        // Load incoming results from SearchFragment
        resultsList = (List<WordResult>) getIntent().getSerializableExtra(EXTRA_RESULTS_LIST);

        shimmerAdapter = new ShimmerAdapter();
        shimmerRecycler.setAdapter(shimmerAdapter);

        wordAdapter = new WordResultAdapter(this, resultsList);
        resultsRecycler.setAdapter(wordAdapter);

        Log.d("DEBUG_RESULTS", "Received results count = " + resultsList.size());
        for (WordResult w : resultsList) {
            Log.d("DEBUG_RESULTS", "Word = " + w.getWord() + " | Score = " + w.getScore());
        }

        // Initial visibility
        shimmerRecycler.setVisibility(View.VISIBLE);
        resultsRecycler.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);

        // Retrofit for dictionary
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DictionaryService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dictionaryService = retrofit.create(DictionaryService.class);

        if (resultsList != null && !resultsList.isEmpty()) {
            definitionCallCounter.set(resultsList.size());
            fetchDefinitionsForAllWords();
        } else {
            // Datamuse returned ZERO items → show No Results immediately
            showNoResults();
        }
    }

    // ----------------------------------------------------------------
    // FETCH DEFINITIONS FOR EACH WORD (but DO NOT remove words)
    // ----------------------------------------------------------------
    private void fetchDefinitionsForAllWords() {
        for (int i = 0; i < resultsList.size(); i++) {
            fetchDefinitionForWord(resultsList.get(i), i);
        }
    }

    private void fetchDefinitionForWord(WordResult wordResult, int position) {

        dictionaryService.getDefinitions(wordResult.getWord())
                .enqueue(new Callback<List<DefinitionResponse>>() {

                    @Override
                    public void onResponse(Call<List<DefinitionResponse>> call,
                                           Response<List<DefinitionResponse>> response) {

                        boolean success = false;

                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                            Meaning meaning = response.body().get(0).getMeanings().isEmpty()
                                    ? null
                                    : response.body().get(0).getMeanings().get(0);

                            if (meaning != null && !meaning.getDefinitions().isEmpty()) {
                                wordResult.setDefinition(meaning.getDefinitions().get(0).getDefinition());
                                wordResult.setPartOfSpeech(meaning.getPartOfSpeech());
                                success = true;
                            }
                        }

                        if (!success) {
                            wordResult.setDefinition("Definition not found.");
                            wordResult.setPartOfSpeech("N/A");
                        }

                        wordAdapter.notifyItemChanged(position);
                        onDefinitionComplete();
                    }

                    @Override
                    public void onFailure(Call<List<DefinitionResponse>> call, Throwable t) {
                        Log.e(TAG, "Failed def for: " + wordResult.getWord(), t);
                        wordResult.setDefinition("Definition not found.");
                        wordResult.setPartOfSpeech("N/A");

                        wordAdapter.notifyItemChanged(position);
                        onDefinitionComplete();
                    }
                });
    }

    // ----------------------------------------------------------------
    // AFTER ALL DEFINITIONS ARE FINISHED LOADING
    // ----------------------------------------------------------------
    private void onDefinitionComplete() {

        if (definitionCallCounter.decrementAndGet() == 0) {

            // Datamuse gave results → ALWAYS show list
            // Even if dictionary failed for some words.
            showResultsList();
        }
    }

    // ----------------------------------------------------------------
    // UI STATE HANDLERS
    // ----------------------------------------------------------------
    private void showResultsList() {
        shimmerRecycler.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);

        resultsRecycler.setVisibility(View.VISIBLE);
        wordAdapter.notifyDataSetChanged();
    }

    private void showNoResults() {
        shimmerRecycler.setVisibility(View.GONE);
        resultsRecycler.setVisibility(View.GONE);

        layoutNoResults.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wordAdapter != null) wordAdapter.shutdownTts();
    }
}
