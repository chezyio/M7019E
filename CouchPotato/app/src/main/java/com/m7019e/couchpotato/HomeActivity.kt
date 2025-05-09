package com.m7019e.couchpotato

import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.work.*
import com.m7019e.couchpotato.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

const val TAG = "CouchPotato"

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActivity() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { getDatabase(context) }
    var selectedCategory by remember { mutableStateOf("Popular Movies") }
    var cachedMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var viewType by remember { mutableStateOf("Popular Movies") }
    var cachedViewType by remember { mutableStateOf("Popular Movies") }
    var isConnected by remember { mutableStateOf(isNetworkConnected(context)) }

    // scheduling worker to execute MovieFetchWorker periodically
    LaunchedEffect(Unit) {
        val workRequest = PeriodicWorkRequestBuilder<MovieFetchWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "movie_fetch_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    // check for network connection
    // side effect that runs when the associated composable enters the composition
    DisposableEffect(Unit) {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            // triggered when the device gains internet connectivity
            override fun onAvailable(network: Network) {
                scope.launch(Dispatchers.Main) {
                    isConnected = true
                    Log.i(TAG, "Network connected")
                }
            }

            // triggered when the device loses internet connectivity
            override fun onLost(network: Network) {
                scope.launch(Dispatchers.Main) {
                    isConnected = false
                    Log.i(TAG, "Network disconnected")
                }
            }
        }

        // ensures only networks with internet access are tracked
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // callback is registered to the ConnectivityManager to start monitoring the network
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        onDispose {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }

    // combine launch effect to fetch movies when viewType changes or internet reconnects
    LaunchedEffect(viewType, isConnected) {
        scope.launch(Dispatchers.IO) {
            // clear cache when viewType changes
            if (viewType != cachedViewType) {
                cachedMovies = emptyList()
                cachedViewType = viewType
            }
            // fetch based on viewType
            when (viewType) {
                "Popular Movies" -> {
                    //checking if database has list of movies and displaying it
                    val cached = db.movieDao().getAllPopularMovies().map { it.toMovie() }
                    cachedMovies = if (cached.isNotEmpty()) {
                        cached
                    } else if (isConnected) {
                        //fetch from internet and updating database if database empty & internet available
                        fetchPopularMovies().also {
                            db.movieDao().insertPopularMovies(it.map { movie -> movie.toPopularEntity() })
                            cachedMovies = it
                        }
                    } else emptyList()
                }

                "Top Rated Movies" -> {
                    val cached = db.movieDao().getAllTopRatedMovies().map { it.toMovie() }
                    cachedMovies = if (cached.isNotEmpty()) {
                        cached
                    } else if (isConnected) {
                        fetchTopRatedMovies().also {
                            db.movieDao().insertTopRatedMovies(it.map { movie -> movie.toTopRatedEntity() })
                            cachedMovies = it
                        }
                    } else emptyList()
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
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                MovieListScreen(
                    movies = cachedMovies,
                    selectedCategory = viewType,
                    cachedViewType = cachedViewType,
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
                    isConnected = isConnected
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

//Worker to fetch popular & top rated movies and updating database
class MovieFetchWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = getDatabase(applicationContext)
        val dao = db.movieDao()

        return try {
            if (isNetworkConnected(applicationContext)) {
                val popular = fetchPopularMovies()
                val topRated = fetchTopRatedMovies()

                dao.insertPopularMovies(popular.map { it.toPopularEntity() })
                dao.insertTopRatedMovies(topRated.map { it.toTopRatedEntity() })

                Log.i(TAG, "Background fetch successful")
                Result.success()
            } else {
                Log.i(TAG, "No internet connection, skipping fetch")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed", e)
            Result.retry()
        }
    }
}


