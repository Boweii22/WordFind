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
import com.smartherd.wordfind.model.Definition;
import com.smartherd.wordfind.model.DefinitionResponse;
import com.smartherd.wordfind.model.Meaning;
import com.smartherd.wordfind.model.WordResult;

import java.util.ArrayList;
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

    private ShimmerAdapter shimmerAdapter;
    private WordResultAdapter wordAdapter;

    private List<WordResult> resultsList = new ArrayList<>();

    private DictionaryService dictionaryService;

    private static final String TAG = "ResultsActivity";

    private final AtomicInteger definitionCallsRemaining = new AtomicInteger(0);
    private final List<Call<?>> runningCalls = new ArrayList<>();

    // Stop updates if Activity finished
    private boolean isActivityAlive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_results);

        // Back button
        findViewById(R.id.icon_back).setOnClickListener(v -> finish());

        // Views
        shimmerRecycler = findViewById(R.id.recycler_shimmer);
        resultsRecycler = findViewById(R.id.recycler_results);

        // Adapters
        shimmerAdapter = new ShimmerAdapter();
        shimmerRecycler.setAdapter(shimmerAdapter);

        // Retrieve results
        List<WordResult> incomingList =
                (List<WordResult>) getIntent().getSerializableExtra(EXTRA_RESULTS_LIST);

        if (incomingList != null) {
            resultsList.addAll(incomingList);
        }

        wordAdapter = new WordResultAdapter(this, resultsList);
        resultsRecycler.setAdapter(wordAdapter);

        shimmerRecycler.setVisibility(View.VISIBLE);
        resultsRecycler.setVisibility(View.GONE);

        // API setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DictionaryService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dictionaryService = retrofit.create(DictionaryService.class);

        if (!resultsList.isEmpty()) {
            fetchDefinitionsForAllWords();
        } else {
            showResults();
        }
    }

    // ----------------------------------------------------------
    // FETCH DEFINITIONS FOR EACH WORD SAFELY
    // ----------------------------------------------------------
    private void fetchDefinitionsForAllWords() {

        definitionCallsRemaining.set(resultsList.size());

        for (int i = 0; i < resultsList.size(); i++) {
            WordResult wr = resultsList.get(i);
            fetchDefinition(wr, i);
        }
    }

    private void fetchDefinition(WordResult word, int pos) {

        Call<List<DefinitionResponse>> call =
                dictionaryService.getDefinitions(word.getWord());

        runningCalls.add(call);

        call.enqueue(new Callback<List<DefinitionResponse>>() {

            @Override
            public void onResponse(Call<List<DefinitionResponse>> call,
                                   Response<List<DefinitionResponse>> response) {

                if (!isActivityAlive) return;

                boolean found = false;

                List<DefinitionResponse> body = response.body();

                if (response.isSuccessful() && body != null && !body.isEmpty()) {

                    Meaning meaning = safeFirstMeaning(body);
                    Definition definition = safeFirstDefinition(meaning);

                    if (definition != null) {
                        word.setDefinition(definition.getDefinition());
                        word.setPartOfSpeech(meaning.getPartOfSpeech());
                        found = true;
                    }
                }

                if (!found) {
                    word.setDefinition("Definition not available.");
                    word.setPartOfSpeech("N/A");
                }

                wordAdapter.notifyItemChanged(pos);

                onDefinitionCallFinished();
            }

            @Override
            public void onFailure(Call<List<DefinitionResponse>> call, Throwable t) {
                if (!isActivityAlive) return;

                Log.e(TAG, "Definition load failed for: " + word.getWord(), t);

                word.setDefinition("Definition not available.");
                word.setPartOfSpeech("N/A");

                wordAdapter.notifyItemChanged(pos);

                onDefinitionCallFinished();
            }
        });
    }

    // ----------------------------------------------------------
    // SAFETY HELPERS FOR NULL / EMPTY API FIELDS
    // ----------------------------------------------------------
    private Meaning safeFirstMeaning(List<DefinitionResponse> list) {
        if (list == null || list.isEmpty()) return null;

        List<Meaning> m = list.get(0).getMeanings();
        if (m == null || m.isEmpty()) return null;

        return m.get(0);
    }

    private Definition safeFirstDefinition(Meaning meaning) {
        if (meaning == null) return null;

        List<Definition> defs = meaning.getDefinitions();
        if (defs == null || defs.isEmpty()) return null;

        return defs.get(0);
    }

    // ----------------------------------------------------------
    // HANDLE COMPLETION OF ALL DEFINITION FETCHES
    // ----------------------------------------------------------
    private void onDefinitionCallFinished() {

        if (definitionCallsRemaining.decrementAndGet() == 0) {

            // Remove invalid words
            Iterator<WordResult> it = resultsList.iterator();
            while (it.hasNext()) {
                WordResult w = it.next();
                if (w.getDefinition().equals("Definition not available.")) {
                    it.remove();
                }
            }

            wordAdapter.notifyDataSetChanged();
            showResults();
        }
    }

    // ----------------------------------------------------------
    // SHOW RESULTS & HIDE SHIMMER
    // ----------------------------------------------------------
    private void showResults() {
        shimmerRecycler.setVisibility(View.GONE);
        resultsRecycler.setVisibility(View.VISIBLE);
    }

    // ----------------------------------------------------------
    // LIFECYCLE: PREVENT CRASHES AFTER ACTIVITY CLOSES
    // ----------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();

        isActivityAlive = false;

        // Cancel all pending API calls to avoid crashes
        for (Call<?> call : runningCalls) {
            if (!call.isCanceled()) call.cancel();
        }

        if (wordAdapter != null) {
            wordAdapter.shutdownTts();
        }
    }
}
