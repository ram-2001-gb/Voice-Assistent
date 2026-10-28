package com.aura.assistant.voice.recognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.aura.assistant.domain.models.AssistantLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed class SpeechRecognitionEvent {
    data class PartialResult(val text: String) : SpeechRecognitionEvent()
    data class FinalResult(val text: String) : SpeechRecognitionEvent()
    data class RmsChanged(val rmsDb: Float) : SpeechRecognitionEvent()
    data class Error(val errorCode: Int, val message: String) : SpeechRecognitionEvent()
    data object EndOfSpeech : SpeechRecognitionEvent()
}

@Singleton
class AndroidSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun isRecognitionAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListeningFlow(
        language: AssistantLanguage,
        preferOffline: Boolean = false
    ): Flow<SpeechRecognitionEvent> = callbackFlow {
        if (!isRecognitionAvailable()) {
            trySend(SpeechRecognitionEvent.Error(-1, "Speech Recognition unavailable"))
            close()
            return@callbackFlow
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context).also {
            speechRecognizer = it
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

            when (language) {
                AssistantLanguage.TAMIL -> putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                AssistantLanguage.ENGLISH -> putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                AssistantLanguage.BILINGUAL -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta-IN")
                    putExtra(RecognizerIntent.EXTRA_ADDITIONAL_LANGUAGES, arrayOf("en-US", "en-IN"))
                }
            }

            if (preferOffline) {
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) { trySend(SpeechRecognitionEvent.RmsChanged(rmsdB)) }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { trySend(SpeechRecognitionEvent.EndOfSpeech) }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NETWORK -> "Network communication error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    else -> "Speech error: $error"
                }
                trySend(SpeechRecognitionEvent.Error(error, errorMsg))
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                trySend(SpeechRecognitionEvent.FinalResult(matches?.firstOrNull().orEmpty()))
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull().orEmpty()
                if (text.isNotBlank()) trySend(SpeechRecognitionEvent.PartialResult(text))
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)

        awaitClose {
            recognizer.stopListening()
            recognizer.cancel()
            recognizer.destroy()
            speechRecognizer = null
        }
    }

    fun cancelListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
