package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.repository.PostRepository
import com.example.androidapp.util.RequestState
import com.example.easypeasyenglish.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _selectedPost = MutableStateFlow<RequestState<Post>>(RequestState.Idle)
    val selectedPost: StateFlow<RequestState<Post>> = _selectedPost

    fun getPostById(postId: String) {
        viewModelScope.launch {
            repository.getPostById(postId).collectLatest { state ->
                _selectedPost.value = state
            }
        }
    }
}
