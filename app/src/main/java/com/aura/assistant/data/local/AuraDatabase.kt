package com.aura.assistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ConversationEntity::class], version = 1, exportSchema = false)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}
