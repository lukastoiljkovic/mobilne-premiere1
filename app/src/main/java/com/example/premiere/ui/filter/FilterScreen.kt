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
    onApplyFilters: () -> Unit,
    onBack: () -> Unit,
    viewModel: FilterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

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
            // Search
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onEvent(FilterEvent.QueryChanged(it)) },
                label = { Text("Search by title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Genre
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Genre", style = MaterialTheme.typography.titleSmall)
                when {
                    state.isLoadingGenres -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else -> {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.genres.forEach { genre ->
                                FilterChip(
                                    selected = state.selectedGenreId == genre.id,
                                    onClick = { viewModel.onEvent(FilterEvent.GenreSelected(genre.id)) },
                                    label = { Text(genre.name) }
                                )
                            }
                        }
                    }
                }
            }

            // Year range
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Year range", style = MaterialTheme.typography.titleSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.minYear,
                        onValueChange = { viewModel.onEvent(FilterEvent.MinYearChanged(it)) },
                        label = { Text("From") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.maxYear,
                        onValueChange = { viewModel.onEvent(FilterEvent.MaxYearChanged(it)) },
                        label = { Text("To") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Min rating
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
                    onValueChange = { viewModel.onEvent(FilterEvent.MinRatingChanged(it)) },
                    valueRange = 0f..10f,
                    steps = 19
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.onEvent(FilterEvent.ClearAll) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = {
                        viewModel.onEvent(FilterEvent.ApplyFilters)
                        onApplyFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}