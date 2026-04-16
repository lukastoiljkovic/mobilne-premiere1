package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CastResponse(
    val cast: List<CastMember> = emptyList()
)