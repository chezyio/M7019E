package com.m7019e.couchpotato

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val name: String,
    val site: String,
    val key: String
) {
    val url: String
        get() = when (site) {
            "YouTube" -> "https://www.youtube.com/watch?v=$key"
            "Vimeo" -> "https://vimeo.com/$key"
            else -> ""
        }
}

@Serializable
data class VideoResponse(
    val results: List<Video>,
    val id: Int? = null
)
