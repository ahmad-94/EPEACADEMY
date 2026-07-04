package com.example.androidapp.ui.screens

import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Layout
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil3.compose.AsyncImage
import com.example.androidapp.util.RequestState
import com.example.androidapp.viewmodel.DetailsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    postId: String,
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit
) {
    val postState by viewModel.selectedPost.collectAsState()

    LaunchedEffect(key1 = postId) {
        viewModel.getPostById(postId)
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val post = when (postState) {
                is RequestState.Success -> (postState as RequestState.Success).data
                is RequestState.Loading -> (postState as RequestState.Loading).data
                else -> null
            }

            if (post != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = post.thumbnail,
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .height(250.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = formatDate(post.date),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = post.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        val contentColor = MaterialTheme.colorScheme.onSurface
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    textSize = 16f
                                    setTextColor(contentColor.toArgb())
                                    movementMethod = LinkMovementMethod.getInstance()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        @Suppress("WrongConstant")
                                        justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
                                    }
                                }
                            },
                            update = { textView ->
                                textView.text = HtmlCompat.fromHtml(
                                    post.content,
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (postState is RequestState.Loading && post == null) {
                CircularProgressIndicator(color = Color.Red)
            }

            if (postState is RequestState.Error && post == null) {
                Text("Error: ${(postState as RequestState.Error).error.message}")
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(date)
}
