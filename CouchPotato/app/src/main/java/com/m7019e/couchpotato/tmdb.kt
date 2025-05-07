package com.m7019e.couchpotato

import android.util.Log
import com.m7019e.couchpotato.database.MovieDAO
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

private val json = Json { ignoreUnknownKeys = true }


// fetch 20 popular movies
suspend fun fetchPopularMovies(): List<Movie> {
    val apiKey = BuildConfig.TMDB_KEY
    if (apiKey.isEmpty()) {
        Log.e(TAG, "TMDB API key is empty or missing")
        return emptyList()
    }
    val url = "${TMDB_BASE_URL}movie/popular?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.e(TAG, "API call failed with code ${response.code}: ${response.message}")
            return emptyList()
        }
        val jsonString = response.body?.string() ?: run {
            Log.e(TAG, "Empty response body")
            return emptyList()
        }
        Log.d(TAG, "Raw JSON response: $jsonString")
        val movieResponse = json.decodeFromString<MovieResponse>(jsonString)
        val movies = movieResponse.results.take(20).map { it.withGenresFromIds() }
        Log.i(TAG, "Fetched ${movies.size} popular movies")
        movies
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching popular movies: ${e.message}", e)
        emptyList()
    }
}

// fetch 20 top-rated movies
suspend fun fetchTopRatedMovies(): List<Movie> {
    val apiKey = BuildConfig.TMDB_KEY
    if (apiKey.isEmpty()) {
        Log.e(TAG, "TMDB API key is empty or missing")
        return emptyList()
    }
    val url = "${TMDB_BASE_URL}movie/top_rated?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.e(TAG, "API call failed with code ${response.code}: ${response.message}")
            return emptyList()
        }
        val jsonString = response.body?.string() ?: run {
            Log.e(TAG, "Empty response body")
            return emptyList()
        }
        Log.d(TAG, "Raw JSON response: $jsonString")
        val movieResponse = json.decodeFromString<MovieResponse>(jsonString)
        val movies = movieResponse.results.take(20).map { it.withGenresFromIds() }
        Log.i(TAG, "Fetched ${movies.size} top-rated movies")
        movies
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching top-rated movies: ${e.message}", e)
        emptyList()
    }
}

// fetch reviews for a specific movie
suspend fun fetchReviews(movieId: Int): List<Review> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "${TMDB_BASE_URL}movie/$movieId/reviews?api_key=$apiKey&language=en-US&page=1"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val jsonString = response.body?.string() ?: run {
            Log.e(TAG, "Empty response body")
            return emptyList()
        }
        Log.d(TAG, "Raw JSON response for reviews: $jsonString")
        val reviewResponse = json.decodeFromString<ReviewResponse>(jsonString)
        val reviews = reviewResponse.results
        Log.d(TAG, "Fetched ${reviews.size} reviews for movie $movieId")
        reviews
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching reviews for movie $movieId: ${e.message}", e)
        emptyList()
    }
}

// fetch videos for a specific movie
suspend fun fetchMovieVideos(movieId: Int): List<Video> {
    val apiKey = BuildConfig.TMDB_KEY
    val url = "${TMDB_BASE_URL}movie/$movieId/videos?api_key=$apiKey&language=en-US"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return try {
        val response = client.newCall(request).execute()
        val jsonString = response.body?.string() ?: run {
            Log.e(TAG, "Empty response body")
            return emptyList()
        }
        Log.d(TAG, "Raw JSON response for videos: $jsonString")
        val videoResponse = json.decodeFromString<VideoResponse>(jsonString)
        val videos = videoResponse.results.filter { it.site == "YouTube" || it.site == "Vimeo" }
        Log.d(TAG, "Fetched ${videos.size} videos for movie $movieId")
        videos
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching videos for movie $movieId: ${e.message}", e)
        emptyList()
    }
}

// fetch favorite movies from local database
suspend fun fetchFavoriteMovies(dao: MovieDAO): List<Movie> {
    return try {
        dao.getAllFavoriteMovies().first().map { entity ->
            entity.toMovie()
        }.also {
            Log.i(TAG, "Fetched ${it.size} favorite movies")
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching favorite movies: ${e.message}", e)
        emptyList()
    }
}