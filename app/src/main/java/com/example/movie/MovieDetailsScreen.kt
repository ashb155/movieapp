package com.example.movie

import YouTubeTrailerPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Spacer(modifier = Modifier.height(16.dp))

        when {
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                .background(MaterialTheme.colorScheme.primary.copy(alpha=0.2f))
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
                                text = "Please check your connection or try again later.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            movie != null -> {
                Text(
                    text = movie!!.title,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    movie!!.backdropPath?.let {
                        val imageUrl = "https://image.tmdb.org/t/p/w780$it"
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.75f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    movie!!.videos?.results
                        ?.firstOrNull { it.site == "YouTube" }?.key?.let { trailerKey ->
                            YouTubeTrailerPlayer(
                                trailerKey = trailerKey,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth(0.9f)
                                    .height(200.dp)
                            )
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier=Modifier.fillMaxWidth()){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                Text(
                    text = "Rating: ${movie!!.
                    voteAverage ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Release Date: ${movie!!.releaseDate ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )}}

                Text(
                    text = "Overview:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Text(
                    text = movie!!.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )

                if (cast.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Top Cast", style = MaterialTheme.typography.titleLarge)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Back", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            else -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
