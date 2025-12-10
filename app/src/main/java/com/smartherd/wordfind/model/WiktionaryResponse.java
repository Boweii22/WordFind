package com.smartherd.wordfind.model;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WiktionaryResponse implements Serializable {

    // The API returns: { "en": [ { "definitions": [ ... ] } ] }
    @SerializedName("en")
    private List<WiktionaryEntry> englishEntries;

    public List<WiktionaryEntry> getEnglishEntries() {
        return englishEntries;
    }
}
