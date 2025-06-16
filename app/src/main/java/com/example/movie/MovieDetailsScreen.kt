package com.example.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieViewModel = viewModel(),
    onBack: () -> Unit
) {
    val movie by remember { derivedStateOf { viewModel.selectedMovie } }
    val cast by remember { derivedStateOf { viewModel.cast } }
    val error by remember { derivedStateOf { viewModel.error } }

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back", color = MaterialTheme.colorScheme.tertiary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            error != null -> {
                Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            }
            movie != null -> {
                Text(
                    text = movie!!.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                movie!!.backdropPath?.let {
                    val imageUrl = "https://image.tmdb.org/t/p/w780$it"
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                }

                Text(
                    text = "Rating: ${movie!!.voteAverage ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Release Date: ${movie!!.releaseDate ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Overview:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )

                Text(
                    text = movie!!.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )

                if (cast.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Top Cast", style = MaterialTheme.typography.titleMedium)
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(cast.take(10)) { actor ->
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                actor.profilePath?.let { path ->
                                    val imageUrl = "https://image.tmdb.org/t/p/w200$path"
                                    Image(
                                        painter = rememberAsyncImagePainter(imageUrl),
                                        contentDescription = actor.name,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    text = actor.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                Text(
                    text = "Loading movie details...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
