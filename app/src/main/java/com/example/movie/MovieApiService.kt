package com.example.movie


import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface MovieApiService {
    @GET("movie/now_playing")
    suspend fun getLatestMovies(
        @Query("api_key") apiKey: String
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): MovieResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse
}

