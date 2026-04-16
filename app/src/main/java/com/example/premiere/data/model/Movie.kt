package com.example.premiere.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("imdbId") val id: String,
    val title: String,
    val year: Int? = null,
    val imdbRating: Double? = null,
    val imdbVotes: Int? = null,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val runtime: Int? = null,
    val overview: String? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    @SerialName("languageCode") val language: String? = null,
    val popularity: Double? = null,
    val tmdbRating: Double? = null,
    val genres: List<Genre> = emptyList()
)