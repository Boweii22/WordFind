package com.smartherd.wordfind.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Represents a single definition entry returned by the Dictionary API.
 * Fully null-safe and Serializable for safe Intent passing.
 */
public class Definition implements Serializable {

    @SerializedName("definition")
    private String definition = "";

    // -----------------------------
    // GETTER (null-safe)
    // -----------------------------
    public String getDefinition() {
        return definition != null ? definition : "";
    }

    // -----------------------------
    // SETTER (optional but safe)
    // -----------------------------
    public void setDefinition(String definition) {
        this.definition = definition != null ? definition : "";
    }

    // -----------------------------
    // DEBUGGING SUPPORT
    // -----------------------------
    @Override
    public String toString() {
        return "Definition{" +
                "definition='" + getDefinition() + '\'' +
                '}';
    }
}
