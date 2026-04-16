package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieVideo(
    val key: String,
    val name: String? = null,
    val site: String? = null,
    val type: String? = null
)