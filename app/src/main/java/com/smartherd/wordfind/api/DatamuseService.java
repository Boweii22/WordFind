package com.smartherd.wordfind.api;

import com.smartherd.wordfind.model.DatamuseWord;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DatamuseService {
    String BASE_URL = "https://api.datamuse.com/";

    /**
     * Finds words that "mean like" the input phrase (Reverse Dictionary).
     * @param phrase The meaning or description typed by the user.
     * @return A list of DatamuseWord objects ranked by score.
     */
    @GET("words")
    Call<List<DatamuseWord>> getMeaningLikeWords(@Query("ml") String phrase);
}