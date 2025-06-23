package com.example.movie

class MovieRepository(private val apiService: MovieApiService, private val apiKey: String) {

    suspend fun getLatestMovies() = apiService.getLatestMovies(apiKey)

    suspend fun searchMovies(query: String) = apiService.searchMovies(apiKey, query)

    suspend fun getMovieCredits(movieId: Int) = apiService.getMovieCredits(movieId, apiKey)

    suspend fun getMovieDetails(movieId: Int) = apiService.getMovieDetails(movieId, apiKey)

    suspend fun getMovieVideos(movieId: Int) = apiService.getMovieVideos(movieId, apiKey)

    suspend fun getGenres() = apiService.getGenres(apiKey)

    suspend fun getMoviesByGenre(genreIds: String) = apiService.getMoviesByGenre(apiKey, genreIds)

}