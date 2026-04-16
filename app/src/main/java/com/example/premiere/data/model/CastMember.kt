package com.example.premiere.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastMember(
    @SerialName("imdbId") val id: String,
    val name: String,
    val profilePath: String? = null,
    val character: String? = null
)