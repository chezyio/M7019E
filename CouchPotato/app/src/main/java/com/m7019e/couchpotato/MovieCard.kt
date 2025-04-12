package com.m7019e.couchpotato

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                contentDescription = "${movie.title} poster",
                modifier = Modifier
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = movie.title,
//                    style = MaterialTheme.typography.titleMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 2,
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    movie.genres.forEach { genre ->
//                        Surface(
//                            shape = MaterialTheme.shapes.small,
//                            color = MaterialTheme.colorScheme.secondaryContainer,
//                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//                        ) {
//                            Text(
//                                text = genre.name,
//                                style = MaterialTheme.typography.labelLarge,
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
//                    }
//                }
        }
    }
}