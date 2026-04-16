package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    val backdrops: List<MovieImage> = emptyList()
)