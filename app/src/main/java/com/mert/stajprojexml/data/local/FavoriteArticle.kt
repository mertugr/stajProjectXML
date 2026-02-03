package com.mert.stajprojexml.data.local

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "url"]
)
data class FavoriteArticle(
    val userId: String,
    val url: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val content: String?
)
