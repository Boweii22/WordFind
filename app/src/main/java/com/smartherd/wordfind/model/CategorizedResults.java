package com.smartherd.wordfind.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A clean, safe, well-structured container for categorized results.
 * Provides encapsulation, guaranteed initialization, and immutable getters.
 */
public class CategorizedResults implements Serializable {

    private final List<DatamuseWord> bestMatch;
    private final List<DatamuseWord> goodMatches;
    private final List<DatamuseWord> otherSuggestions;

    public CategorizedResults() {
        // Guaranteed non-null lists
        this.bestMatch = new ArrayList<>();
        this.goodMatches = new ArrayList<>();
        this.otherSuggestions = new ArrayList<>();
    }

    // ---------------------------
    // ADDERS (safe & controlled)
    // ---------------------------
    public void addBestMatch(DatamuseWord word) {
        if (word != null) bestMatch.add(word);
    }

    public void addGoodMatch(DatamuseWord word) {
        if (word != null) goodMatches.add(word);
    }

    public void addSuggestion(DatamuseWord word) {
        if (word != null) otherSuggestions.add(word);
    }

    // ---------------------------
    // IMMUTABLE GETTERS
    // Prevent external modification
    // ---------------------------
    public List<DatamuseWord> getBestMatch() {
        return Collections.unmodifiableList(bestMatch);
    }

    public List<DatamuseWord> getGoodMatches() {
        return Collections.unmodifiableList(goodMatches);
    }

    public List<DatamuseWord> getOtherSuggestions() {
        return Collections.unmodifiableList(otherSuggestions);
    }

    // ---------------------------
    // HELPERS
    // ---------------------------
    public boolean isEmpty() {
        return bestMatch.isEmpty() &&
                goodMatches.isEmpty() &&
                otherSuggestions.isEmpty();
    }

    public int totalCount() {
        return bestMatch.size() + goodMatches.size() + otherSuggestions.size();
    }
}
