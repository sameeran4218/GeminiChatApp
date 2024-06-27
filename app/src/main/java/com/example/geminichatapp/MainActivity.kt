package com.example.geminichatapp
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.geminichatapp.presentation.events.ChatUiEvent
import com.example.geminichatapp.presentation.viewmodel.ChatViewModel
import com.example.geminichatapp.ui.theme.GeminiChatAppTheme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {
    private val uriState = MutableStateFlow<Uri?>(null)
    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            uriState.value = uri
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiChatAppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = "Buddy",
                                fontSize = 40.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic) },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.Black,
                                titleContentColor = Color.White
                            )
                        )
                    },
                )
                {
                  ChatScreen()
                }



            }
        }
    }

    @Composable
    fun getBitmap(): Bitmap? {
        val uri = uriState.collectAsState().value

        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }

        return null
    }



    @Composable
    fun ChatScreen() {
        Box(modifier=Modifier.background(Color.LightGray)){
        @Composable
        fun UserChatItem(prompt: String, bitmap: Bitmap?) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(topEnd = 20.dp)), horizontalAlignment = Alignment.Start
            )
            {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(250.dp)
                            .padding(10.dp)
                            .clip(
                                RoundedCornerShape(10.dp)
                            )
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.Gray)
                        .align(Alignment.End)
                ) {
                    Text(
                        text = prompt,
                        modifier = Modifier.padding(7.dp),
                        fontSize = 20.sp,
                        color = Color.Black

                    )
                }
            }

        }

        @Composable
        fun ModelChatItem(response: String) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp)),
                horizontalAlignment = Alignment.Start
            )
            {
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = response,
                        modifier = Modifier
                            .padding(7.dp),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }

        }


        val chatViewModel: ChatViewModel = viewModel()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitmap()
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom)
        {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList)
                { index, chat ->
                    if (chat.isUser) {
                        UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                    } else {
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp, start = 3.dp, end = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "picked image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(10.dp)
                                .clip(
                                    RoundedCornerShape(10.dp)
                                )
                        )
                    }
                    IconButton(onClick = {
                        imagePicker.launch(
                            PickVisualMediaRequest
                                .Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                .build()
                        )
                    }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Rounded.Photo, contentDescription = "add image")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                TextField(value = chatState.prompt, onValueChange = {
                    chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                },
                    placeholder = { Text(text = "Enter your prompt") })
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(onClick = {
                    chatViewModel.onEvent(
                        ChatUiEvent.SendPrompt(
                            chatState.prompt,
                            bitmap
                        )
                    )
                }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "send prompt")
                }


            }
        }
    }
    }
}