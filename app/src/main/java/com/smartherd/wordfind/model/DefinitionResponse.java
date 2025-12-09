package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a dictionary API response for a word.
 * Provides safe access to meanings and prevents crashes from null fields.
 */
public class DefinitionResponse implements Serializable {

    @SerializedName("meanings")
    private List<Meaning> meanings = new ArrayList<>();

    // -----------------------------
    // NULL-SAFE GETTER
    // -----------------------------
    public List<Meaning> getMeanings() {
        return meanings != null ? meanings : Collections.emptyList();
    }

    // -----------------------------
    // SETTER (optional but safe)
    // -----------------------------
    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings != null ? meanings : new ArrayList<>();
    }

    // -----------------------------
    // DEBUGGING SUPPORT
    // -----------------------------
    @Override
    public String toString() {
        return "DefinitionResponse{" +
                "meanings=" + getMeanings() +
                '}';
    }
}
