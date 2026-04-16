package com.example.premiere.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiere.data.model.FilterParams
import com.example.premiere.data.remote.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilterViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FilterState())
    val state: StateFlow<FilterState> = _state.asStateFlow()

    // Shared filter state that MoviesListViewModel reads
    var pendingFilters: FilterParams = FilterParams()
        private set

    init {
        loadGenres()
    }

    fun onEvent(event: FilterEvent) {
        when (event) {
            is FilterEvent.QueryChanged -> _state.update { it.copy(query = event.query) }
            is FilterEvent.GenreSelected -> {
                val newId = if (_state.value.selectedGenreId == event.genreId) null else event.genreId
                _state.update { it.copy(selectedGenreId = newId) }
            }
            is FilterEvent.MinYearChanged -> _state.update { it.copy(minYear = event.year) }
            is FilterEvent.MaxYearChanged -> _state.update { it.copy(maxYear = event.year) }
            is FilterEvent.MinRatingChanged -> _state.update { it.copy(minRating = event.rating) }
            is FilterEvent.ClearAll -> {
                _state.update {
                    it.copy(query = "", selectedGenreId = null, minYear = "", maxYear = "", minRating = 0f)
                }
                pendingFilters = FilterParams() // DODATI OVO
            }
            is FilterEvent.ApplyFilters -> buildFilters()
        }
    }

    private fun buildFilters() {
        val s = _state.value
        pendingFilters = FilterParams(
            query = s.query,
            genreId = s.selectedGenreId,
            minYear = s.minYear.toIntOrNull(),
            maxYear = s.maxYear.toIntOrNull(),
            minRating = if (s.minRating > 0f) s.minRating else null
        )
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingGenres = true) }
            repository.getGenres()
                .onSuccess { genres ->
                    _state.update {
                        it.copy(genres = genres, isLoadingGenres = false)
                    }
                }
                .onFailure { error ->
                    android.util.Log.e("FilterVM", "Genre load failed: ${error.message}")
                    _state.update { it.copy(isLoadingGenres = false) }
                }
        }
    }
}