package com.example.easypeasyenglish.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.CategoryMenuItems
import com.example.easypeasyenglish.components.OverflowSidePanel
import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.Params.POSTS_PER_PAGE
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.sections.FooterSection
import com.example.easypeasyenglish.sections.HeaderSection
import com.example.easypeasyenglish.sections.LatestPostsSection
import com.example.easypeasyenglish.sections.MainPostsSection
import com.example.easypeasyenglish.sections.NewsletterSection
import com.example.easypeasyenglish.sections.SponsoredPostsSection
import com.example.easypeasyenglish.utils.fetchLatestPosts
import com.example.easypeasyenglish.utils.fetchMainPosts
import com.example.easypeasyenglish.utils.fetchPopularPosts
import com.example.easypeasyenglish.utils.fetchSponsoredPosts
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.launch


@OptIn(DelicateApi::class)
@Page
@Composable
fun HomePage() {
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    var overflowMenuOpened by remember { mutableStateOf(false) }

    var mainPosts by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    val latestPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val sponsoredPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val popularPosts = remember { mutableStateListOf<PostWithoutDetails>() }

    var latestPostsToSkip  by remember { mutableIntStateOf(0) }
    var popularPostsToSkip  by remember { mutableIntStateOf(0) }
    var showMoreLatest by remember { mutableStateOf(false) }
    var showMorePopular by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        fetchMainPosts(
            onSuccess = {
                mainPosts = it
                console.log("Success fetching main posts:", mainPosts)
            },
            onError = {
                console.log("Error fetching main posts:", mainPosts)
            }
        )

        fetchLatestPosts(
            skip = latestPostsToSkip,
            onSuccess = {
                console.log("Success fetching latest posts:", latestPosts)
                if (it is ApiListResponse.Success ) {
                    latestPosts.addAll(it.data)
                    latestPostsToSkip += POSTS_PER_PAGE
                    if (it.data.size >= POSTS_PER_PAGE) showMoreLatest = true
                }
            },
            onError = {
                console.log("Error fetching latest posts:", latestPosts)
            }
        )

        fetchSponsoredPosts(
            onSuccess = {
                if (it is ApiListResponse.Success ) {
                    sponsoredPosts.addAll(it.data)
                }
            },
            onError = {
                console.log("Error fetching sponsored posts:", sponsoredPosts)
            }
        )

        fetchPopularPosts(
            skip = popularPostsToSkip,
            onSuccess = {
                console.log("Success fetching popular posts:", popularPosts)
                if (it is ApiListResponse.Success ) {
                    popularPosts.addAll(it.data)
                    popularPostsToSkip += POSTS_PER_PAGE
                    if (it.data.size >= POSTS_PER_PAGE) showMorePopular = true
                }
            },
            onError = {}
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
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

        MainPostsSection(
            breakpoint = breakpoint,
            mainPosts = mainPosts,
            onClick = {
                context.router.navigateTo(Screen.PostPage.getPost(id = it))
            }
        )

        LatestPostsSection(
            title = "Latest Posts",
            posts = latestPosts,
            breakpoint = breakpoint,
            showMore = showMoreLatest,
            onShowMore = {
                scope.launch {
                    fetchLatestPosts(
                        skip = latestPostsToSkip,
                        onSuccess = {
                            if (it is ApiListResponse.Success) {
                                if (it.data.isNotEmpty()) {
                                    if (it.data.size < POSTS_PER_PAGE) {
                                        showMoreLatest = false
                                    }
                                    latestPosts.addAll(it.data)
                                    latestPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMoreLatest = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            latest = true,
            onClick = {
                context.router.navigateTo(Screen.PostPage.getPost(id = it))
            }

        )


        SponsoredPostsSection(
            breakpoint = breakpoint,
            posts = sponsoredPosts,
            onClick = {
                context.router.navigateTo(Screen.PostPage.getPost(id = it))
            }
        )

        LatestPostsSection(
            title = "Popular Posts",
            posts = popularPosts,
            breakpoint = breakpoint,
            showMore = showMorePopular,
            onShowMore = {
                scope.launch {
                    fetchPopularPosts(
                        skip = popularPostsToSkip,
                        onSuccess = {
                            if (it is ApiListResponse.Success) {
                                if (it.data.isNotEmpty()) {
                                    if (it.data.size < POSTS_PER_PAGE) {
                                        showMorePopular = false
                                    }
                                    popularPosts.addAll(it.data)
                                    popularPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMorePopular = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            latest = false,
            onClick = {
                context.router.navigateTo(Screen.PostPage.getPost(id = it))
            }

        )

        NewsletterSection(breakpoint = breakpoint)
        FooterSection()

    }
}