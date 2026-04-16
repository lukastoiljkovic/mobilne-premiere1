package com.example.premiere.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiere.data.model.FilterParams
import com.example.premiere.data.remote.MovieRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesListState())
    val state: StateFlow<MoviesListState> = _state.asStateFlow()

    private val _effect = Channel<MoviesListEffect>()
    val effect = _effect.receiveAsFlow()

    var currentFilters: FilterParams = FilterParams()
        private set

    init {
        onEvent(MoviesListEvent.LoadMovies)
    }

    fun onEvent(event: MoviesListEvent) {
        when (event) {
            is MoviesListEvent.LoadMovies -> loadMovies()
            is MoviesListEvent.MovieClicked -> {
                viewModelScope.launch {
                    _effect.send(MoviesListEffect.NavigateToDetails(event.movieId))
                }
            }
            is MoviesListEvent.FilterClicked -> {
                viewModelScope.launch {
                    _effect.send(MoviesListEffect.NavigateToFilter)
                }
            }
            is MoviesListEvent.SortChanged -> {
                _state.update { it.copy(sortBy = event.sortBy) }
                loadMovies()
            }
        }
    }

    fun applyFilters(filters: FilterParams) {
        currentFilters = filters
        _state.update { it.copy(activeFiltersCount = filters.activeCount()) }
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val filters = currentFilters
            val result = repository.getMovies(
                pageSize = 30,
                sortBy = _state.value.sortBy,
                sortOrder = _state.value.sortOrder,
                genreId = filters.genreId,
                query = filters.query.ifBlank { null },
                minYear = filters.minYear,
                maxYear = filters.maxYear,
                minRating = filters.minRating
            )
            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            movies = response.items,
                            totalMovies = response.totalItems,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }
}