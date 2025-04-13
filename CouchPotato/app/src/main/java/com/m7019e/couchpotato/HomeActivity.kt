package com.m7019e.couchpotato

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import coil.compose.AsyncImage
import com.m7019e.couchpotato.database.AppDatabase
import com.m7019e.couchpotato.database.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.m7019e.couchpotato.BuildConfig
import com.m7019e.couchpotato.database.MovieDAO
import kotlinx.coroutines.flow.first

private const val TAG = "CouchPotato"

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
    val network = connectivityManager?.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}


fun getDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "CouchPotatoDB"
    ).build()
}

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
        Log.i(TAG, "Fetched ${movies.size} popular movies")
        movies
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching popular movies: ${e.message}")
        emptyList()
    }
}

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
        Log.i(TAG, "Fetched ${movies.size} top-rated movies")
        movies
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching top-rated movies: ${e.message}")
        emptyList()
    }
}

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
        Log.d(TAG, "Fetched ${reviews.size} reviews for movie $movieId")
        reviews
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching reviews for movie $movieId: ${e.message}")
        emptyList()
    }
}

suspend fun fetchMovieVideos(movieId: Int): List<Video> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "https://api.themoviedb.org/3/movie/$movieId/videos?api_key=$apiKey&language=en-US"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return emptyList()
        val jsonObject = JSONObject(json)
        val results = jsonObject.getJSONArray("results")
        val videos = mutableListOf<Video>()

        for (i in 0 until results.length()) {
            val videoJson = results.getJSONObject(i)
            val site = videoJson.getString("site")
            val key = videoJson.getString("key")
            val videoUrl = when (site) {
                "YouTube" -> "https://www.youtube.com/watch?v=$key"
                "Vimeo" -> "https://vimeo.com/$key"
                else -> null
            }
            videoUrl?.let {
                videos.add(
                    Video(
                        id = videoJson.getString("id"),
                        name = videoJson.getString("name"),
                        site = site,
                        key = key,
                        url = it
                    )
                )
            }
        }
        Log.d(TAG, "Fetched ${videos.size} videos for movie $movieId")
        videos
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching videos for movie $movieId: ${e.message}")
        emptyList()
    }
}

