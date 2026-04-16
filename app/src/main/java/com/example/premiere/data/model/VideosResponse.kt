package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VideosResponse(
    val videos: List<MovieVideo> = emptyList()
)