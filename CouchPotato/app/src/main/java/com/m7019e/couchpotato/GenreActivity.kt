package com.m7019e.couchpotato
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Genre(
    val id: Int,
    val name: String
)

data class MovieDetails(
    val id: Int,
    val title: String,
    val genres: List<Genre>
)

// Hardcoded movie database
val movieDatabase = listOf(
    MovieDetails(
        id = 1,
        title = "Inception",
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Sci-Fi"),
            Genre(id = 3, name = "Thriller")
        )
    ),
    MovieDetails(
        id = 2,
        title = "The Dark Knight",
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 4, name = "Crime"),
            Genre(id = 5, name = "Drama")
        )
    ),
    MovieDetails(
        id = 3,
        title = "Interstellar",
        genres = listOf(
            Genre(id = 2, name = "Sci-Fi"),
            Genre(id = 5, name = "Drama"),
            Genre(id = 6, name = "Adventure")
        )
    ),
    MovieDetails(
        id = 4,
        title = "Parasite",
        genres = listOf(
            Genre(id = 5, name = "Drama"),
            Genre(id = 7, name = "Thriller"),
            Genre(id = 8, name = "Comedy")
        )
    ),
    MovieDetails(
        id = 5,
        title = "The Matrix",
        genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Sci-Fi"),
            Genre(id = 7, name = "Thriller")
        )
    )
)


@Composable
fun MovieGenresFromDatabase(movieId: Int) {
    // Find the movie in the hardcoded database
    val movie = movieDatabase.find { it.id == movieId }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (movie == null) {
            Text(
                text = "Movie not found",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Genres:",
                style = MaterialTheme.typography.titleLarge
            )
            movie.genres.forEach { genre ->
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MovieGenresFromDatabasePreview() {
    MovieGenresFromDatabase(movieId = 1)
}