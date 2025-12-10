package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class WiktionaryDefinition implements Serializable {

    @SerializedName("definition")
    private String definition;

    @SerializedName("examples")
    private List<String> examples;

    public String getDefinition() {
        return definition != null ? definition : "";
    }

    public List<String> getExamples() {
        return examples;
    }
}

