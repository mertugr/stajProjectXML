package com.mert.stajprojexml.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [FavoriteArticle::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // add userId column and composite PK by recreating table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS favorites_new(
                        userId TEXT NOT NULL,
                        url TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        imageUrl TEXT,
                        content TEXT,
                        PRIMARY KEY(userId, url)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO favorites_new(userId, url, title, description, imageUrl, content)
                    SELECT 'guest', url, title, description, imageUrl, content FROM favorites
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE favorites")
                db.execSQL("ALTER TABLE favorites_new RENAME TO favorites")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS users(
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT,
                        email TEXT
                    )
                    """.trimIndent()
                )
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news-db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
