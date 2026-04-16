package com.example.premiere.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.premiere.ui.details.MovieDetailsScreen
import com.example.premiere.ui.filter.FilterScreen
import com.example.premiere.ui.filter.FilterViewModel
import com.example.premiere.ui.movies.MoviesListScreen
import com.example.premiere.ui.movies.MoviesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // Shared ViewModels scoped to the NavGraph
    val moviesViewModel: MoviesListViewModel = koinViewModel()
    val filterViewModel: FilterViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.MoviesList.route
    ) {
        composable(Screen.MoviesList.route) {
            MoviesListScreen(
                viewModel = moviesViewModel,
                onMovieClick = { movieId: String -> navController.navigate(Screen.MovieDetails.createRoute(movieId)) },
                onFilterClick = {
                    navController.navigate(Screen.Filter.route)
                }
            )
        }

        composable(Screen.Filter.route) {
            FilterScreen(
                viewModel = filterViewModel,
                onApplyFilters = {
                    // Pass filters back to movies list
                    moviesViewModel.applyFilters(filterViewModel.pendingFilters)
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: return@composable
            MovieDetailsScreen(
                movieId = movieId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}