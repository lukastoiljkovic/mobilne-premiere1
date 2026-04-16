package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenresResponse(
    val genres: List<Genre> = emptyList()
)