package com.example.movie

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
//defining API interface to send requests and handle responses
interface MovieApiService {
    @GET("movie/now_playing")
    suspend fun getLatestMovies(
        @Query("api_key") apiKey: String,
        @Query("page")page:Int=1
    ): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page")page:Int=1
    ): MovieResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditsResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "videos"
    ): Movie
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): VideosResponse

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String
    ): GenreResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreIds: String,
        @Query("page")page:Int=1
    ): MovieResponse
}
