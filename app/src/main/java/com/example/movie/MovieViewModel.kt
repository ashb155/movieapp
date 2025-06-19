package com.example.movie

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing



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
                error = ErrorMessage(e)
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

    fun getGenreText(movie: Movie): String {
        return movie.genreIds.mapNotNull { id ->
            genres.find { it.id == id }?.name
        }.joinToString(", ")
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
                error = ErrorMessage(e)
            }
        }
    }

    fun refreshMovies(){
        viewModelScope.launch{
            _isRefreshing.value=true
            fetchMoviesByGenres()
            _isRefreshing.value=false
        }
    }

    private fun ErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host", ignoreCase = true) == true -> {
                "No internet connection. Please check your network."
            }
            e.message?.contains("timeout", ignoreCase = true) == true -> {
                "The request timed out. Please try again later."
            }
            e.message?.contains("404", ignoreCase = true) == true -> {
                "Content not found."
            }
            else -> {
                "We are facing technical issues. Please try again later."
            }
        }
    }}
