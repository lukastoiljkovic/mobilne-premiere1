package com.example.premiere.ui.movies

import com.example.premiere.data.model.Movie

data class MoviesListState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortBy: String = "imdb_rating",
    val sortOrder: String = "desc",
    val activeFiltersCount: Int = 0,
    val totalMovies: Int = 0
)

sealed class MoviesListEvent {
    object LoadMovies : MoviesListEvent()
    data class MovieClicked(val movieId: String) : MoviesListEvent()
    object FilterClicked : MoviesListEvent()
    data class SortChanged(val sortBy: String) : MoviesListEvent()
    object ToggleSortOrder : MoviesListEvent()
}

sealed class MoviesListEffect {
    data class NavigateToDetails(val movieId: String) : MoviesListEffect()
    object NavigateToFilter : MoviesListEffect()
}