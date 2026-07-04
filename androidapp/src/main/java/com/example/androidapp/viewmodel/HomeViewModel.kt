package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.repository.PostRepository
import com.example.androidapp.util.RequestState
import com.example.easypeasyenglish.models.Post
import com.example.easypeasyenglish.models.PostWithoutDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    var allPosts: RequestState<List<PostWithoutDetails>> = RequestState.Loading()
)

class HomeViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHomeScreenData()
    }

    fun loadHomeScreenData() {
        viewModelScope.launch {
            repository.getAllPosts(0).collectLatest { state ->
                _uiState.update { it.copy(allPosts = state) }
            }
        }
    }

    fun loadMorePosts(skip: Int) {
        viewModelScope.launch {
            repository.getAllPosts(skip).collectLatest { state ->
                val current = _uiState.value.allPosts
                if (current is RequestState.Success && state is RequestState.Success) {
                    val combined = current.data + state.data
                    _uiState.update {
                        it.copy(allPosts = RequestState.Success(combined))
                    }
                } else {
                    _uiState.update { it.copy(allPosts = state) }
                }
            }
        }
    }
}
