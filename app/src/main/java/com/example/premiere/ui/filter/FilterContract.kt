package com.example.premiere.ui.filter

import com.example.premiere.data.model.FilterParams
import com.example.premiere.data.model.Genre

interface FilterContract {

    data class UiState(
        val query: String = "",
        val genres: List<Genre> = emptyList(),
        val selectedGenreId: Int? = null,
        val minYear: String = "",
        val maxYear: String = "",
        val minRating: Float = 0f,
        val isLoadingGenres: Boolean = false
    )

    sealed class UiEvent {
        data class QueryChanged(val query: String) : UiEvent()
        data class GenreSelected(val genreId: Int) : UiEvent()
        data class MinYearChanged(val year: String) : UiEvent()
        data class MaxYearChanged(val year: String) : UiEvent()
        data class MinRatingChanged(val rating: Float) : UiEvent()
        object ClearAll : UiEvent()
        object ApplyFilters : UiEvent()
    }

    sealed class SideEffect {
        data class FiltersApplied(val filters: FilterParams) : SideEffect()
    }
}