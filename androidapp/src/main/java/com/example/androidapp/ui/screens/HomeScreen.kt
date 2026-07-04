package com.example.androidapp.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Layout
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.androidapp.R
import com.example.androidapp.navigation.Destination
import com.example.androidapp.util.RequestState
import com.example.androidapp.viewmodel.HomeViewModel
import com.example.easypeasyenglish.models.PostWithoutDetails
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.epe),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .size(90.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "EPE ACADEMY",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = "YouTube", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        openUrl(context, "https://www.youtube.com/channel/UCJJ8KFpya16lfWYar7vGB1A?sub_confirmation=1")
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(painter = painterResource(id = R.drawable.youtube), contentDescription = null, modifier = Modifier.size(38.dp), tint = Color.Unspecified) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = "Facebook", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        openUrl(context, "https://www.facebook.com/ahmad.shiravand.12")
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(painter = painterResource(id = R.drawable.facebook), contentDescription = null, modifier = Modifier.size(38.dp), tint = Color.Unspecified) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = "Instagram", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        openUrl(context, "https://www.instagram.com/epeacademy?igsh=aXlubDJ5bXU3MGE=")
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(painter = painterResource(id = R.drawable.instagram), contentDescription = null, modifier = Modifier.size(38.dp), tint = Color.Unspecified) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text(text = "TikTok", fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        openUrl(context, "https://tiktok.com/@epeacademy")
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(painter = painterResource(id = R.drawable.tictok), contentDescription = null, modifier = Modifier.size(38.dp), tint = Color.Unspecified) }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "EPE ACADEMY",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_menu),
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Destination.Search.route) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Red,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
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
                val state = uiState.allPosts
                val posts = when (state) {
                    is RequestState.Success -> state.data
                    is RequestState.Loading -> state.data ?: emptyList()
                    else -> emptyList()
                }

                if (posts.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(posts) { post ->
                            PostItem(
                                post = post,
                                onClick = {
                                    navController.navigate(Destination.Details.passPostId(post.postId))
                                }
                            )
                        }
                    }
                }

                if (state is RequestState.Loading && posts.isEmpty()) {
                    CircularProgressIndicator(color = Color.Red)
                }

                if (state is RequestState.Error && posts.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Error: ${state.error.message}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadHomeScreenData() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(text = "Try Again", color = Color.White)
                        }
                    }
                }

                if (state is RequestState.Success && posts.isEmpty()) {
                    Text(text = "Empty")
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = if (url.contains("facebook.com")) {
            // Try to open in Facebook App/Lite using their custom URI scheme
            val fbUri = "fb://facewebmodal/f?href=$url".toUri()
            Intent(Intent.ACTION_VIEW, fbUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // If the specialized FB scheme fails (app not installed), use standard URL
            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: PostWithoutDetails,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = post.thumbnail,
                contentDescription = "Post Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = post.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 14f
                            setTextColor(contentColor.toArgb())
                            maxLines = 3
                            ellipsize = android.text.TextUtils.TruncateAt.END
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
                            post.content ?: "",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
