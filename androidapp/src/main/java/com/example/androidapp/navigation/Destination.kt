package com.example.androidapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidapp.ui.screens.DetailsScreen
import com.example.androidapp.ui.screens.HomeScreen
import com.example.androidapp.ui.screens.SearchScreen
import com.example.androidapp.viewmodel.DetailsViewModel
import com.example.androidapp.viewmodel.HomeViewModel
import com.example.androidapp.viewmodel.SearchViewModel
import com.example.easypeasyenglish.models.Post

sealed class Destination(val route: String) {
    object Home: Destination("home")
    object Search: Destination("search")
    object Category: Destination("category/{category}") {
        fun passCategory(category: Category) = "category/$category"
    }
    object Details: Destination("details/{postId}") {
        fun passPostId(postId: String) = "details/$postId"
    }
}

@Composable
fun EPENavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    detailsViewModel: DetailsViewModel,
    searchViewModel: SearchViewModel,
    modifier: Modifier
) {
    NavHost(
        startDestination = Destination.Home.route,
        navController = navController
    ) {
        composable(Destination.Home.route) {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }
        composable(Destination.Search.route) {
            SearchScreen(
                navController = navController,
                viewModel = searchViewModel
            )
        }
        composable(
            route = Destination.Details.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            DetailsScreen(
                postId = postId,
                viewModel = detailsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}