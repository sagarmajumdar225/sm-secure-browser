package com.smsecure.browser.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_exceptions")
data class SiteExceptionEntity(
    @PrimaryKey val domain: String,
    val allowCookies: Boolean = false,
    val allowStorage: Boolean = false,
    val allowScripts: Boolean = true
)
