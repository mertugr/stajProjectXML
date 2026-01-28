package com.mert.stajprojexml.data.remote.dto

/**
 * NewsAPI returns either a success payload (status=ok, totalResults, articles)
 * or an error payload (status=error, code, message). All fields are nullable to
 * tolerate error bodies without crashing deserialization.
 */
data class NewsResponseDto(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleDto>?,
    val code: String? = null,
    val message: String? = null
)
