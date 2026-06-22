package com.example.easypeasyenglish.models

import jdk.internal.org.jline.utils.Colors
import kotlinx.serialization.Serializable
import java.awt.Color

@Serializable
actual enum class Category(val color: Color) {
    Programming(color = Color.green),
    Technology(color = Color.yellow),
    Design(color = Color.pink)
}