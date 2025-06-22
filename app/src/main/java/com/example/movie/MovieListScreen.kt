package com.example.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.grid.*

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel(), onMovieClick: (Int) -> Unit) {
    val movies = viewModel.movies
    val error = viewModel.error
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyGridState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(viewModel.selectedGenreIds) {
        viewModel.fetchMoviesByGenres()
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(searchQuery) {
        delay(500)
        if (searchQuery.isBlank()) {
            viewModel.fetchMoviesByGenres()
        } else {
            viewModel.searchMovies(searchQuery)
        }
    }

    LaunchedEffect(viewModel.currentPage) {
        listState.animateScrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error Icon",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.padding(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.fetchMovies() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.netflix),
                contentDescription = "App Logo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(40.dp)
            )
            Spacer(Modifier.padding(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search movies...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(Unit) {
                viewModel.fetchGenres()
            }

            if (viewModel.genres.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    strokeWidth = 2.dp
                )
            } else {
                LazyRow(modifier = Modifier.padding(vertical = 10.dp)) {
                    item {
                        FilterChip(
                            selected = viewModel.selectedGenreIds.isEmpty(),
                            onClick = { viewModel.fetchMoviesByGenres() },
                            label = { Text("All") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    items(viewModel.genres) { genre ->
                        FilterChip(
                            selected = viewModel.selectedGenreIds.contains(genre.id),
                            onClick = {
                                viewModel.toggleGenreSelection(genre.id)
                                viewModel.fetchMoviesByGenres()
                            },
                            label = { Text(genre.name) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            if (viewModel.selectedGenreIds.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.clearSelectedGenres()
                        viewModel.fetchMoviesByGenres()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 2.dp)
                        .size(width = 100.dp, height = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                ) {
                    Text("Clear")
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refreshMovies() },
                modifier=Modifier.weight(1f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(movies) { movie ->
                        MovieItem(
                            movie = movie,
                            viewModel = viewModel,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (listState.firstVisibleItemIndex > 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.loadPreviousPage() },
                        enabled = viewModel.currentPage > 1,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Text("Previous")
                    }

                    Text(
                        text = "Page ${viewModel.currentPage} of ${viewModel.totalPages}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Button(
                        onClick = { viewModel.loadNextPage() },
                        enabled = viewModel.currentPage < viewModel.totalPages,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, viewModel: MovieViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(8f / 19f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.8f)
        )
    ) { Column{
        val imageUrl = viewModel.getImageUrl(movie.posterPath)
        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "${movie.title} poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            )
        }
        Column(
            modifier=Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ){
            Text(
                text=movie.title,
                style=MaterialTheme.typography.bodyMedium,
                maxLines=3,
                overflow=TextOverflow.Ellipsis
            )

            Text(
                text=movie.releaseDate?.take(4)?:"NA",
                style=MaterialTheme.typography.bodySmall
            )
        }
    }
    }}