suspend fun fetchFavoriteMovies(dao: MovieDAO): List<Movie> {
    return try {
        dao.getAllFavoriteMovies().first().map { entity ->
            entity.toMovie()
        }.also {
            Log.i(TAG, "Fetched ${it.size} favorite movies")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching favorite movies: ${e.message}")
        emptyList()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeActivity() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { getDatabase(context) }
    var popularMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var topRatedMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var favoriteMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("Popular Movies") }
    var cachedMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var viewType by remember { mutableStateOf("Popular Movies") }
    var isConnected by remember { mutableStateOf(isNetworkConnected(context)) }

    // check for network connection
    DisposableEffect(Unit) {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                scope.launch(Dispatchers.Main) {
                    isConnected = true
                    Log.i(TAG, "Network connected")
                }
            }

            override fun onLost(network: Network) {
                scope.launch(Dispatchers.Main) {
                    isConnected = false
                    Log.i(TAG, "Network disconnected")
                }
            }
        }
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        onDispose {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }

    // combine launch effect to include connectivity status
    LaunchedEffect(viewType, isConnected) {
        scope.launch(Dispatchers.IO) {
            when (viewType) {
                "Popular Movies" -> {
                    if (isConnected) {
                        cachedMovies = fetchPopularMovies()
                    } else {
                        cachedMovies = emptyList()
                    }
                }
                "Top Rated Movies" -> {
                    if (isConnected) {
                        cachedMovies = fetchTopRatedMovies()
                    } else {
                        cachedMovies = emptyList()
                    }
                }
                "Favorite Movies" -> {
                    cachedMovies = fetchFavoriteMovies(db.movieDao())
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "movie_list",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("movie_list") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Couch Potato",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                MovieListScreen(
                    movies = cachedMovies,
                    selectedCategory = viewType,
                    isConnected = isConnected,
                    onCategorySelected = { newCategory ->
                        viewType = newCategory
                    },
                    onMovieClick = { movie ->
                        navController.navigate("movie_detail/${movie.id}")
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
        composable("movie_detail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            val movie = cachedMovies.find { it.id == movieId }
            if (movie != null) {
                MovieDetailScreen(
                    movie = movie,
                    onBackClick = { navController.popBackStack() },
                    db = db,
//                    isConnected = isConnected
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    movies: List<Movie>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    isConnected: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(listOf("Popular Movies", "Top Rated Movies", "Favorite Movies")) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(50)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // movie Grid or no connection
        if (movies.isEmpty() && !isConnected && selectedCategory != "Favorite Movies") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_signal_wifi_connected_no_internet_4_24),
                    contentDescription = "No internet connection",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else if (movies.isEmpty() && selectedCategory == "Favorite Movies") {
            Text(
                text = "No favorite movies",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(movie: Movie, onBackClick: () -> Unit, db: AppDatabase) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var videos by remember { mutableStateOf<List<Video>>(emptyList()) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val threshold = with(density) { 400.dp.toPx() }
    val opacity = (scrollState.value / threshold).coerceIn(0f, 1f)
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(movie.id) {
        scope.launch(Dispatchers.IO) {
            reviews = fetchReviews(movie.id)
            videos = fetchMovieVideos(movie.id)
            isFavorite = db.movieDao().isFavorite(movie.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* null */ },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // White for contrast
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Log.d("FAVOURITES", "clicked")
                        scope.launch(Dispatchers.IO) {
                            if (isFavorite) {
                                db.movieDao().deleteMovie(movie.id)
                                Log.i(TAG, "Removed ${movie.title} from favorites")
                            } else {
                                db.movieDao().insertMovie(movie.toEntity())
                                Log.i(TAG, "Added ${movie.title} to favorites")
                            }
                            isFavorite = !isFavorite
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = opacity),
                    navigationIconContentColor = Color.White
                ),
                modifier = Modifier
                    .zIndex(1f)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // cover
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    AsyncImage(
                        model = movie.backdrop_path?.let { "https://image.tmdb.org/t/p/w780$it" }
                            ?: "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                        contentDescription = "${movie.title} cover",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    ),
                                    startY = 0.2f * 500f,
                                    endY = 900f
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .padding(top = paddingValues.calculateTopPadding())
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        movie.genres.forEach { genre ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = genre.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = movie.overview ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Release Date",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.release_date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${movie.vote_average}/10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (reviews.isEmpty()) {
                        Text(
                            text = "No reviews available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(reviews) { review ->
                                ReviewCard(review = review)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Trailers",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (videos.isEmpty()) {
                        Text(
                            text = "No trailers available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(videos) { video ->
                                VideoCard(video = video)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Review(
    val author: String,
    val content: String
)

data class Video(
    val id: String,
    val name: String,
    val site: String,
    val key: String,
    val url: String
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

fun Movie.toEntity() = MovieEntity(
    id = id,
    adult = adult,
    backdrop_path = backdrop_path,
    genres = genres,
    original_language = original_language,
    original_title = original_title,
    overview = overview,
    popularity = popularity,
    poster_path = poster_path,
    release_date = release_date,
    title = title,
    video = video,
    vote_average = vote_average,
    vote_count = vote_count
)

fun MovieEntity.toMovie() = Movie(
    adult = adult,
    backdrop_path = backdrop_path,
    genres = genres,
    id = id,
    original_language = original_language,
    original_title = original_title,
    overview = overview,
    popularity = popularity,
    poster_path = poster_path,
    release_date = release_date,
    title = title,
    video = video,
    vote_average = vote_average,
    vote_count = vote_count
)