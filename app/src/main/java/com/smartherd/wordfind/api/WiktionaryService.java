package com.smartherd.wordfind.api;

import com.smartherd.wordfind.model.WiktionaryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WiktionaryService {

    String BASE_URL = "https://en.wiktionary.org/api/rest_v1/";

    @GET("page/definition/{word}")
    Call<WiktionaryResponse> getDefinitions(@Path("word") String word);
}
