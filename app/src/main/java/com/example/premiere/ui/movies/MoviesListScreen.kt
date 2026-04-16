package com.example.premiere.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.premiere.data.model.Movie
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesListScreen(
    onMovieClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    viewModel: MoviesListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MoviesListEffect.NavigateToDetails -> onMovieClick(effect.movieId)
                is MoviesListEffect.NavigateToFilter -> onFilterClick()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premiere") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (state.activeFiltersCount > 0) {
                                Badge { Text(state.activeFiltersCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { viewModel.onEvent(MoviesListEvent.FilterClicked) }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SortBar(
                currentSort = state.sortBy,
                sortOrder = state.sortOrder,
                onSortChanged = { viewModel.onEvent(MoviesListEvent.SortChanged(it)) },
                onToggleSortOrder = { viewModel.onEvent(MoviesListEvent.ToggleSortOrder) }
            )

            Text(
                text = "${state.totalMovies} movies",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.onEvent(MoviesListEvent.LoadMovies) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                state.movies.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No movies found")
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        state.movies.forEach { movie ->
                            MovieItem(
                                movie = movie,
                                onClick = { viewModel.onEvent(MoviesListEvent.MovieClicked(movie.id)) }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SortBar(
    currentSort: String,
    sortOrder: String,
    onSortChanged: (String) -> Unit,
    onToggleSortOrder: () -> Unit
) {
    val sortOptions = listOf(
        "imdb_rating" to "Rating",
        "year" to "Year",
        "title" to "Title",
        "popularity" to "Popularity"
    )

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sortOptions.forEach { (value, label) ->
            FilterChip(
                selected = currentSort == value,
                onClick = { onSortChanged(value) },
                label = { Text(label) }
            )
        }

        // Asc/Desc toggle dugme
        FilledTonalIconButton(onClick = onToggleSortOrder) {
            Icon(
                imageVector = if (sortOrder == "desc")
                    Icons.Default.KeyboardArrowDown
                else
                    Icons.Default.KeyboardArrowUp,
                contentDescription = if (sortOrder == "desc") "Descending" else "Ascending"
            )
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = movie.posterPath?.let {
                    "https://image.tmdb.org/t/p/w185$it"
                },
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = buildString {
                        movie.year?.let { append(it) }
                        if (movie.year != null && movie.runtime != null) append(" • ")
                        movie.runtime?.let { append("${it}min") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = buildString {
                            movie.imdbRating?.let { append("%.1f".format(it)) }
                            movie.imdbVotes?.let { append(" (${formatVotes(it)})") }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    movie.genres.take(3).forEach { genre ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

fun formatVotes(votes: Int): String {
    return when {
        votes >= 1_000_000 -> "%.1fM".format(votes / 1_000_000.0)
        votes >= 1_000 -> "%.1fK".format(votes / 1_000.0)
        else -> votes.toString()
    }
}