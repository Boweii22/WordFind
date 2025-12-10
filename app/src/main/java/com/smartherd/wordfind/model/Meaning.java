package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single meaning block from dictionaryapi.dev.
 * Includes part of speech, definitions, synonyms, antonyms.
 * Fully null-safe and Serializable.
 */
public class Meaning implements Serializable {

    @SerializedName("partOfSpeech")
    private String partOfSpeech = "";

    @SerializedName("definitions")
    private List<Definition> definitions = new ArrayList<>();

    @SerializedName("synonyms")
    private List<String> synonyms = new ArrayList<>();

    @SerializedName("antonyms")
    private List<String> antonyms = new ArrayList<>();

    // ---------------------------------------------------
    // GETTERS (NULL-SAFE)
    // ---------------------------------------------------

    public String getPartOfSpeech() {
        return partOfSpeech != null ? partOfSpeech : "";
    }

    public List<Definition> getDefinitions() {
        return definitions != null ? definitions : Collections.emptyList();
    }

    public List<String> getSynonyms() {
        return synonyms != null ? synonyms : Collections.emptyList();
    }

    public List<String> getAntonyms() {
        return antonyms != null ? antonyms : Collections.emptyList();
    }

    // ---------------------------------------------------
    // SETTERS (OPTIONAL BUT SAFE)
    // ---------------------------------------------------

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech != null ? partOfSpeech : "";
    }

    public void setDefinitions(List<Definition> definitions) {
        this.definitions = definitions != null ? definitions : new ArrayList<>();
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms != null ? synonyms : new ArrayList<>();
    }

    public void setAntonyms(List<String> antonyms) {
        this.antonyms = antonyms != null ? antonyms : new ArrayList<>();
    }

    // ---------------------------------------------------
    // DEBUG / LOGGING SUPPORT
    // ---------------------------------------------------

    @Override
    public String toString() {
        return "Meaning{" +
                "partOfSpeech='" + getPartOfSpeech() + '\'' +
                ", definitions=" + getDefinitions() +
                ", synonyms=" + getSynonyms() +
                ", antonyms=" + getAntonyms() +
                '}';
    }
}
