package com.aura.assistant.data.repository

import com.aura.assistant.data.local.ConversationDao
import com.aura.assistant.data.local.ConversationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao
) {
    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversationsFlow()

    suspend fun saveExchange(query: String, response: String, language: String, isOffline: Boolean) = withContext(Dispatchers.IO) {
        conversationDao.insertConversation(
            ConversationEntity(
                userQuery = query,
                assistantResponse = response,
                language = language,
                isOfflineExecuted = isOffline
            )
        )
    }

    suspend fun deleteItem(id: Long) = withContext(Dispatchers.IO) {
        conversationDao.deleteConversationById(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        conversationDao.clearAllHistory()
    }
}
