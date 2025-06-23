package com.example.movie

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Path

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

   /* var movies by mutableStateOf<List<Movie>>(emptyList())
        private set*/

    /*var error by mutableStateOf<String?>(null)
        private set*/

    var cast by mutableStateOf<List<Actor>>(emptyList())
        private set

    var selectedMovie by mutableStateOf<Movie?>(null)
        private set

    var genres by mutableStateOf<List<Genre>>(emptyList())
        private set

    /*var selectedGenreIds by mutableStateOf<List<Int>>(emptyList())*/

    /*var currentPage by mutableStateOf(1)
        private set*/

    /*var totalPages by mutableStateOf(1)
        private set*/

//    var lastQuery by mutableStateOf("")
//        private set

    private val apiKey = "63331023e6b62fc328b87bd9bc6dbfbe"

    private enum class FetchMode { DEFAULT, GENRE, SEARCH }
    private var lastMode = FetchMode.DEFAULT

 /*   fun loadMovies(page: Int = 1, query: String = "", genres: List<Int> = emptyList()) {
        viewModelScope.launch {
            try {
                if (query.isNotBlank() && genres.isNotEmpty()) {
                    val response = RetrofitInstance.api.searchMovies(apiKey, query, page)
                    val filteredResults = response.results.filter { movie ->
                        movie.genreIds.any { genres.contains(it) }
                    }
                    currentPage = page
                    totalPages = response.total_pages
                    movies = filteredResults
                    error = null
                    lastMode = FetchMode.SEARCH
                    lastQuery = query
                    selectedGenreIds = genres
                } else if (query.isNotBlank()) {
                    val response = RetrofitInstance.api.searchMovies(apiKey, query, page)
                    currentPage = page
                    totalPages = response.total_pages
                    movies = response.results
                    error = null
                    lastMode = FetchMode.SEARCH
                    lastQuery = query
                    selectedGenreIds = emptyList()
                } else if (genres.isNotEmpty()) {
                    val genresParam = genres.joinToString(",")
                    val response = RetrofitInstance.api.getMoviesByGenre(apiKey, genresParam, page)
                    currentPage = page
                    totalPages = response.total_pages
                    movies = response.results
                    error = null
                    lastMode = FetchMode.GENRE
                    lastQuery = ""
                    selectedGenreIds = genres
                } else {
                    val response = RetrofitInstance.api.getLatestMovies(apiKey, page)
                    currentPage = page
                    totalPages = response.total_pages
                    movies = response.results
                    error = null
                    lastMode = FetchMode.DEFAULT
                    lastQuery = ""
                    selectedGenreIds = emptyList()
                }
            } catch (e: Exception) {
                error = ErrorMessage(e)
            }
        }
    }*/

    fun searchMovies(query: String) {
        currentPage = 1
        lastQuery = query
        viewModelScope.launch{movieRepository.loadMovies(1, query, selectedGenreIds)}
    }

    fun fetchMoviesByGenres() {
        currentPage = 1
        viewModelScope.launch{movieRepository.loadMovies(1, lastQuery, selectedGenreIds)}
    }

    fun fetchMovies() {
        currentPage = 1
        selectedGenreIds = emptyList()
        lastQuery = ""
        viewModelScope.launch{movieRepository.loadMovies()}
    }

    fun loadNextPage() {
        if (currentPage < totalPages) {
            viewModelScope.launch{movieRepository.loadMovies(currentPage + 1, lastQuery, selectedGenreIds)}
        }
    }

    fun loadPreviousPage() {
        if (currentPage > 1) {
            viewModelScope.launch{movieRepository.loadMovies(currentPage - 1, lastQuery, selectedGenreIds)}
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _isRefreshing.value = true
            viewModelScope.launch{movieRepository.loadMovies(currentPage, lastQuery, selectedGenreIds)}
            _isRefreshing.value = false
        }
    }

    fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGenres(apiKey)
                genres = response.genres
                error = null
            } catch (e: Exception) {
                error = ErrorMessage(e)
            }
        }
    }

    fun fetchMovieCredits(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMovieCredits(movieId, apiKey)
                cast = response.cast
            } catch (e: Exception) {
                cast = emptyList()
            }
        }
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                selectedMovie = RetrofitInstance.api.getMovieDetails(movieId, apiKey)
                error = null
                fetchMovieCredits(movieId)
            } catch (e: Exception) {
                selectedMovie = null
                error = ErrorMessage(e)
            }
        }
    }

    fun toggleGenreSelection(genreId: Int) {
        selectedGenreIds = if (selectedGenreIds.contains(genreId)) {
            selectedGenreIds - genreId
        } else {
            selectedGenreIds + genreId
        }
        currentPage = 1
        viewModelScope.launch{movieRepository.loadMovies(1, lastQuery, selectedGenreIds)}
    }

    fun clearSelectedGenres() {
        selectedGenreIds = emptyList()
        currentPage = 1
        viewModelScope.launch{movieRepository.loadMovies(1, lastQuery, selectedGenreIds)}
    }

    fun getImageUrl(posterPath: String?): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getBackdropUrl(backdropPath: String?): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
    }

    private fun ErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host", true) == true -> "No internet connection."
            e.message?.contains("timeout", true) == true -> "Request timed out."
            e.message?.contains("404", true) == true -> "Content not found."
            else -> "Something went wrong."
        }
    }
}
