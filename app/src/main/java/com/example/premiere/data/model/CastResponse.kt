package com.example.premiere.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CastResponse(
    val page: Int = 1,
    val items: List<CastMember> = emptyList()
)