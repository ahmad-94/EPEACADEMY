package com.example.easypeasyenglish.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.AdminPageLayout
import com.example.easypeasyenglish.components.PostsView
import com.example.easypeasyenglish.components.SearchBar
import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.Params.POSTS_PER_PAGE
import com.example.easypeasyenglish.models.Params.QUERY_PARAM
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.utils.Dimensions
import com.example.easypeasyenglish.utils.Id
import com.example.easypeasyenglish.utils.Theme
import com.example.easypeasyenglish.utils.deleteSelectedPosts
import com.example.easypeasyenglish.utils.fetchMyPosts
import com.example.easypeasyenglish.utils.isUserLoggedIn
import com.example.easypeasyenglish.utils.noBorder
import com.example.easypeasyenglish.utils.parseSwitchText
import com.example.easypeasyenglish.utils.searchPostsByTitle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.w3c.dom.HTMLInputElement

@Page("/admin/myposts")
@Composable
fun MyPostsPage() {
    isUserLoggedIn {
        MyPostsScreen()
    }
}

@OptIn(DelicateApi::class)
@Composable
fun MyPostsScreen() {
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()
    var isSwitchSelected by remember { mutableStateOf(false) }
    var switchText by remember { mutableStateOf("Select") }
    val myPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val coroutineScope = rememberCoroutineScope()

    var postsToSkip by remember { mutableStateOf(0) }
    var showMore by remember { mutableStateOf(false) }
    val selectedPosts = remember { mutableStateListOf<String>() }

    val hasParam = remember(context.route) { context.route.params.containsKey(QUERY_PARAM) }
    val query = remember(context.route) { context.route.params[QUERY_PARAM] ?: "" }


    LaunchedEffect(context.route) {
        postsToSkip = 0
        if (hasParam) {
            (document.getElementById(Id.ADMIN_SEARCH_BAR) as HTMLInputElement).value = query
            searchPostsByTitle(
                query = query,
                skip = postsToSkip,
                onSuccess = {
                    if (it is ApiListResponse.Success) {
                        myPosts.clear()
                        myPosts.addAll(it.data)
                        postsToSkip += POSTS_PER_PAGE
                        showMore = it.data.size >= POSTS_PER_PAGE
                    }
                },
                onError = {
                    println(it)
                }
            )
        } else {
            fetchMyPosts(
                skip = postsToSkip,
                onSuccess = {
                    if (it is ApiListResponse.Success) {
                        myPosts.clear()
                        myPosts.addAll(it.data)
                        postsToSkip += POSTS_PER_PAGE
                        showMore = it.data.size >= POSTS_PER_PAGE
                    }
                },
                onError = {
                    println(it)
                }
            )
        }
    }

    AdminPageLayout {
        Column(
            modifier = Modifier
                .margin(topBottom = 50.px)
                .fillMaxSize()
                .padding(left = if (breakpoint > Breakpoint.MD) Dimensions.SIDE_PANEL_WIDTH.px else 0.px),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (breakpoint > Breakpoint.MD) 30.percent else 50.percent
                    ),
                contentAlignment = Alignment.Center
            ) {
                SearchBar(
                    modifier = Modifier
                        .visibility(if (isSwitchSelected) Visibility.Hidden else Visibility.Visible)

                    ,
                    onEnterClick = {
                        val query = (document.getElementById(Id.ADMIN_SEARCH_BAR) as HTMLInputElement).value
                        if (query.isNotEmpty()) {
                            context.router.navigateTo(Screen.AdminMyPosts.searchPostsByTitle(query = query))
                        } else {
                            context.router.navigateTo(Screen.AdminMyPosts.route)
                        }
                    },
                    breakpoint = breakpoint,
                    onSearchBarClick = {},
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(if (breakpoint > Breakpoint.MD) 80.percent else 90.percent)
                    .margin(bottom = 24.px, top =  24.px),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px),
                        size = SwitchSize.LG,
                        checked = isSwitchSelected,
                        onCheckedChange = {
                            isSwitchSelected = it
                            if (isSwitchSelected) {
                                switchText = "No Posts Selected."
                            } else {
                                 switchText = "Select"
                                selectedPosts.clear()
                            }
                        }
                    )
                    SpanText(
                        modifier = Modifier.color(if (isSwitchSelected) Colors.Black else Theme.lightGray),
                        text = switchText
                    )
                }
                Button(
                    attrs = Modifier
                        .height(54.px)
                        .padding(leftRight = 24.px)
                        .backgroundColor(Colors.Red)
                        .color(Colors.White)
                        .noBorder()
                        .borderRadius(4.px)
                        .fontSize(14.px)
                        .fontWeight(FontWeight.Medium)
                        .visibility(if (selectedPosts.isNotEmpty()) Visibility.Visible else Visibility.Hidden)
                        .onClick {
                            coroutineScope.launch {
                                val result = deleteSelectedPosts(selectedPosts)
                                if (result) {
                                    isSwitchSelected = false
                                    switchText = "Select"
                                    postsToSkip -= selectedPosts.size
                                    selectedPosts.forEach {selectedPostId ->
                                        myPosts.removeAll {
                                            it.postId == selectedPostId
                                        }
                                    }
                                    selectedPosts.clear()
                                }
                            }
                        }
                        .toAttrs()
                ) {
                    SpanText("Delete")
                }
            }
            PostsView(
                posts = myPosts,
                breakpoint = breakpoint,
                showMore = showMore,
                selectable = isSwitchSelected,
                onSelect = {
                    selectedPosts.add(it)
                    switchText = parseSwitchText(selectedPosts.toList())
                },
                onDeSelect = {
                    selectedPosts.remove(it)
                    switchText = parseSwitchText(selectedPosts.toList())
                },
                onShowMore = {
                    coroutineScope.launch {
                        if (hasParam) {
                            searchPostsByTitle(
                                query = query,
                                skip = postsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        val newPosts = it.data
                                        if (newPosts.isNotEmpty()) {
                                            myPosts.addAll(newPosts)
                                            postsToSkip += POSTS_PER_PAGE
                                            showMore = newPosts.size >= POSTS_PER_PAGE
                                        } else {
                                            showMore = false
                                        }
                                    }
                                },
                                onError = {
                                    println(it)
                                }
                            )
                        } else {
                            fetchMyPosts(
                                skip = postsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        val newPosts = it.data
                                        if (newPosts.isNotEmpty()) {
                                            myPosts.addAll(newPosts)
                                            postsToSkip += POSTS_PER_PAGE
                                            showMore = newPosts.size >= POSTS_PER_PAGE
                                        } else {
                                            showMore = false
                                        }
                                    }
                                },
                                onError = {
                                    println(it)
                                }
                            )
                        }
                    }
                },
                onClick = {
                    context.router.navigateTo(Screen.AdminCreatePost.passPostId(id = it))
                }
            )
        }
    }
}