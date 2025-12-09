package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single meaning block for a word from the Dictionary API.
 * Fully null-safe, Serializable, and robust against missing API fields.
 */
public class Meaning implements Serializable {

    @SerializedName("partOfSpeech")
    private String partOfSpeech = "";

    @SerializedName("definitions")
    private List<Definition> definitions = new ArrayList<>();

    // ---------------------------------------------------
    // GETTERS (null-safe)
    // ---------------------------------------------------

    public String getPartOfSpeech() {
        return partOfSpeech != null ? partOfSpeech : "";
    }

    public List<Definition> getDefinitions() {
        return definitions != null ? definitions : Collections.emptyList();
    }

    // ---------------------------------------------------
    // SETTERS (optional but safe)
    // ---------------------------------------------------

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech != null ? partOfSpeech : "";
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions != null ? definitions : new ArrayList<>();
    }

    // ---------------------------------------------------
    // DEBUGGING SUPPORT
    // ---------------------------------------------------

    @Override
    public String toString() {
        return "Meaning{" +
                "partOfSpeech='" + getPartOfSpeech() + '\'' +
                ", definitions=" + getDefinitions() +
                '}';
    }
}
