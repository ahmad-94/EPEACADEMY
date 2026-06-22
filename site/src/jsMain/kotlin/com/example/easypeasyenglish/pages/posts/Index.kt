package com.example.easypeasyenglish.pages.posts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.CategoryMenuItems
import com.example.easypeasyenglish.components.ErrorMessage
import com.example.easypeasyenglish.components.LoadingIndicator
import com.example.easypeasyenglish.components.OverflowSidePanel
import com.example.easypeasyenglish.models.ApiResponse
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.sections.FooterSection
import com.example.easypeasyenglish.sections.HeaderSection
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.Theme
import com.example.easypeasyenglish.utils.fetchSelectedPost
import com.example.easypeasyenglish.utils.parseDateString
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.hidden
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxHeight
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AutoComplete.Companion.on
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement


@OptIn(DelicateApi::class)
@Page("post")
@Composable
fun PostPage() {
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()
    var apiResponse by remember { mutableStateOf<ApiResponse>(ApiResponse.Idle) }
    var overflowMenuOpened by remember { mutableStateOf(false)}
    val hasPostIdParam = remember(context.route) {
        context.route.params.containsKey(POST_ID_PARAM)
    }

    LaunchedEffect(context.route) {
        if (hasPostIdParam) {
            val postId = context.route.params.getValue(POST_ID_PARAM)
            apiResponse = fetchSelectedPost(id = postId)
        }
    }

    Column(
        modifier = Modifier

            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (overflowMenuOpened) {
            OverflowSidePanel(
                onMenuClose = {
                    overflowMenuOpened  = false
                },
                content = {
//                    CategoryMenuItems(vertical = true)
                }
            )
        }
        HeaderSection(
            breakpoint = breakpoint,
            onMenuOpen = {
                overflowMenuOpened = true
            }
        )
        when (apiResponse) {
            is ApiResponse.Success -> {
                PostContent(post = (apiResponse as ApiResponse.Success).data)
            }
            is ApiResponse.Idle -> {
                LoadingIndicator()
            }
            is ApiResponse.Error -> ErrorMessage(message = (apiResponse as ApiResponse.Error).message)
        }
        FooterSection()
    }
}

@Composable
fun PostContent(post: Post) {
    LaunchedEffect(post) {
        (document.getElementById(Id.postContent) as HTMLDivElement).innerHTML = post.content
    }
    Column(
        modifier = Modifier
            .margin(top = 50.px, bottom = 200.px)
            .padding(leftRight = 24.px)
            .maxWidth(1280.px)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SpanText(
            modifier = Modifier
                .fontSize(16.px)
                .color(Theme.lightGray)
                .align(Alignment.Start)
               ,
            text = post.date.parseDateString()
        )
        SpanText(
            modifier = Modifier
                .fontSize(50.px)
                .fontWeight(FontWeight.Bold)
                .textOverflow(TextOverflow.Ellipsis)
                .overflow(Overflow.Hidden)
                .margin(bottom = 20.px)
                .align(Alignment.Start)

                .styleModifier {
                    property("display", "-webkit-box")
                    property("-webkit-line-clamp", "2")
                    property("line-clamp","2")
                    property("-webkit-box-orient", "vertical")
                }
            ,
            text = post.title

        )
        Image(
            src = post.thumbnail,
            modifier = Modifier
                .fillMaxWidth()
                .maxWidth(1280.px)
                .maxHeight(720.px)
                .margin(bottom = 40.px)
        )
        Div(
            attrs = Modifier
                .id(Id.postContent)
                .fillMaxWidth()
                .toAttrs()
        )
    }
}