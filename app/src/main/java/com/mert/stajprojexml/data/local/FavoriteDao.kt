package com.mert.stajprojexml.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getAll(userId: String): Flow<List<FavoriteArticle>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND url = :url)")
    fun isFavorite(userId: String, url: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: FavoriteArticle)

    @Query("DELETE FROM favorites WHERE userId = :userId AND url = :url")
    suspend fun deleteByUrl(userId: String, url: String)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAll(userId: String)
}
