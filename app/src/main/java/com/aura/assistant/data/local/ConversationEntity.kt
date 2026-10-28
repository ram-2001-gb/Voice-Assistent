package com.aura.assistant.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_query") val userQuery: String,
    @ColumnInfo(name = "assistant_response") val assistantResponse: String,
    @ColumnInfo(name = "language") val language: String,
    @ColumnInfo(name = "is_offline_executed") val isOfflineExecuted: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
