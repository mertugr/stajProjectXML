package com.mert.stajprojexml.data.repository

import android.content.Context
import com.mert.stajprojexml.data.local.AppDatabase
import com.mert.stajprojexml.data.local.FavoriteArticle
import com.mert.stajprojexml.data.local.SessionManager
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(context: Context) {
    private val dao = AppDatabase.get(context).favoriteDao()
    private val session = SessionManager(context)

    private fun userId(): String = session.currentUserId()

    fun favorites(): Flow<List<FavoriteArticle>> = dao.getAll(userId())

    fun isFavorite(url: String): Flow<Boolean> = dao.isFavorite(userId(), url)

    suspend fun add(article: FavoriteArticle) = dao.insert(article.copy(userId = userId()))

    suspend fun remove(url: String) = dao.deleteByUrl(userId(), url)
}
