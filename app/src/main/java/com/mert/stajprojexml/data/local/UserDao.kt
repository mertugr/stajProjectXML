package com.mert.stajprojexml.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: String): UserEntity?
}
