package com.m7019e.couchpotato

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val author: String,
    val content: String
)