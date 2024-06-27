package com.example.geminichatapp.data

import android.graphics.Bitmap
import com.example.geminichatapp.BuildConfig
import com.example.geminichatapp.domain.model.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    val api_key = BuildConfig.apiKey

    // get response without image
    suspend fun getResponseWithoutImage(prompt: String): Chat {
        // use gemini-pro model
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = api_key
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            return Chat(prompt = response.text ?: "error", bitmap = null, isUser = false)
        } catch (e: Exception) {

            return Chat(prompt = "error", bitmap = null, isUser = false)
        }
    }

    // get response with image
    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat
    {
        // use gemini-pro-vision model
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = api_key
        )
        val inputContent= content {
            image(bitmap)
            text(prompt) }
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(inputContent)
            }
            return Chat(prompt = response.text ?: "error", bitmap = bitmap, isUser = false)
            }
        catch (e:Exception){
            return Chat(prompt = "error", bitmap = null, isUser = false)
        }
    }
}
