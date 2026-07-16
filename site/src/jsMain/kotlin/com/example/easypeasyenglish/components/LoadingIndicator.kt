package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.models.ApiListResponse
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.silk.components.text.SpanText

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
    when (response) {
        is ApiListResponse.Idle -> {
            LoadingIndicator()
        }
        is ApiListResponse.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SpanText("Error: ${response.message}")
            }
        }
        else -> {}
    }
}
