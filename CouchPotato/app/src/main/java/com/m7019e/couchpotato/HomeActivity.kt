package com.m7019e.couchpotato

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

// GET 20 popular movies from tmdb
suspend fun fetchPopularMovies(): List<Movie> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "https://api.themoviedb.org/3/movie/popular?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return emptyList()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")
        val movies = mutableListOf<Movie>()

        for (i in 0 until minOf(results.length(), 20)) {
            val movieJson = results.getJSONObject(i)
            val genres = mutableListOf<Genre>()
            val genreIds = movieJson.getJSONArray("genre_ids")
            for (j in 0 until genreIds.length()) {
                val genreId = genreIds.getInt(j)
                val genreName = genreMap[genreId] ?: "Unknown"
                genres.add(Genre(genreId, genreName))
            }
            movies.add(
                Movie(
                    adult = movieJson.optBoolean("adult", false),
                    backdrop_path = movieJson.optString("backdrop_path", null),
                    genres = genres,
                    id = movieJson.getInt("id"),
                    original_language = movieJson.getString("original_language"),
                    original_title = movieJson.getString("original_title"),
                    overview = movieJson.optString("overview", null),
                    popularity = movieJson.getDouble("popularity"),
                    poster_path = movieJson.optString("poster_path", null),
                    release_date = movieJson.getString("release_date"),
                    title = movieJson.getString("title"),
                    video = movieJson.optBoolean("video", false),
                    vote_average = movieJson.getDouble("vote_average"),
                    vote_count = movieJson.getInt("vote_count")
                )
            )
        }
        movies
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// GET 20 top rated movies from tmdb
suspend fun fetchTopRatedMovies(): List<Movie> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "https://api.themoviedb.org/3/movie/top_rated?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return emptyList()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")
        val movies = mutableListOf<Movie>()

        for (i in 0 until minOf(results.length(), 20)) {
            val movieJson = results.getJSONObject(i)
            val genres = mutableListOf<Genre>()
            val genreIds = movieJson.getJSONArray("genre_ids")
            for (j in 0 until genreIds.length()) {
                val genreId = genreIds.getInt(j)
                val genreName = genreMap[genreId] ?: "Unknown"
                genres.add(Genre(genreId, genreName))
            }
            movies.add(
                Movie(
                    adult = movieJson.optBoolean("adult", false),
                    backdrop_path = movieJson.optString("backdrop_path", null),
                    genres = genres,
                    id = movieJson.getInt("id"),
                    original_language = movieJson.getString("original_language"),
                    original_title = movieJson.getString("original_title"),
                    overview = movieJson.optString("overview", null),
                    popularity = movieJson.getDouble("popularity"),
                    poster_path = movieJson.optString("poster_path", null),
                    release_date = movieJson.getString("release_date"),
                    title = movieJson.getString("title"),
                    video = movieJson.optBoolean("video", false),
                    vote_average = movieJson.getDouble("vote_average"),
                    vote_count = movieJson.getInt("vote_count")
                )
            )
        }
        movies
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
// GET reviews for movie from tmdb
suspend fun fetchReviews(movieId: Int): List<Review> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "https://api.themoviedb.org/3/movie/$movieId/reviews?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return emptyList()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")
        val reviews = mutableListOf<Review>()

        for (i in 0 until results.length()) {
            val reviewJson = results.getJSONObject(i)
            reviews.add(
                Review(
                    author = reviewJson.getString("author"),
                    content = reviewJson.getString("content")
                )
            )
        }
        Log.d("TMDB — GET REVIEWS", reviews.toString())
        reviews
    } catch (e: Exception) {
        emptyList()
    }
}


@Composable
fun HomeActivity() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var popularMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var topRatedMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            popularMovies = fetchPopularMovies()
            topRatedMovies = fetchTopRatedMovies()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "movie_list",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("movie_list") {
            MovieListScreen(
                popularMovies = popularMovies,
                topRatedMovies = topRatedMovies,
                onMovieClick = { movie ->
                    navController.navigate("movie_detail/${movie.id}")
                }
            )
        }
        composable("movie_detail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            val allMovies = popularMovies + topRatedMovies
            val movie = allMovies.find { it.id == movieId }
            if (movie != null) {
                MovieDetailScreen(
                    movie = movie,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Text(
                    text = "Movie not found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun MovieListScreen(
    popularMovies: List<Movie>,
    topRatedMovies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (popularMovies.isEmpty() && topRatedMovies.isEmpty()) {
            Text(
                text = "Loading movies...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Popular Movies Section
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Popular Movies",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(popularMovies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                }

                // Top Rated Movies Section
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Top Rated Movies",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(topRatedMovies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                }
            }
        }
    }
}
@Composable
fun MovieList(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie) })
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
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
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    movie.genres.forEach { genre ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text(
                                text = genre.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(movie: Movie, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }

    LaunchedEffect(movie.id) {
        scope.launch(Dispatchers.IO) {
            reviews = fetchReviews(movie.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Genres: ${movie.genres.joinToString { it.name }}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Overview: ${movie.overview ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Release Date: ${movie.release_date}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rating: ${movie.vote_average}/10",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .width(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


data class Review(
    val author: String,
    val content: String
)


val genreMap = mapOf(
    28 to "Action",
    12 to "Adventure",
    16 to "Animation",
    35 to "Comedy",
    80 to "Crime",
    99 to "Documentary",
    18 to "Drama",
    10751 to "Family",
    14 to "Fantasy",
    36 to "History",
    27 to "Horror",
    10402 to "Music",
    9648 to "Mystery",
    10749 to "Romance",
    878 to "Science Fiction",
    10770 to "TV Movie",
    53 to "Thriller",
    10752 to "War",
    37 to "Western"
)