package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.utils.Theme
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px

@Composable
fun CategoryChip(category: Category) {
    Box(
        modifier = Modifier
            .padding(leftRight = 14.px)
            .height(32.px)
            .borderRadius(100.px)
            .border(
                width = 1.px,
                style = LineStyle.Solid,
                color = Theme.lightGray
            ),
        contentAlignment = Alignment.Center

    ) {
        SpanText(
            text = category.name,
            modifier = Modifier
                .fontSize(12.px)
                .color(Theme.lightGray)
        )
    }
}