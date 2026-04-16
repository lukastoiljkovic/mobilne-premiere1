package com.example.premiere.navigation

sealed class Screen(val route: String) {
    object MoviesList : Screen("movies_list")
    object Filter : Screen("filter")
    object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: String) = "movie_details/$movieId"
    }
}