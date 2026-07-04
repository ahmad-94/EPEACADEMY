package com.example.androidapp.util

sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    data class Loading<out T>(val data: T? = null) : RequestState<T>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val error: Throwable) : RequestState<Nothing>()
}
