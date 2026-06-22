package com.example.easypeasyenglish.sections

import androidx.compose.runtime.Composable
import com.example.easypeasyenglish.components.PostPreview
import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.utils.Dimensions.PAGE_WIDTH
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.dpcm
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@Composable
fun MainPostsSection(
    breakpoint: Breakpoint,
    mainPosts: ApiListResponse,
    onClick: (String) -> Unit
) {
    Box (
        modifier = Modifier
            .backgroundColor(Color("#EAEFEF"))
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(PAGE_WIDTH.px),
            contentAlignment = Alignment.TopCenter
        ) {
            when(mainPosts) {
                is ApiListResponse.Idle -> {}
                is ApiListResponse.Success -> {
                    MainPosts(
                        breakpoint = breakpoint,
                        mainPosts = mainPosts.data,
                        onClick = onClick
                    )
                }
                is ApiListResponse.Error -> {}
            }
        }
    }
}

@Composable
fun MainPosts(
    breakpoint: Breakpoint,
    mainPosts: List<PostWithoutDetails>,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(
                if (breakpoint > Breakpoint.MD) 80.percent else 90.percent
            )
            .margin(topBottom = 20.px),
        verticalAlignment = Alignment.Top,
    ) {
        if (breakpoint > Breakpoint.XL) {
               PostPreview(
                   post = mainPosts.first(),
                   thumbnailHeight = 580.px,
                   onClick = { onClick(mainPosts.first().postId) }
               )
               Column(
                   modifier = Modifier
                       .fillMaxWidth(90.percent)
                       .margin(left = 20.px, top = 8.px)
               ) {
                   mainPosts.drop(1).forEach { postWithoutDetails ->
                       PostPreview(
                           post = postWithoutDetails,
                           vertical = false,
                           isMainSection = true,
                           titleMaxLine = 1,
                           thumbnailHeight = 180.px,
                           onClick = { onClick(postWithoutDetails.postId) }
                       )
                   }
               }

        } else if (breakpoint >= Breakpoint.LG) {
            Box (modifier = Modifier.margin(right = 10.px)) {
                PostPreview(
                    post = mainPosts.first(),
                    isMainSection = true,
                    thumbnailHeight = 600.px,
                    onClick = { onClick(mainPosts.first().postId) }
                )
            }
            Box(modifier = Modifier.margin(left = 10.px)) {
                PostPreview(
                    post = mainPosts[1],
                    thumbnailHeight = 580.px,
                    onClick = { onClick(mainPosts[1].postId) }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                mainPosts.forEach { postWithoutDetails ->
                    PostPreview(
                        post = postWithoutDetails,
                        thumbnailHeight = 580.px,
                        onClick = { onClick(postWithoutDetails.postId) }
                    )
                }
            }
        }
    }
}