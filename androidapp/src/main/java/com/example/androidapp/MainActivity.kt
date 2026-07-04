package com.example.androidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.androidapp.navigation.EPENavGraph
import com.example.androidapp.network.ApiService
import com.example.androidapp.repository.PostRepository
import com.example.androidapp.ui.theme.EasypeayenglishTheme
import com.example.androidapp.viewmodel.DetailsViewModel
import com.example.androidapp.viewmodel.HomeViewModel
import com.example.androidapp.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiService()
        val repository = PostRepository(apiService)
        val homeViewModel = HomeViewModel(repository)
        val detailsViewModel = DetailsViewModel(repository)
        val searchViewModel = SearchViewModel(repository)

        enableEdgeToEdge()
        setContent {
            EasypeayenglishTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    EPENavGraph(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        detailsViewModel = detailsViewModel,
                        searchViewModel = searchViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
