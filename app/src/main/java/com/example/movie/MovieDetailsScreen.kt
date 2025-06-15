package com.example.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val movie = viewModel.getMovieById(movieId)
    val cast = viewModel.cast

    LaunchedEffect(movieId) {
        viewModel.fetchMovieCredits(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (movie != null) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            movie.posterPath?.let {
                val imageUrl = "https://image.tmdb.org/t/p/w500$it"
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "${movie.title} poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp)
                )
            }

            Text(
                text = "Rating: ${movie.voteAverage}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Release Date: ${movie.releaseDate ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Overview:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )

            Text(
                text = movie.overview,
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
        } else {
            Text(
                text = "Movie details not available.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
