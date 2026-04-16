package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MoviesResponse(
    val page: Int = 1,
    val pageSize: Int = 30,
    val totalItems: Int = 0,
    val totalPages: Int = 0,
    val items: List<Movie> = emptyList()
)