package com.example.premiere.ui.filter

import com.example.premiere.data.model.Genre

data class FilterState(
    val query: String = "",
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val minYear: String = "",
    val maxYear: String = "",
    val minRating: Float = 0f,
    val isLoadingGenres: Boolean = false
)

sealed class FilterEvent {
    data class QueryChanged(val query: String) : FilterEvent()
    data class GenreSelected(val genreId: Int) : FilterEvent()
    data class MinYearChanged(val year: String) : FilterEvent()
    data class MaxYearChanged(val year: String) : FilterEvent()
    data class MinRatingChanged(val rating: Float) : FilterEvent()
    object ClearAll : FilterEvent()
    object ApplyFilters : FilterEvent()
}