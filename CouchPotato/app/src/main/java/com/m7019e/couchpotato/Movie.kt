package com.m7019e.couchpotato

import com.m7019e.couchpotato.database.MovieEntity
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val adult: Boolean = false,
    val backdrop_path: String? = null,
    val genre_ids: List<Int> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String? = null,
    val popularity: Double,
    val poster_path: String? = null,
    val release_date: String,
    val title: String,
    val video: Boolean = false,
    val vote_average: Double,
    val vote_count: Int
) {
    fun withGenresFromIds(): Movie {
        val mappedGenres = genre_ids.map { id ->
            Genre(id, genreMap[id] ?: "Unknown")
        }
        return copy(genres = mappedGenres)
    }
}

@Serializable
data class MovieResponse(
    val results: List<Movie>,
    val page: Int? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null
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