package com.example.easypeasyenglish.pages.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.components.CategoryMenuItems
import com.example.easypeasyenglish.components.LoadingIndicator
import com.example.easypeasyenglish.components.OverflowSidePanel
import com.example.easypeasyenglish.components.ShowLoadingMessage
import com.example.easypeasyenglish.models.ApiListResponse
import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Params.CAT_PARAM
import com.example.easypeasyenglish.models.Params.POSTS_PER_PAGE
import com.example.easypeasyenglish.models.Params.QUERY_PARAM
import com.example.easypeasyenglish.models.PostWithoutDetails
import com.example.easypeasyenglish.navigation.Screen
import com.example.easypeasyenglish.sections.FooterSection
import com.example.easypeasyenglish.sections.HeaderSection
import com.example.easypeasyenglish.sections.LatestPostsSection
import com.example.easypeasyenglish.utils.searchPostsByCategory
import com.example.easypeasyenglish.utils.searchPostsByTitle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.framework.annotations.DelicateApi
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.px

@Page("query")
@OptIn(DelicateApi::class)
@Composable
fun SearchPage() {


    val context = rememberPageContext()
    var skip by remember { mutableStateOf(0)}
    val hasCategoryParam = remember(context.route) { context.route.params.containsKey(CAT_PARAM) }
    val hasQueryParam = remember(context.route) { context.route.params.containsKey(QUERY_PARAM) }
    var apiResponse by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }

    val catValue = remember(context.route) {
        if (hasCategoryParam) {
            context.route.params.getValue(CAT_PARAM)
        } else {
            ""
        }
    }

    val queryValue = remember(context.route) {
        if (hasQueryParam) {
            context.route.params.getValue(QUERY_PARAM)
        } else {
            ""
        }
    }

    val breakpoint = rememberBreakpoint()
    val scope = rememberCoroutineScope()
    val searchedPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    var searchedPostsToSkip by remember { mutableStateOf(0) }
    var showMoreSearchedPosts by remember { mutableStateOf(false) }
    var overflowMenuOpened by remember { mutableStateOf(false) }

    LaunchedEffect(context.route) {
        showMoreSearchedPosts = false
        searchedPostsToSkip = 0
       if (hasCategoryParam) {
           searchPostsByCategory(
               category = Category.valueOf(value = catValue),
               skip = skip,
               onSuccess = {
                   apiResponse = it
                   console.log("Success fetching searched posts posts:", searchedPosts)
                   if (it is ApiListResponse.Success ) {
                       searchedPosts.clear()
                       searchedPosts.addAll(it.data)
                       searchedPostsToSkip += POSTS_PER_PAGE
                       if (it.data.size >= POSTS_PER_PAGE) showMoreSearchedPosts = true
                   }
               },
               onError = {
                   console.log("Error fetching searched posts:", searchedPosts)
               }

           )
       } else if (hasQueryParam) {
           searchPostsByTitle(
               query = queryValue,
               skip = skip,
               onSuccess = {
                   apiResponse = it
                   console.log("Success fetching searched posts posts:", searchedPosts)
                   if (it is ApiListResponse.Success ) {
                       searchedPosts.clear()
                       searchedPosts.addAll(it.data)
                       searchedPostsToSkip += POSTS_PER_PAGE
                       if (it.data.size >= POSTS_PER_PAGE) showMoreSearchedPosts = true
                   }
               },
               onError = {
                   console.log("Error fetching searched posts:", searchedPosts)
               }

           )
       }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (overflowMenuOpened) {
            OverflowSidePanel(
                onMenuClose = {
                    overflowMenuOpened = false
                },
                content = {
//                    CategoryMenuItems(
//                        vertical = true,
//                        selectedCategory = if (hasCategoryParam) Category.valueOf(catValue) else null
//                    )
                }
            )
        }

        HeaderSection(
            breakpoint = breakpoint,
            selectedCategory = if (hasCategoryParam) Category.valueOf(catValue) else null,
            onMenuOpen = {
                overflowMenuOpened = true
            }
        )
        if (apiResponse is ApiListResponse.Success) {
            if (hasCategoryParam) {
                SpanText(
                    text = catValue,
                    modifier = Modifier
                        .fontSize(36.px)
                        .fillMaxWidth()
                        .margin(top = 100.px, bottom = 40.px)
                        .textAlign(TextAlign.Center)
                )
            }
            LatestPostsSection(
                posts = searchedPosts,
                breakpoint = breakpoint,
                showMore = showMoreSearchedPosts,
                onShowMore = {
                    if (hasCategoryParam) {
                        scope.launch {
                            searchPostsByCategory(
                                category = Category.valueOf(catValue),
                                skip = searchedPostsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        if (it.data.isNotEmpty()) {
                                            if (it.data.size < POSTS_PER_PAGE) {
                                                showMoreSearchedPosts = false
                                            }
                                            searchedPosts.addAll(it.data)
                                            searchedPostsToSkip += POSTS_PER_PAGE
                                        } else {
                                            showMoreSearchedPosts = false
                                        }
                                    }
                                },
                                onError = {}
                            )
                        }
                    } else if (hasQueryParam) {
                        scope.launch {
                            searchPostsByTitle(
                                query = queryValue,
                                skip = searchedPostsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        if (it.data.isNotEmpty()) {
                                            if (it.data.size < POSTS_PER_PAGE) {
                                                showMoreSearchedPosts = false
                                            }
                                            searchedPosts.addAll(it.data)
                                            searchedPostsToSkip += POSTS_PER_PAGE
                                        } else {
                                            showMoreSearchedPosts = false
                                        }
                                    }
                                },
                                onError = {}
                            )
                        }
                    }
                },
                onClick = {
                    context.router.navigateTo(Screen.PostPage.getPost(id = it))
                }

            )
        } else {
            ShowLoadingMessage(apiResponse)
        }
        FooterSection()
    }
}