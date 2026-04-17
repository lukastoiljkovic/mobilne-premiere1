package com.example.premiere.ui.moviedetails

import com.example.premiere.data.model.CastMember
import com.example.premiere.data.model.Movie
import com.example.premiere.data.model.MovieImage

interface MovieDetailsContract {

    data class UiState(
        val movie: Movie? = null,
        val cast: List<CastMember> = emptyList(),
        val backdropImages: List<MovieImage> = emptyList(),
        val trailerKey: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class UiEvent {
        object Retry : UiEvent()
    }
}