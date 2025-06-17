package com.example.movie

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var cast by mutableStateOf<List<Actor>>(emptyList())
        private set

    var selectedMovie by mutableStateOf<Movie?>(null)
        private set

    var genres by mutableStateOf<List<Genre>>(emptyList())
        private set

    var selectedGenreIds by mutableStateOf<List<Int>>(emptyList())

    private val apiKey = "63331023e6b62fc328b87bd9bc6dbfbe"

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getLatestMovies(apiKey)
                movies = response.results
                error = null
            } catch (e: Exception) {
                error = "Failed to load movies: ${e.message}"
            }
        }
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            try {
                val response = if (query.isBlank()) {
                    RetrofitInstance.api.getLatestMovies(apiKey)
                } else {
                    RetrofitInstance.api.searchMovies(apiKey, query)
                }
                movies = response.results
                error = null
            } catch (e: Exception) {
                error = "Search failed: ${e.message}"
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
                error = "Failed to load movie details: ${e.message}"
            }
        }
    }

    fun fetchGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getGenres(apiKey)
                genres = response.genres
                error = null
            } catch (e: Exception) {
                error = "Failed to load genres: ${e.message}"
            }
        }
    }

    fun toggleGenreSelection(genreId:Int){
        selectedGenreIds=if(selectedGenreIds.contains(genreId)){
            selectedGenreIds-genreId
        }else{
            selectedGenreIds+genreId
        }
    }

    fun fetchMoviesByGenres() {
        viewModelScope.launch {
            try {
                if (selectedGenreIds.isEmpty()) {
                    val response = RetrofitInstance.api.getLatestMovies(apiKey)
                    movies = response.results
                } else {
                    val genreIdsParam = selectedGenreIds.joinToString(separator = ",")
                    val response = RetrofitInstance.api.getMoviesByGenre(apiKey, genreIdsParam)
                    movies = response.results
                }
                error = null
            } catch (e: Exception) {
                error = "Failed to fetch movies: ${e.message}"
            }
        }
    }

}