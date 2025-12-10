package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single dictionary API response entry.
 * Includes phonetics, meanings, synonyms, antonyms, etc.
 * Fully null-safe to prevent crashes.
 */
public class DefinitionResponse implements Serializable {

    @SerializedName("word")
    private String word;

    @SerializedName("phonetics")
    private List<Phonetic> phonetics = new ArrayList<>();

    @SerializedName("meanings")
    private List<Meaning> meanings = new ArrayList<>();

    // -----------------------------
    // GETTERS — NULL-SAFE
    // -----------------------------

    public String getWord() {
        return word != null ? word : "";
    }

    public List<Phonetic> getPhonetics() {
        return phonetics != null ? phonetics : Collections.emptyList();
    }

    public List<Meaning> getMeanings() {
        return meanings != null ? meanings : Collections.emptyList();
    }

    // -----------------------------
    // SETTERS (optional but safe)
    // -----------------------------

    public void setPhonetics(List<Phonetic> phonetics) {
        this.phonetics = phonetics != null ? phonetics : new ArrayList<>();
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings != null ? meanings : new ArrayList<>();
    }

    // -----------------------------
    // DEBUGGING SUPPORT
    // -----------------------------
    @Override
    public String toString() {
        return "DefinitionResponse{" +
                "word='" + word + '\'' +
                ", phonetics=" + phonetics +
                ", meanings=" + meanings +
                '}';
    }
}
