package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponse(
    val images: List<MovieImage> = emptyList()
)