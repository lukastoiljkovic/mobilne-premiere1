package com.example.premiere.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieImage(
    val filePath: String,
    val width: Int? = null,
    val height: Int? = null
)