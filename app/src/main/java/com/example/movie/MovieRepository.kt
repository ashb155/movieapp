package com.example.movie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

object MovieRepository {
    private lateinit var apiService: MovieApiService
    private val apiKey = "63331023e6b62fc328b87bd9bc6dbfbe"
    val ret = RetrofitInstance.api

    suspend fun getLatestMovies() = apiService.getLatestMovies(apiKey)

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

    var selectedMovie = MutableStateFlow<Movie?>(null)
    //private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    var cast by mutableStateOf<List<Actor>>(emptyList())
        private set


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
                val response = ret.searchMovies(apiKey, query, page)
                val filteredResults = response.results.filter { movie ->
                    movie.genreIds.any { genres.contains(it) }
                }
                currentPage.value = page
                totalPages.value = response.total_pages
                movies.value = filteredResults
                error.value = null
                lastMode = FetchMode.SEARCH
                lastQuery.value = query
                selectedGenreIds.value = genres
            } else if (query.isNotBlank()) {
                val response = ret.searchMovies(apiKey, query, page)
                currentPage.value = page
                totalPages.value = response.total_pages
                movies.value = response.results
                error.value = null
                lastMode = FetchMode.SEARCH
                lastQuery.value = query
                selectedGenreIds.value = emptyList()
            } else if (genres.isNotEmpty()) {
                val genresParam = genres.joinToString(",")
                val response = ret.getMoviesByGenre(apiKey, genresParam, page)
                currentPage.value = page
                totalPages.value = response.total_pages
                movies.value = response.results
                error.value = null
                lastMode = FetchMode.GENRE
                lastQuery.value = ""
                selectedGenreIds.value = genres
            } else {
                val response = ret.getLatestMovies(apiKey, page)
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

    suspend fun searchMovies(query: String) {
        currentPage.value = 1
        this.lastQuery.value = query
        loadMovies(currentPage.value, query, selectedGenreIds.value)
    }

    suspend fun fetchMoviesByGenres() {
        currentPage.value = 1
        loadMovies(currentPage.value, lastQuery.value, selectedGenreIds.value)
    }

    suspend fun fetchMovies() {
        loadMovies()
    }

    suspend fun loadNextPage() {
        this.currentPage = currentPage
        this.lastQuery = lastQuery
        this.selectedGenreIds = selectedGenreIds
        if (currentPage.value < totalPages.value) {
            loadMovies(currentPage.value + 1, lastQuery.value, selectedGenreIds.value)
        }
    }

    suspend fun loadPreviousPage() {
        this.currentPage = currentPage
        this.lastQuery = lastQuery
        this.selectedGenreIds = selectedGenreIds
        if (currentPage.value > 1) {
            loadMovies(currentPage.value - 1, lastQuery.value, selectedGenreIds.value)
        }
    }

    suspend fun refreshMovies() {
        _isRefreshing.value = true
        loadMovies(currentPage.value, lastQuery.value, selectedGenreIds.value)
        _isRefreshing.value = false
    }

    suspend fun fetchMovieCredits(movieId: Int) {
        try {
            val response = RetrofitInstance.api.getMovieCredits(movieId, apiKey)
            cast = response.cast
        } catch (e: Exception) {
            cast = emptyList()
        }
    }


    suspend fun toggleGenreSelection(genreId: Int) {
        selectedGenreIds.value = if (selectedGenreIds.value.contains(genreId)) {
            selectedGenreIds.value - genreId
        } else {
            selectedGenreIds.value + genreId
        }
        currentPage.value = 1
        loadMovies(currentPage.value, lastQuery.value, selectedGenreIds.value)
    }

    suspend fun clearSelectedGenres() {
        selectedGenreIds.value = emptyList()
        currentPage.value = 1
        loadMovies(1, lastQuery.value, selectedGenreIds.value)
    }

    suspend fun fetchGenres() {
        try {
            val response = RetrofitInstance.api.getGenres(apiKey)
            genres.value = response.genres
            error.value = null
        } catch (e: Exception) {
            error.value = ErrorMessage(e)
        }
    }


    suspend fun fetchMovieDetails(movieId: Int) {
        try {
            selectedMovie.value = RetrofitInstance.api.getMovieDetails(movieId, apiKey)
            error.value = null
            fetchMovieCredits(movieId)
        } catch (e: Exception) {
            selectedMovie.value = null
            error.value = ErrorMessage(e)
        }
    }

}


