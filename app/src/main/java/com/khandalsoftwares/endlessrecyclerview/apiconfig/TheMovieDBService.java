package com.khandalsoftwares.endlessrecyclerview.apiconfig;

import com.khandalsoftwares.endlessrecyclerview.TopRatedMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Khandal Software on 2/28/18,58
 * We will define all our API calls here
 */

public interface TheMovieDBService {
    @GET("movie/popular")
    Call<TopRatedMovies> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int pageIndex
    );
}
