package com.example.easypeasyenglish.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.traceEventEnd
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Composable
fun PostsView(
    title: String? = null,
    posts: List<PostWithoutDetails>,
    breakpoint: Breakpoint,
    showMore: Boolean ,
    selectable: Boolean = false,
    onSelect: (String) -> Unit = {},
    onDeSelect: (String) -> Unit = {},
    onShowMore: () -> Unit,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(if (breakpoint > Breakpoint.MD) 80.percent else 90.percent),
        verticalArrangement = Arrangement.Center
    ) {
        if (title != null) {
            SpanText(
                text = title,
                modifier = Modifier
                    .fontSize(24.px)
                    .fontWeight(FontWeight.Bold)
                    .margin(topBottom = 24.px, leftRight = 8.px)
            )
        }

        SimpleGrid(
            modifier = Modifier.fillMaxWidth(),
            numColumns = numColumns(base = 1, sm = 1, md = 1, lg = 2)
        ) {
            posts.forEach {
                PostPreview(
                    post = it,
                    selectable = selectable,
                    onSelect = onSelect,
                    onDeSelect = onDeSelect,
                    onClick = onClick
                )
            }
        }
        SpanText(
            modifier = Modifier
                .fillMaxWidth()
                .margin(topBottom = 50.px)
                .fontSize(16.px)
                .fontWeight(FontWeight.Medium)
                .textAlign(TextAlign.Center)
                .onClick { onShowMore() }
                .visibility(if (showMore) Visibility.Visible else Visibility.Hidden)
                .cursor(Cursor.Pointer),
            text = "Show more"
        )
    }
}