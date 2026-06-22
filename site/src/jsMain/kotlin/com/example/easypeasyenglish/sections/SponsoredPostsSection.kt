package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.components.PostPreview
import com.example.easypeasyenglish.components.PostsView
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.utils.Dimensions.PAGE_WIDTH
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.silk.components.icons.fa.FaTag
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Composable
fun SponsoredPostsSection(
    breakpoint: Breakpoint,
    posts: List<PostWithoutDetails>,
    onClick: (String) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Colors.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(PAGE_WIDTH.px)
            ,
            contentAlignment = Alignment.TopCenter
        ) {
            SponsoredPost(
                breakpoint = breakpoint,
                posts = posts,
                onClick = onClick
            )
        }
    }
}

@Composable
fun SponsoredPost(
    breakpoint: Breakpoint,
    posts: List<PostWithoutDetails>,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .cursor(Cursor.Pointer)
            .fillMaxWidth(
                if (breakpoint > Breakpoint.MD) 80.percent
                else 90.percent
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.margin(bottom = 30.px),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FaTag(
                modifier = Modifier
                    .margin(right = 10.px)
                    .color(Colors.Purple),
                size = IconSize.LG
            )
            SpanText(
                text = "Sponsored Posts",
                modifier = Modifier
                    .fontSize(18.px)
                    .fontWeight(FontWeight.Medium)
                    .color(Colors.Purple)
            )
        }
        SimpleGrid(
            numColumns = numColumns(base = 1, xl = 2),
            modifier = Modifier
                .fillMaxWidth()
                .cursor(Cursor.Pointer)
        ) {
            posts.forEach { post ->
                PostPreview(
                    post = post,
                    vertical = breakpoint < Breakpoint.MD,
                    titleMaxLine = 1,
                    thumbnailHeight = 250.px,
                    onClick = onClick
                )
            }
        }
    }
}