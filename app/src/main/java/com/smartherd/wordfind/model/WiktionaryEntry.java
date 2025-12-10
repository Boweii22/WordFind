package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class WiktionaryEntry implements Serializable {

    @SerializedName("language")
    private String language;

    @SerializedName("partOfSpeech")
    private String partOfSpeech;

    @SerializedName("definitions")
    private List<WiktionaryDefinition> definitions;

    public List<WiktionaryDefinition> getDefinitions() {
        return definitions;
    }

    public String getPartOfSpeech() {
        return partOfSpeech != null ? partOfSpeech : "";
    }
}
