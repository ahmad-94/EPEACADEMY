package com.example.easypeasyenglish.navigation

import com.example.easypeasyenglish.models.Category
import com.example.easypeasyenglish.models.Params.CAT_PARAM
import com.example.easypeasyenglish.models.Params.POST_ID_PARAM
import com.example.easypeasyenglish.models.Params.QUERY_PARAM
import com.example.easypeasyenglish.utils.Id.UPDATED_PARAM

sealed class Screen(val route: String) {
    object AdminLogin: Screen(route = "login")
    object AdminHome: Screen(route = "/admin/")
    object AdminCreatePost: Screen(route = "/admin/createpost") {
        fun passPostId(id: String) = "/admin/createpost?$POST_ID_PARAM=$id"
    }
    object AdminMyPosts: Screen(route = "/admin/myposts") {
        fun searchPostsByTitle(query: String) = "/admin/myposts?$QUERY_PARAM=$query"
    }
    object AdminSuccess: Screen(route = "/admin/success") {
        fun updatePost(result: Boolean) = "/admin/success?$UPDATED_PARAM=$result"
    }
    object HomePage: Screen(route = "/")
    object SearchPage: Screen(route = "/search/query") {
        fun searchPostsByCategory(category: Category) = "/search/query?${CAT_PARAM}=${category.name}"
//        fun searchPostsByTitle(query: String) = "/search/query?${QUERY_PARAM}=${query}"
    }

    object SearchPageByTitle: Screen("/search/query") {
        fun searchPostsByTitle(query: String) = "/search/query?${QUERY_PARAM}=${query}"
    }

    object PostPage: Screen("/posts/post") {
        fun getPost(id: String) = "/posts/post?$POST_ID_PARAM=$id"
    }
}