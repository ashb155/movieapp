package com.example.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
//handles UI logic
class MovieViewModel : ViewModel() {
    private val movieRepository= MovieRepository
    val movies get() = movieRepository.movies
    val genres get() = movieRepository.genres
    val error get() = movieRepository.error
    val selectedGenreIds:StateFlow<List<Int>> get() = movieRepository.selectedGenreIds
    val currentPage:StateFlow<Int> get() = movieRepository.currentPage
    val totalPages: StateFlow<Int> get() = movieRepository.totalPages
    val isRefreshing: StateFlow<Boolean> get() = movieRepository.isRefreshing
    val selectedMovie get()= movieRepository.selectedMovie
    val cast get()=movieRepository.cast
    val lastQuery get()=movieRepository.lastQuery

    fun searchMovies(query: String) {
        viewModelScope.launch {
            movieRepository.lastQuery.value=query
            movieRepository.loadMovies(1,query,movieRepository.selectedGenreIds.value)
        }
    }

    fun fetchMoviesByGenres() {
        viewModelScope.launch {
            movieRepository.fetchMoviesByGenres()
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            movieRepository.fetchMovies()
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            movieRepository.loadNextPage()
        }
    }

    fun loadPreviousPage() {
        viewModelScope.launch {
            movieRepository.loadPreviousPage()
        }
    }

    fun refreshMovies() {
        viewModelScope.launch {
            movieRepository.refreshMovies()
        }
    }

    fun fetchGenres() {
        viewModelScope.launch {
            movieRepository.fetchGenres()
        }
    }

    fun fetchMovieCredits(movieId: Int) {
        viewModelScope.launch {
            movieRepository.fetchMovieCredits(movieId)
        }
    }

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            movieRepository.fetchMovieDetails(movieId)
        }
    }

    fun toggleGenreSelection(genreId: Int) {
        viewModelScope.launch {
            movieRepository.toggleGenreSelection(genreId)
        }
    }

    fun clearSelectedGenres() {
        viewModelScope.launch {
            movieRepository.clearSelectedGenres()
        }
    }

    fun getImageUrl(posterPath: String?): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getBackdropUrl(backdropPath: String?): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
    }

    fun ErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host", true) == true -> "No internet connection."
            e.message?.contains("timeout", true) == true -> "Request timed out."
            e.message?.contains("404", true) == true -> "Content not found."
            else -> "Something went wrong."
        }
    }
}
