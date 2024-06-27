package com.example.geminichatapp.presentation.state

import android.graphics.Bitmap
import com.example.geminichatapp.domain.model.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap?=null,
    val showIndicator: Boolean = false
)
