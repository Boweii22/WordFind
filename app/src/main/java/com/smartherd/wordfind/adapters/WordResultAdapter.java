package com.smartherd.wordfind.adapters;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartherd.wordfind.R;
import com.smartherd.wordfind.WordDetailActivity;
import com.smartherd.wordfind.model.WordResult;

import java.util.List;
import java.util.Locale;

public class WordResultAdapter extends RecyclerView.Adapter<WordResultAdapter.WordViewHolder>
        implements TextToSpeech.OnInitListener {

    private final Context context;
    private final List<WordResult> wordList;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    public WordResultAdapter(Context context, List<WordResult> wordList) {
        this.context = context;
        this.wordList = wordList;
        tts = new TextToSpeech(context, this);
    }

    // -----------------------------
    // TEXT-TO-SPEECH INITIALIZATION
    // -----------------------------
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED) {
                ttsReady = true;
            } else {
                Toast.makeText(context, "TTS language not supported.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "TTS initialization failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void shutdownTts() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        ttsReady = false;
    }

    // -----------------------------
    // VIEW HOLDER
    // -----------------------------
    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_word_card, parent, false);
        return new WordViewHolder(view);
    }

    // -----------------------------
    // BIND EACH ROW
    // -----------------------------
    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {

        WordResult item = wordList.get(position);

        // Word
        holder.textWord.setText(item.getWord());

        // Definition / POS display logic
        if (item.getDefinition() == null || item.getDefinition().trim().isEmpty()) {
            holder.textDefinition.setText("Loading definition...");
            holder.textPartOfSpeech.setVisibility(View.GONE);
        }
        else if (item.getDefinition().equals("Definition not available.")) {
            holder.textDefinition.setText("Definition not available.");
            holder.textPartOfSpeech.setVisibility(View.GONE);
        }
        else {
            holder.textDefinition.setText(item.getDefinition());
            holder.textPartOfSpeech.setVisibility(View.VISIBLE);
            holder.textPartOfSpeech.setText(item.getPartOfSpeech());
        }

        // Reset badge
        holder.textMatchBadge.setVisibility(View.GONE);

        // Apply badge
        setMatchBadge(holder.textMatchBadge, item.getMatchCategory());

        // Favorite icon
        updateFavoriteIcon(holder.iconFavorite, item.isFavorite());

        holder.iconFavorite.setOnClickListener(v -> {
            item.setFavorite(!item.isFavorite());
            updateFavoriteIcon(holder.iconFavorite, item.isFavorite());
            Toast.makeText(context,
                    item.getWord() + (item.isFavorite() ? " added to favorites" : " removed"),
                    Toast.LENGTH_SHORT).show();
        });

        // Volume (TTS)
        holder.iconVolume.setOnClickListener(v -> {
            if (ttsReady && !tts.isSpeaking()) {
                tts.speak(item.getWord(), TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(context, "TTS not ready yet", Toast.LENGTH_SHORT).show();
            }
        });

        // -----------------------------
        // OPEN WORD DETAIL PAGE
        // -----------------------------
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WordDetailActivity.class);
            intent.putExtra("word", item.getWord());
            context.startActivity(intent);
        });
    }

    // -----------------------------
    // BADGE LOGIC
    // -----------------------------
    private void setMatchBadge(TextView badge, WordResult.MatchCategory category) {
        switch (category) {
            case BEST_MATCH:
                badge.setBackgroundResource(R.drawable.badge_best_match);
                badge.setText("Best Match");
                badge.setVisibility(View.VISIBLE);
                break;

            case GOOD_MATCH:
                badge.setBackgroundResource(R.drawable.badge_good_match);
                badge.setText("Good Match");
                badge.setVisibility(View.VISIBLE);
                break;

            default:
                badge.setVisibility(View.GONE);
        }
    }

    // -----------------------------
    // FAVORITE ICON
    // -----------------------------
    private void updateFavoriteIcon(ImageView icon, boolean isFavorite) {
        icon.setImageResource(isFavorite
                ? R.drawable.ic_heart_filled
                : R.drawable.ic_heart_outline);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    // -----------------------------
    // VIEW HOLDER CLASS
    // -----------------------------
    static class WordViewHolder extends RecyclerView.ViewHolder {

        TextView textWord, textMatchBadge, textPartOfSpeech, textDefinition;
        ImageView iconVolume, iconFavorite;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            textWord = itemView.findViewById(R.id.text_word);
            textMatchBadge = itemView.findViewById(R.id.text_match_badge);
            iconVolume = itemView.findViewById(R.id.icon_volume);
            iconFavorite = itemView.findViewById(R.id.icon_favourite);
            textPartOfSpeech = itemView.findViewById(R.id.text_part_of_speech);
            textDefinition = itemView.findViewById(R.id.text_definition);
        }
    }
}
