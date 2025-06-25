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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error


@Composable
fun MovieDetailsScreen(
    movieId: Int,
    viewModel: MovieViewModel = viewModel(),
    onBack: () -> Unit
) {
    val movie by viewModel.selectedMovie.collectAsState()
    val cast by remember { derivedStateOf { viewModel.cast } }
    val errorMessageState=viewModel.error.collectAsState()
    val errorMessage=errorMessageState.value
    val scrollState = rememberScrollState()

    LaunchedEffect(movieId) {
        viewModel.fetchMovieDetails(movieId)
    }

    Box(modifier = Modifier.fillMaxSize()
        ) {

        if (errorMessage != null) {
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
                        errorMessage?.let{
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.fetchMovieDetails(movieId) },
                            modifier=Modifier.fillMaxWidth(),
                            colors=ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha=0.4f)
                            )) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }}

    else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                if (movie != null) {
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
                    movie?.let{nonNullMovie->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) { val imageUrl = viewModel.getBackdropUrl(nonNullMovie.backdropPath)
                        imageUrl?.let { url ->
                            Image(
                                painter = rememberAsyncImagePainter(url),
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

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
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
                        }
                    }
                    Spacer(modifier=Modifier.height(10.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Overview",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = movie!!.overview,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 16.sp),
                            textAlign = TextAlign.Justify,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    if (cast.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = MaterialTheme.shapes.medium
                        ){
                        Text(
                            "Top Cast",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color=MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .padding(top = 24.dp, bottom = 12.dp)
                                .fillMaxWidth()
                        )

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
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            strokeWidth = 2.dp
                        )
                    }
            }
        }}

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.backarrow),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}}
