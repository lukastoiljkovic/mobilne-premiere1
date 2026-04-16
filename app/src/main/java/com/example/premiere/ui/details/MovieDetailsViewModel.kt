package com.example.premiere.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiere.data.remote.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieId: String,
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state.asStateFlow()

    init {
        loadDetails()
    }

    fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.Retry -> loadDetails()
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val movieDeferred = async { repository.getMovieDetails(movieId) }
            val castDeferred = async { repository.getCast(movieId) }
            val imagesDeferred = async { repository.getImages(movieId) }
            val videosDeferred = async { repository.getVideos(movieId) }

            val movieResult = movieDeferred.await()
            if (movieResult.isFailure) {
                _state.update {
                    it.copy(isLoading = false, error = movieResult.exceptionOrNull()?.message ?: "Error")
                }
                return@launch
            }

            val castResult = castDeferred.await()
            val imagesResult = imagesDeferred.await()
            val videosResult = videosDeferred.await()
            val videos = videosResult.getOrNull() ?: emptyList()

            val trailer = (
                    videos.firstOrNull { it.site?.lowercase() == "youtube" && it.type?.lowercase() == "trailer" }
                        ?: videos.firstOrNull { it.site?.lowercase() == "youtube" && it.type?.lowercase() == "featurette" }
                        ?: videos.firstOrNull { it.site?.lowercase() == "youtube" }
                    )?.key

            _state.update {
                it.copy(
                    movie = movieResult.getOrNull(),
                    cast = castResult.getOrNull()?.items ?: emptyList(),
                    backdropImages = imagesResult.getOrNull()?.backdrops ?: emptyList(),
                    trailerKey = trailer,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}