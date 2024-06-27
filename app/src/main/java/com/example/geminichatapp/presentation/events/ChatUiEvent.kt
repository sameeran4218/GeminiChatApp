package com.example.geminichatapp.presentation.events

import android.graphics.Bitmap


sealed class ChatUiEvent {
    data class UpdatePrompt(val newPrompt: String) : ChatUiEvent()

    data class SendPrompt(
        val prompt: String,
        val bitmap: Bitmap?,
    ) : ChatUiEvent()

    // show indicator
    data object ShowIndicator : ChatUiEvent()
}
