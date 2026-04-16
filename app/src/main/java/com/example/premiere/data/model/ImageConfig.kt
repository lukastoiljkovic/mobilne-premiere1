package com.example.premiere.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageConfig(
    @SerialName("base_url") val baseUrl: String,
    val sizes: List<String> = emptyList()
)