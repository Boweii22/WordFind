package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Represents a single Datamuse API word result.
 * Fully null-safe, serializable, and robust against missing API fields.
 */
public class DatamuseWord implements Serializable {

    @SerializedName("word")
    private String word = "";

    @SerializedName("score")
    private int score = 0;

    @SerializedName("numSyllables")
    private int numSyllables = 0;

    // -----------------------------
    // CONSTRUCTORS
    // -----------------------------

    public DatamuseWord() {
        // Empty default constructor for Gson and manual creation
    }

    public DatamuseWord(String word, int score, int numSyllables) {
        this.word = word != null ? word : "";
        this.score = score;
        this.numSyllables = numSyllables;
    }

    // -----------------------------
    // GETTERS (safe & guaranteed)
    // -----------------------------

    public String getWord() {
        return word != null ? word : "";
    }

    public int getScore() {
        return score;
    }

    public int getNumSyllables() {
        return numSyllables;
    }

    // -----------------------------
    // SETTERS (optional but safe)
    // Only used when modifying manually
    // -----------------------------

    public void setWord(String word) {
        this.word = (word != null ? word : "");
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setNumSyllables(int numSyllables) {
        this.numSyllables = numSyllables;
    }

    // -----------------------------
    // DEBUGGING HELPER
    // -----------------------------

    @Override
    public String toString() {
        return "DatamuseWord{" +
                "word='" + getWord() + '\'' +
                ", score=" + score +
                ", numSyllables=" + numSyllables +
                '}';
    }
}
