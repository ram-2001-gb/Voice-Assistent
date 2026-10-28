package com.aura.assistant.domain.models

sealed class AssistantState {
    data object Idle : AssistantState()
    data class Listening(val rmsDb: Float = 0f) : AssistantState()
    data class Processing(val isOfflineMode: Boolean = false) : AssistantState()
    data class Speaking(val spokenText: String, val audioAmplitude: Float = 0f) : AssistantState()
    data class Interrupted(val reason: String = "User cancellation") : AssistantState()
    data class Error(val message: String, val canRetry: Boolean = true) : AssistantState()
}

enum class AssistantLanguage(val tag: String, val displayName: String, val defaultLocale: String) {
    TAMIL("ta", "தமிழ் (Tamil)", "ta_IN"),
    ENGLISH("en", "English", "en_US"),
    BILINGUAL("ta-en", "Tamil + English (தமிங்கிலம்)", "ta_IN")
}

enum class AssistantVoiceGender {
    MALE, FEMALE, SYSTEM_DEFAULT
}

data class AssistantProfile(
    val customName: String = "Aura",
    val language: AssistantLanguage = AssistantLanguage.BILINGUAL,
    val voiceGender: AssistantVoiceGender = AssistantVoiceGender.FEMALE,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val personalityPrompt: String = "Helpful, concise, friendly bilingual assistant.",
    val isOfflinePreferred: Boolean = false,
    val isHistoryEnabled: Boolean = true
)
