package com.aura.assistant.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.aura.assistant.assistant.orchestration.AssistantOrchestrator
import com.aura.assistant.domain.models.AssistantProfile
import com.aura.assistant.presentation.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var orchestrator: AssistantOrchestrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by orchestrator.state.collectAsState()
            val transcription by orchestrator.liveTranscription.collectAsState()
            val isOnline by orchestrator.isOnline.collectAsState()
            val profile by remember { mutableStateOf(AssistantProfile()) }

            HomeScreen(
                state = state,
                transcription = transcription,
                isOnline = isOnline,
                profile = profile,
                onMicClicked = { orchestrator.startInteraction(profile) },
                onStopClicked = { orchestrator.interrupt() }
            )
        }
    }
}
