package com.example.premiere.ui.moviedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.premiere.data.api.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieId: String,
    private val repository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsContract.UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: MovieDetailsContract.UiState.() -> MovieDetailsContract.UiState) {
        _state.getAndUpdate(reducer)
    }

    init {
        loadDetails()
    }

    fun setEvent(event: MovieDetailsContract.UiEvent) {
        when (event) {
            is MovieDetailsContract.UiEvent.Retry -> loadDetails()
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val movieDeferred = async { repository.getMovieDetails(movieId) }
            val castDeferred = async { repository.getCast(movieId) }
            val imagesDeferred = async { repository.getImages(movieId) }
            val videosDeferred = async { repository.getVideos(movieId) }

            val movieResult = movieDeferred.await()
            if (movieResult.isFailure) {
                setState {
                    copy(isLoading = false, error = movieResult.exceptionOrNull()?.message ?: "Error")
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

            setState {
                copy(
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