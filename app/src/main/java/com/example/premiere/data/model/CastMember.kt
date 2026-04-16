package com.example.premiere.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    @SerialName("profile_path") val profilePath: String? = null,
    val character: String? = null
)