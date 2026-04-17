package com.example.premiere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.premiere.ui.Screen
import com.example.premiere.ui.moviedetails.MovieDetailsScreen
import com.example.premiere.ui.filter.FilterContract
import com.example.premiere.ui.filter.FilterScreen
import com.example.premiere.ui.filter.FilterViewModel
import com.example.premiere.ui.movielist.MoviesListScreen
import com.example.premiere.ui.movielist.MoviesListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.MoviesList.route
    ) {
        composable(Screen.MoviesList.route) {
            val viewModel = koinViewModel<MoviesListViewModel>()
            MoviesListScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                },
                onFilterClick = {
                    navController.navigate(Screen.Filter.route)
                }
            )
        }

        composable(Screen.Filter.route) {
            val filterViewModel = koinViewModel<FilterViewModel>()
            val moviesBackStack = navController.getBackStackEntry(Screen.MoviesList.route)
            val moviesViewModel = koinViewModel<MoviesListViewModel>(
                viewModelStoreOwner = moviesBackStack
            )

            LaunchedEffect(filterViewModel) {
                filterViewModel.effects.collect { effect ->
                    when (effect) {
                        is FilterContract.SideEffect.FiltersApplied -> {
                            moviesViewModel.applyFilters(effect.filters)
                            navController.popBackStack()
                        }
                    }
                }
            }

            FilterScreen(
                viewModel = filterViewModel,
                onBack = { navController.popBackStack() }
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