package com.example.movie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MovieRepository(private val apiService: MovieApiService, ) { 
    
    private val apiKey = "63331023e6b62fc328b87bd9bc6dbfbe"

    suspend fun getLatestMovies() = apiService.getLatestMovies(apiKey)

    suspend fun searchMovies(query: String) = apiService.searchMovies(apiKey, query)

    suspend fun getMovieCredits(movieId: Int) = apiService.getMovieCredits(movieId, apiKey)

    suspend fun getMovieDetails(movieId: Int) = apiService.getMovieDetails(movieId, apiKey)

    suspend fun getMovieVideos(movieId: Int) = apiService.getMovieVideos(movieId, apiKey)

    suspend fun getGenres() = apiService.getGenres(apiKey)

    suspend fun getMoviesByGenre(genreIds: String) = apiService.getMoviesByGenre(apiKey, genreIds)

    var currentPage = MutableStateFlow(1)
//        private set

    var totalPages = MutableStateFlow(1)
//        private set

    var lastQuery = MutableStateFlow("")
//        private set

    var movies = MutableStateFlow<List<Movie>>(emptyList())
     //   private set

    var selectedGenreIds = MutableStateFlow<List<Int>>(emptyList())

    var error = MutableStateFlow<String?>(null)
      //  private set

    var genres = MutableStateFlow<List<Genre>>(emptyList())
        //private set


    private fun ErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host", true) == true -> "No internet connection."
            e.message?.contains("timeout", true) == true -> "Request timed out."
            e.message?.contains("404", true) == true -> "Content not found."
            else -> "Something went wrong."
        }
    }

    private enum class FetchMode { DEFAULT, GENRE, SEARCH }
    private var lastMode = FetchMode.DEFAULT

    suspend fun loadMovies(page: Int = 1, query: String = "", genres: List<Int> = emptyList()) {
            try {
                if (query.isNotBlank() && genres.isNotEmpty()) {
                    val response = RetrofitInstance.api.searchMovies(apiKey, query, page)
                    val filteredResults = response.results.filter { movie ->
                        movie.genreIds.any { genres.contains(it) }
                    }
                    currentPage.value = page
                    totalPages.value= response.total_pages
                    movies.value = filteredResults
                    error.value = null
                    lastMode = FetchMode.SEARCH
                    lastQuery.value = query
                    selectedGenreIds.value = genres
                } else if (query.isNotBlank()) {
                    val response = RetrofitInstance.api.searchMovies(apiKey, query, page)
                    currentPage.value = page
                    totalPages.value = response.total_pages
                    movies.value = response.results
                    error.value = null
                    lastMode = FetchMode.SEARCH
                    lastQuery.value = query
                    selectedGenreIds.value= emptyList()
                } else if (genres.isNotEmpty()) {
                    val genresParam = genres.joinToString(",")
                    val response = RetrofitInstance.api.getMoviesByGenre(apiKey, genresParam, page)
                    currentPage.value = page
                    totalPages.value = response.total_pages
                    movies.value = response.results
                    error.value = null
                    lastMode = FetchMode.GENRE
                    lastQuery.value = ""
                    selectedGenreIds.value = genres
                } else {
                    val response = RetrofitInstance.api.getLatestMovies(apiKey, page)
                    currentPage.value = page
                    totalPages.value = response.total_pages
                    movies.value = response.results
                    error.value = null
                    lastMode = FetchMode.DEFAULT
                    lastQuery.value = ""
                    selectedGenreIds.value = emptyList()
                }
            } catch (e: Exception) {
                error.value = ErrorMessage(e)
            }
        
    }

    suspend fun searchMovies(query: String,currentPage:Int) {
        this.currentPage.value = currentPage
        this.lastQuery.value = query
        loadMovies(1, query, selectedGenreIds.value)
    }


    /*   suspend fun fetchGenres() {
               try {
                   val response = RetrofitInstance.api.getGenres(apiKey)
                   genres.value= response.genres
                   kotlin.error = null
               } catch (e: Exception) {
                   kotlin.error = ErrorMessage(e)
               }
           }
       }

   suspend fun fetchMovieDetails(movieId: Int) {
       viewModelScope.launch {
           try {
               selectedMovie = RetrofitInstance.api.getMovieDetails(movieId, apiKey)
               error = null
               fetchMovieCredits(movieId)
           } catch (e: Exception) {
               selectedMovie = null
               error = ErrorMessage(e)
           }
       }*/
}

