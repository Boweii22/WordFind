package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single definition block inside a Meaning from dictionaryapi.dev.
 * Includes example, synonyms, antonyms. Fully null-safe & Serializable.
 */
public class Definition implements Serializable {

    @SerializedName("definition")
    private String definition = "";

    @SerializedName("example")
    private String example = "";

    @SerializedName("synonyms")
    private List<String> synonyms = new ArrayList<>();

    @SerializedName("antonyms")
    private List<String> antonyms = new ArrayList<>();

    // ---------------------------------------------------
    // GETTERS (null-safe)
    // ---------------------------------------------------

    public String getDefinition() {
        return definition != null ? definition : "";
    }

    public String getExample() {
        return example != null ? example : "";
    }

    public List<String> getSynonyms() {
        return synonyms != null ? synonyms : Collections.emptyList();
    }

    public List<String> getAntonyms() {
        return antonyms != null ? antonyms : Collections.emptyList();
    }

    // ---------------------------------------------------
    // SETTERS (safe)
    // ---------------------------------------------------

    public void setDefinition(String definition) {
        this.definition = definition != null ? definition : "";
    }

    public void setExample(String example) {
        this.example = example != null ? example : "";
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms != null ? synonyms : new ArrayList<>();
    }

    public void setAntonyms(List<String> antonyms) {
        this.antonyms = antonyms != null ? antonyms : new ArrayList<>();
    }

    // ---------------------------------------------------
    // Debug
    // ---------------------------------------------------

    @Override
    public String toString() {
        return "Definition{" +
                "definition='" + getDefinition() + '\'' +
                ", example='" + getExample() + '\'' +
                ", synonyms=" + getSynonyms() +
                ", antonyms=" + getAntonyms() +
                '}';
    }
}
