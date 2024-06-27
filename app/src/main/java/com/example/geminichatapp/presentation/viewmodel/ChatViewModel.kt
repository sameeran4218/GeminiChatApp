package com.example.geminichatapp.presentation.viewmodel

import android.graphics.Bitmap
import androidx.collection.emptyLongSet
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminichatapp.data.ChatData
import com.example.geminichatapp.domain.model.Chat
import com.example.geminichatapp.presentation.events.ChatUiEvent
import com.example.geminichatapp.presentation.state.ChatState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel()
{
    private val _chatState= MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    fun onEvent(event: ChatUiEvent)
    {
        when(event)
        {
            is ChatUiEvent.UpdatePrompt -> {
                _chatState.update { it.copy(prompt = event.newPrompt) }
            }
            is ChatUiEvent.SendPrompt -> {
                if(event.prompt.isNotEmpty())
                {
                    addPrompt(event.prompt,event.bitmap)
                    showIndicator()
                    if(event.bitmap!=null)
                    {
                        getResponseWithImage(event.prompt,event.bitmap)
                }
                    else{
                        getResponseWithoutImage(event.prompt)
                }
                }
            }
            is ChatUiEvent.ShowIndicator -> {
                showIndicator()
            }
        }
    }

    private fun addPrompt(prompt: String,bitmap: Bitmap?)
    {
        // Add prompt to chat list
        _chatState.update { it.copy(chatList = it.chatList.toMutableList().apply
        { add(Chat(prompt,bitmap,true)) }, prompt = "",bitmap = null)}
    }
    private fun getResponseWithoutImage(prompt: String)
    {
        viewModelScope.launch {
            val response = ChatData.getResponseWithoutImage(prompt)
            _chatState.update { it.copy(chatList = it.chatList.toMutableList().apply
            { add(0,response) }
            ,showIndicator = false) }

    }
    }
    private fun getResponseWithImage(prompt: String,bitmap: Bitmap)
    {
        viewModelScope.launch {
            val response = ChatData.getResponseWithImage(prompt,bitmap)
            _chatState.update { it.copy(chatList = it.chatList.toMutableList().apply
            { add(0,response) },showIndicator = false) }

    }
}
    private fun showIndicator()
    {
        _chatState.update { it.copy(showIndicator = true) }

    }
}