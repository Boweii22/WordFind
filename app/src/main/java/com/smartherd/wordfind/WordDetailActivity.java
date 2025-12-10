package com.smartherd.wordfind;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.smartherd.wordfind.api.DictionaryService;
import com.smartherd.wordfind.model.Definition;
import com.smartherd.wordfind.model.DefinitionResponse;
import com.smartherd.wordfind.model.Meaning;
import com.smartherd.wordfind.model.Phonetic;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordDetailActivity extends AppCompatActivity {

    private TextView textWord, textPhonetic, textDefinition, textExample, textPartOfSpeech;
    private ImageView iconAudio, iconBack, iconFavorite;

    private FlexboxLayout textSynonymsRow1;
    private FlexboxLayout textSynonymsRow2;
    private FlexboxLayout textAntonymsRow1;

    private DictionaryService dictionaryService;
    private MediaPlayer mediaPlayer;

    private static final String TAG = "WORD_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        String word = getIntent().getStringExtra("word");

        initViews();
        textWord.setText(word);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DictionaryService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dictionaryService = retrofit.create(DictionaryService.class);

        fetchWordDetails(word);
    }

    private void initViews() {
        iconBack = findViewById(R.id.icon_back);
        iconBack.setOnClickListener(v -> finish());

        iconFavorite = findViewById(R.id.icon_fav);

        textWord = findViewById(R.id.text_word);
        textPhonetic = findViewById(R.id.text_phonetic);
        textDefinition = findViewById(R.id.text_definition);
        textExample = findViewById(R.id.text_example);
        textPartOfSpeech = findViewById(R.id.text_pos);

        iconAudio = findViewById(R.id.icon_audio);

        textSynonymsRow1 = findViewById(R.id.text_synonyms);
        textSynonymsRow2 = findViewById(R.id.text_synonyms_2);
        textAntonymsRow1 = findViewById(R.id.text_antonyms);
    }

    private void fetchWordDetails(String word) {

        dictionaryService.getDefinitions(word).enqueue(new Callback<List<DefinitionResponse>>() {
            @Override
            public void onResponse(Call<List<DefinitionResponse>> call, Response<List<DefinitionResponse>> response) {

                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    showUnavailable();
                    return;
                }

                DefinitionResponse data = response.body().get(0);

                List<Phonetic> phonetics = data.getPhonetics();
                if (phonetics != null && !phonetics.isEmpty()) {
                    Phonetic p = phonetics.get(0);

                    if (p.getText() != null)
                        textPhonetic.setText(p.getText());

                    if (p.getAudio() != null && !p.getAudio().isEmpty())
                        iconAudio.setOnClickListener(v -> playAudio(p.getAudio()));
                }

                List<Meaning> meanings = data.getMeanings();
                if (meanings == null || meanings.isEmpty()) {
                    showUnavailable();
                    return;
                }

                Meaning meaning = meanings.get(0);

                textPartOfSpeech.setText(meaning.getPartOfSpeech());

                if (!meaning.getDefinitions().isEmpty()) {
                    Definition def = meaning.getDefinitions().get(0);
                    textDefinition.setText(def.getDefinition());
                    textExample.setText(def.getExample() != null ? def.getExample() : "—");
                }

                addChips(textSynonymsRow1, meaning.getSynonyms());
                addChips(textSynonymsRow2, meaning.getSynonyms());
                addChips(textAntonymsRow1, meaning.getAntonyms());
            }

            @Override
            public void onFailure(Call<List<DefinitionResponse>> call, Throwable t) {
                Log.e(TAG, "Error loading definition", t);
                showUnavailable();
            }
        });
    }

    private void addChips(FlexboxLayout layout, List<String> list) {
        layout.removeAllViews();

        if (list == null || list.isEmpty()) {
            layout.addView(makeChip("—"));
            return;
        }

        for (String word : list) {
            layout.addView(makeChip(word));
        }
    }

    private Chip makeChip(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setChipBackgroundColorResource(R.color.blue);
        chip.setTextColor(getColor(R.color.white));
        chip.setClickable(false);
        chip.setCheckable(false);

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        chip.setLayoutParams(params);
        return chip;
    }

    private void showUnavailable() {
        textDefinition.setText("Definition unavailable.");
        textExample.setText("—");
        textPhonetic.setText("");

        textSynonymsRow1.removeAllViews();
        textSynonymsRow1.addView(makeChip("—"));

        textSynonymsRow2.removeAllViews();
        textAntonymsRow1.removeAllViews();
        textAntonymsRow1.addView(makeChip("—"));
    }

    private void playAudio(String url) {
        try {
            if (mediaPlayer != null)
                mediaPlayer.release();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);

        } catch (IOException e) {
            Log.e(TAG, "Audio play error", e);
        }
    }
}
