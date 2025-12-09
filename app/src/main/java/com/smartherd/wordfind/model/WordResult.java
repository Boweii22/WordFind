package com.smartherd.wordfind.model;

import java.io.Serializable;

/**
 * Represents a final word result shown to the user.
 * Fully null-safe, stable, and optimized for RecyclerView usage.
 */
public class WordResult implements Serializable {

    private String word = "";
    private int score = 0;
    private String definition = "Loading definition...";
    private String partOfSpeech = "N/A";
    private MatchCategory matchCategory = MatchCategory.SUGGESTION;
    private boolean isFavorite = false;

    public enum MatchCategory {
        BEST_MATCH,
        GOOD_MATCH,
        SUGGESTION
    }

    // --------------------------------------------------------
    // SAFE CONSTRUCTOR (from DatamuseWord → WordResult)
    // --------------------------------------------------------
    public WordResult(DatamuseWord datamuseWord, MatchCategory category) {
        if (datamuseWord != null) {
            this.word = datamuseWord.getWord();
            this.score = datamuseWord.getScore();
        }
        this.matchCategory = (category != null ? category : MatchCategory.SUGGESTION);
    }

    // --------------------------------------------------------
    // NULL-SAFE GETTERS
    // --------------------------------------------------------

    public String getWord() {
        return word != null ? word : "";
    }

    public int getScore() {
        return score;
    }

    public MatchCategory getMatchCategory() {
        return matchCategory != null ? matchCategory : MatchCategory.SUGGESTION;
    }

    public String getDefinition() {
        return definition != null ? definition : "Definition not available.";
    }

    public String getPartOfSpeech() {
        return partOfSpeech != null ? partOfSpeech : "N/A";
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // --------------------------------------------------------
    // SAFE SETTERS
    // --------------------------------------------------------

    public void setWord(String word) {
        this.word = word != null ? word : "";
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setMatchCategory(MatchCategory category) {
        this.matchCategory = category != null ? category : MatchCategory.SUGGESTION;
    }

    public void setDefinition(String definition) {
        this.definition = definition != null ? definition : "Definition not available.";
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech != null ? partOfSpeech : "N/A";
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // --------------------------------------------------------
    // DEBUGGING SUPPORT
    // --------------------------------------------------------
    @Override
    public String toString() {
        return "WordResult{" +
                "word='" + getWord() + '\'' +
                ", score=" + score +
                ", definition='" + getDefinition() + '\'' +
                ", partOfSpeech='" + getPartOfSpeech() + '\'' +
                ", category=" + getMatchCategory() +
                ", isFavorite=" + isFavorite +
                '}';
    }
}
