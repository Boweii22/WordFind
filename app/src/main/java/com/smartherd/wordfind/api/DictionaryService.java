package com.smartherd.wordfind.api;


import com.smartherd.wordfind.model.DefinitionResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DictionaryService {
    String BASE_URL = "https://api.dictionaryapi.dev/api/v2/";

    /**
     * Fetches definitions for a given word.
     * @param word The word to look up (e.g., "serene").
     * @return A list of DefinitionResponse objects.
     */
    @GET("entries/en/{word}")
    Call<List<DefinitionResponse>> getDefinitions(@Path("word") String word);
}