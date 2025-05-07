package com.m7019e.couchpotato

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val author: String,
    val content: String
)

@Serializable
data class ReviewResponse(
    val results: List<Review>,
    val page: Int? = null,
    val total_pages: Int? = null,
    val total_results: Int? = null
)
