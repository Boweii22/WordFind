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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {

    public static final String EXTRA_RESULTS_LIST = "extra_results_list";

    private RecyclerView shimmerRecycler;
    private RecyclerView resultsRecycler;
    private View layoutNoResults;

    private ShimmerAdapter shimmerAdapter;
    private WordResultAdapter wordAdapter;

    private List<WordResult> resultsList;

    private DictionaryService dictionaryService;

    private static final String TAG = "ResultsActivity";

    private final AtomicInteger definitionCallCounter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_results);

        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

        shimmerRecycler = findViewById(R.id.recycler_shimmer);
        resultsRecycler = findViewById(R.id.recycler_results);
        layoutNoResults = findViewById(R.id.layout_no_results);

        resultsList = (List<WordResult>) getIntent().getSerializableExtra(EXTRA_RESULTS_LIST);

        shimmerAdapter = new ShimmerAdapter();
        shimmerRecycler.setAdapter(shimmerAdapter);

        wordAdapter = new WordResultAdapter(this, resultsList);
        resultsRecycler.setAdapter(wordAdapter);

        shimmerRecycler.setVisibility(View.VISIBLE);
        resultsRecycler.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DictionaryService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dictionaryService = retrofit.create(DictionaryService.class);

        if (resultsList != null && !resultsList.isEmpty()) {
            definitionCallCounter.set(resultsList.size());
            fetchDefinitionsForAllWords();
        } else {
            showNoResults();
        }
    }

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

                        if (response.isSuccessful() &&
                                response.body() != null &&
                                !response.body().isEmpty()) {

                            List<Meaning> meanings = response.body().get(0).getMeanings();

                            if (meanings != null && !meanings.isEmpty()) {
                                Meaning m = meanings.get(0);

                                if (m.getDefinitions() != null && !m.getDefinitions().isEmpty()) {
                                    wordResult.setDefinition(m.getDefinitions().get(0).getDefinition());
                                    wordResult.setPartOfSpeech(m.getPartOfSpeech());
                                    success = true;
                                }
                            }
                        }

                        if (!success) {
                            wordResult.setDefinition("Definition not available.");
                            wordResult.setPartOfSpeech("N/A");
                        }

                        wordAdapter.notifyItemChanged(position);
                        onDefinitionComplete();
                    }

                    @Override
                    public void onFailure(Call<List<DefinitionResponse>> call, Throwable t) {
                        Log.e(TAG, "Failed def for: " + wordResult.getWord(), t);

                        wordResult.setDefinition("Definition not available.");
                        wordResult.setPartOfSpeech("N/A");

                        wordAdapter.notifyItemChanged(position);
                        onDefinitionComplete();
                    }
                });
    }

    private void onDefinitionComplete() {

        if (definitionCallCounter.decrementAndGet() == 0) {

            // REMOVE ALL WORDS WITHOUT VALID DEFINITIONS
            Iterator<WordResult> iterator = resultsList.iterator();

            while (iterator.hasNext()) {
                WordResult w = iterator.next();

                if (w.getDefinition() == null ||
                        w.getDefinition().trim().isEmpty() ||
                        w.getDefinition().equals("Definition not available.") ||
                        w.getDefinition().equals("Definition not found.")) {

                    iterator.remove();
                }
            }

            if (resultsList.isEmpty()) {
                showNoResults();
            } else {
                showResultsList();
            }
        }
    }

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
