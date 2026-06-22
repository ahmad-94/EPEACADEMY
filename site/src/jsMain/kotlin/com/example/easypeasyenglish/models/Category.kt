package com.example.easypeasyenglish.models

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import kotlinx.serialization.Serializable

@Serializable
actual enum class Category(val color: Color) {
    Programming(color = Colors.Green),
    Technology(color = Colors.Yellow),
    Design(color = Colors.Pink)
}