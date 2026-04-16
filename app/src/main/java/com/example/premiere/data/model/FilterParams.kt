package com.example.premiere.data.model

data class FilterParams(
    val query: String = "",
    val genreId: Int? = null,
    val minYear: Int? = null,
    val maxYear: Int? = null,
    val minRating: Float? = null
) {
    fun activeCount(): Int {
        var count = 0
        if (query.isNotBlank()) count++
        if (genreId != null) count++
        if (minYear != null) count++
        if (maxYear != null) count++
        if (minRating != null && minRating > 0f) count++
        return count
    }
}