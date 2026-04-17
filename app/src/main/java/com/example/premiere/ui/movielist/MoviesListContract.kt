package com.example.premiere.ui.movielist

import com.example.premiere.data.model.Movie

interface MoviesListContract {

    data class UiState(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val sortBy: String = "imdb_rating",
        val sortOrder: String = "desc",
        val activeFiltersCount: Int = 0,
        val totalMovies: Int = 0
    )

    sealed class UiEvent {
        object LoadMovies : UiEvent()
        data class MovieClicked(val movieId: String) : UiEvent()
        object FilterClicked : UiEvent()
        data class SortChanged(val sortBy: String) : UiEvent()
        object ToggleSortOrder : UiEvent()
    }

    sealed class SideEffect {
        data class NavigateToDetails(val movieId: String) : SideEffect()
        object NavigateToFilter : SideEffect()
    }
}