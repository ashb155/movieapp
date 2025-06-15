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

    init {
        fetchMovies()
    }

   fun fetchMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getLatestMovies("63331023e6b62fc328b87bd9bc6dbfbe")
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
                if(query.isBlank()){
                    val response = RetrofitInstance.api.searchMovies("63331023e6b62fc328b87bd9bc6dbfbe", query)
                    movies = response.results ?: emptyList()}
                else{
                    val response = RetrofitInstance.api.searchMovies("63331023e6b62fc328b87bd9bc6dbfbe", query)
                    movies = response.results ?: emptyList()
                }
                error = null
            } catch (e: Exception) {
                error = "Search failed: ${e.message}"
            }
        }
    }

}


