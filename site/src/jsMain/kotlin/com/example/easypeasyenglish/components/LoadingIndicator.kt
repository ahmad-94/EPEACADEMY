package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.models.ApiListResponse
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.delay

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SpanText("Loading...")
    }
}

@Composable
fun ShowLoadingMessage(response: ApiListResponse) {
    var showingMessage by remember { mutableStateOf(false) }

    if (response is ApiListResponse.Idle) {
        showingMessage = true
        if (showingMessage) {
            LoadingIndicator()
        }
        if (showingMessage) {
            LaunchedEffect(Unit) {
                delay(3000)
                showingMessage = false
            }
        }
    }

}