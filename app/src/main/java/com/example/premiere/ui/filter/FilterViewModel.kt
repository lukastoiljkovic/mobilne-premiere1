package com.example.premiere.ui.filter

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

class FilterViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FilterContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: FilterContract.UiState.() -> FilterContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    private val _effects = MutableSharedFlow<FilterContract.SideEffect>()
    val effects = _effects.asSharedFlow()
    private fun setEffect(effect: FilterContract.SideEffect) {
        viewModelScope.launch { _effects.emit(effect) }
    }

    init {
        loadGenres()
    }

    fun setEvent(event: FilterContract.UiEvent) {
        when (event) {
            is FilterContract.UiEvent.QueryChanged -> setState { copy(query = event.query) }
            is FilterContract.UiEvent.GenreSelected -> {
                val newId = if (_state.value.selectedGenreId == event.genreId) null else event.genreId
                setState { copy(selectedGenreId = newId) }
            }
            is FilterContract.UiEvent.MinYearChanged -> setState { copy(minYear = event.year) }
            is FilterContract.UiEvent.MaxYearChanged -> setState { copy(maxYear = event.year) }
            is FilterContract.UiEvent.MinRatingChanged -> setState { copy(minRating = event.rating) }
            is FilterContract.UiEvent.ClearAll -> {
                setState { copy(query = "", selectedGenreId = null, minYear = "", maxYear = "", minRating = 0f) }
            }
            is FilterContract.UiEvent.ApplyFilters -> buildAndEmitFilters()
        }
    }

    private fun buildAndEmitFilters() {
        val s = _state.value
        val filters = FilterParams(
            query = s.query,
            genreId = s.selectedGenreId,
            minYear = s.minYear.toIntOrNull(),
            maxYear = s.maxYear.toIntOrNull(),
            minRating = if (s.minRating > 0f) s.minRating else null
        )
        setEffect(FilterContract.SideEffect.FiltersApplied(filters))
    }

    private fun loadGenres() {
        viewModelScope.launch {
            setState { copy(isLoadingGenres = true) }
            repository.getGenres()
                .onSuccess { genres -> setState { copy(genres = genres, isLoadingGenres = false) } }
                .onFailure { setState { copy(isLoadingGenres = false) } }
        }
    }
}