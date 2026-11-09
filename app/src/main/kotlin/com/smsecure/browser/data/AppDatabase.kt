package com.smsecure.browser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import timber.log.Timber

@Database(
    entities = [HistoryEntity::class, BookmarkEntity::class, SiteExceptionEntity::class, PasswordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun siteExceptionDao(): SiteExceptionDao
    abstract fun passwordDao(): PasswordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    // CRITICAL: Load SQLCipher native library before opening encrypted database
                    System.loadLibrary("sqlcipher")
                } catch (e: Throwable) {
                    Timber.e(e, "Error loading SQLCipher native library")
                }

                val passphrase = KeyStoreHelper.getPassphrase(context)
                val factory = SupportOpenHelperFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sm_secure_browser.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
