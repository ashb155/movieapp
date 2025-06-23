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


    var cast by mutableStateOf<List<Actor>>(emptyList())
        private set

    var selectedMovie by mutableStateOf<Movie?>(null)
        private set

    var genres by mutableStateOf<List<Genre>>(emptyList())
        private set

    private val apiKey = "63331023e6b62fc328b87bd9bc6dbfbe"

    private enum class FetchMode { DEFAULT, GENRE, SEARCH }
    private var lastMode = FetchMode.DEFAULT

    fun searchMovies(query: String) {
        viewModelScope.launch{ movieRepository.searchMovies(query) }
    }

    fun fetchMoviesByGenres() {

        viewModelScope.launch{movieRepository.fetchMoviesByGenres()}

    fun fetchMovies() {
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
