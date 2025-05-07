package com.m7019e.couchpotato

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