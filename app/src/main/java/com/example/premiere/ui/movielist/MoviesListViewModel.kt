package com.example.premiere.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiere.data.model.FilterParams
import com.example.premiere.data.api.MovieRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class MoviesListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesListContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: MoviesListContract.UiState.() -> MoviesListContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val _effects = MutableSharedFlow<MoviesListContract.SideEffect>()
    val effects = _effects.asSharedFlow()
    private fun setEffect(effect: MoviesListContract.SideEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }

    var currentFilters: FilterParams = FilterParams()
        private set

    init {
        setEvent(MoviesListContract.UiEvent.LoadMovies)
    }

    fun setEvent(event: MoviesListContract.UiEvent) {
        when (event) {
            is MoviesListContract.UiEvent.LoadMovies -> loadMovies()
            is MoviesListContract.UiEvent.MovieClicked -> {
                setEffect(MoviesListContract.SideEffect.NavigateToDetails(event.movieId))
            }
            is MoviesListContract.UiEvent.FilterClicked -> {
                setEffect(MoviesListContract.SideEffect.NavigateToFilter)
            }
            is MoviesListContract.UiEvent.SortChanged -> {
                setState { copy(sortBy = event.sortBy) }
                loadMovies()
            }
            is MoviesListContract.UiEvent.ToggleSortOrder -> {
                val newOrder = if (_state.value.sortOrder == "desc") "asc" else "desc"
                setState { copy(sortOrder = newOrder) }
                loadMovies()
            }
        }
    }

    fun applyFilters(filters: FilterParams) {
        currentFilters = filters
        setState { copy(activeFiltersCount = filters.activeCount()) }
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
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
                    setState {
                        copy(
                            movies = response.items,
                            totalMovies = response.items.size,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    setState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Error"
                        )
                    }
                }
            )
        }
    }
}