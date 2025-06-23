package com.example.movie

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages") val total_pages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    val videos:VideosResponse?=null
)

data class VideosResponse(
    val results:List<Video>
)
data class Video(
    val id:String,
    val key:String,
    val name:String,
    val site: String,
    val type: String
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



data class GenreResponse(
    val genres:List<Genre>
)
data class Genre(
    val id:Int,
    val name:String
)

