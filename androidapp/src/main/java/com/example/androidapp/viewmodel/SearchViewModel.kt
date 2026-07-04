package com.example.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidapp.repository.PostRepository
import com.example.androidapp.util.RequestState
import com.example.easypeasyenglish.models.PostWithoutDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: RequestState<List<PostWithoutDetails>> = RequestState.Idle
)

class SearchViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun searchPosts(query: String) {
        if (query.isEmpty()) {
            _uiState.value = _uiState.value.copy(searchResults = RequestState.Idle)
            return
        }
        viewModelScope.launch {
            repository.searchPosts(query, 0).collectLatest { state ->
                _uiState.value = _uiState.value.copy(searchResults = state)
            }
        }
    }
}
