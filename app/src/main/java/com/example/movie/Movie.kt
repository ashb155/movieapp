package com.example.movie

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val results: List<Movie>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?
)

data class CreditsResponse(
    val cast: List<Actor>
)

data class Actor(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val character: String
)
