package com.example.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.*
import kotlinx.coroutines.delay
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel(), onMovieClick: (Int) -> Unit) {
    val movies by viewModel.movies.collectAsState()
    val error by viewModel.error.collectAsState()
    val repoSearchQuery by viewModel.lastQuery.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf(repoSearchQuery) }
    val listState = rememberLazyGridState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val genres by viewModel.genres.collectAsState()
    val selectedGenreIds by viewModel.selectedGenreIds.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()


    LaunchedEffect(viewModel.selectedGenreIds) {
        viewModel.fetchMoviesByGenres()
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(searchQuery) {
        delay(500)
        if (searchQuery != repoSearchQuery) {
            if (searchQuery.isBlank()) {
                viewModel.fetchMovies()
            } else {
                viewModel.searchMovies(searchQuery)
            }
        }
    }

    LaunchedEffect(currentPage) {
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
                        error?.let{
                        Text(
                            text = it,
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
        } }else {
            AnimatedFadeInLogo()
            Spacer(Modifier.padding(2.dp))


            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(Unit) {
                viewModel.fetchGenres()
            }

            if (genres.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    strokeWidth = 2.dp
                )
            } else {

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
                LazyRow(modifier = Modifier.padding(vertical = 10.dp)) {
                    item {
                        FilterChip(
                            selected = selectedGenreIds.isEmpty(),
                            onClick = { viewModel.fetchMoviesByGenres() },
                            label = { Text("All") },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    items(genres) { genre ->
                        FilterChip(
                            selected = selectedGenreIds.contains(genre.id),
                            onClick = {
                                viewModel.toggleGenreSelection(genre.id)
                            },
                            label = { Text(genre.name) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            if (selectedGenreIds.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.clearSelectedGenres()
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
            if (selectedGenreIds.isNotEmpty() && movies.isEmpty()){
                Box(
                    modifier=Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ){

                    Text(
                        text = "No movies found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refreshMovies() },
                modifier = Modifier.weight(1f)
            ) { AnimatedVisibility(visible=genres.isNotEmpty() && movies.isNotEmpty(),
                enter=fadeIn(animationSpec=tween(durationMillis=600))) {
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
                    items(movies.take(18)) { movie ->
                        MovieItem(
                            movie = movie,
                            viewModel = viewModel,
                            onClick = { onMovieClick(movie.id) } )
                    }
                }
            }}

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = listState.firstVisibleItemIndex > 5,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 600))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.loadPreviousPage() },
                        enabled = currentPage > 1,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        Text("Previous")
                    }

                    Text(
                        text = "Page ${currentPage} of ${totalPages}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Button(
                        onClick = { viewModel.loadNextPage() },
                        enabled = currentPage < totalPages,
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    ) {
        Column {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.releaseDate?.take(4) ?: "NA",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AnimatedFadeInLogo() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1200)
    )

    Image(
        painter = painterResource(id = R.drawable.flixist),
        contentDescription = "App Logo",
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .height(130.dp),
        contentScale = ContentScale.Fit
    )
}