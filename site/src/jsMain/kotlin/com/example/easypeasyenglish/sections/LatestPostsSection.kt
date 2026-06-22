package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.components.PostsView
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.utils.Dimensions.PAGE_WIDTH
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.px

@Composable
fun LatestPostsSection(
    title: String? = null,
    posts: List<PostWithoutDetails>,
    breakpoint: Breakpoint,
    showMore: Boolean,
    latest: Boolean? = null,
    onShowMore: () -> Unit,
    onClick: (String) -> Unit
) {

    Box (
        modifier = Modifier
            .backgroundColor(Color(if(latest == true) "#EBF4F6" else "#EEEEEE"))
            .fillMaxWidth()

        ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .maxWidth(PAGE_WIDTH.px)
                .margin(topBottom = 50.px),
            contentAlignment = Alignment.TopCenter
        ) {
            PostsView(
                title = title,
                posts = posts,
                breakpoint = breakpoint,
                showMore = showMore,
                onShowMore = onShowMore,
                onClick = onClick
            )
        }
    }
}