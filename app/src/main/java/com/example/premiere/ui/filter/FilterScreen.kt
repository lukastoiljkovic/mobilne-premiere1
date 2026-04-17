package com.example.premiere.ui.filter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.premiere.data.model.FilterParams
import com.example.premiere.data.model.Genre
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    FilterScreen(
        state = state,
        eventPublisher = viewModel::setEvent,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterScreen(
    state: FilterContract.UiState,
    eventPublisher: (FilterContract.UiEvent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Movies") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { eventPublisher(FilterContract.UiEvent.QueryChanged(it)) },
                label = { Text("Search by title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Genre", style = MaterialTheme.typography.titleSmall)
                when {
                    state.isLoadingGenres -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    state.genres.isEmpty() -> Text(
                        "Could not load genres",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.genres.forEach { genre ->
                                FilterChip(
                                    selected = state.selectedGenreId == genre.id,
                                    onClick = { eventPublisher(FilterContract.UiEvent.GenreSelected(genre.id)) },
                                    label = { Text(genre.name) }
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Year range", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.minYear,
                        onValueChange = { eventPublisher(FilterContract.UiEvent.MinYearChanged(it)) },
                        label = { Text("From") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.maxYear,
                        onValueChange = { eventPublisher(FilterContract.UiEvent.MaxYearChanged(it)) },
                        label = { Text("To") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Minimum rating", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = if (state.minRating > 0f) "%.1f".format(state.minRating) else "Any",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = state.minRating,
                    onValueChange = { eventPublisher(FilterContract.UiEvent.MinRatingChanged(it)) },
                    valueRange = 0f..10f,
                    steps = 19
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { eventPublisher(FilterContract.UiEvent.ClearAll) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear all")
                }
                Button(
                    onClick = {
                        eventPublisher(FilterContract.UiEvent.ApplyFilters)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply filters")
                }
            }
        }
    }
}