package com.example.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel(), onMovieClick: (Int) -> Unit) {
    val movies = viewModel.movies
    val error = viewModel.error
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel.selectedGenreIds) {
        viewModel.fetchMoviesByGenres()
        listState.animateScrollToItem(0)

    }
    LaunchedEffect(searchQuery) {
        delay(500)
        if(searchQuery.isBlank()){
            viewModel.fetchMoviesByGenres()
        }else{
        viewModel.searchMovies(searchQuery)
    }}




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
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

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

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
            LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
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
                            viewModel.fetchMoviesByGenres()},
                        label = { Text(genre.name) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }




        LazyColumn(
            state=listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(movies) { movie ->
                MovieItem(movie = movie, onClick = { onMovieClick(movie.id) })
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
        ) {
            movie.posterPath?.let {
                val imageUrl = "https://image.tmdb.org/t/p/w500$it"
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "${movie.title} poster",
                    modifier = Modifier
                        .size(width = 100.dp, height = 150.dp)
                        .clip(MaterialTheme.shapes.small)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}

