package com.example.easypeasyenglish.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.easypeasyenglish.models.Newsletter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsletterStateHolder(
    private val scope: CoroutineScope
) {

    var email by mutableStateOf("")
    var showInvalidEmailPopup by mutableStateOf(false)
    var showSuccessPopup by mutableStateOf(false)
    var responseMessage by mutableStateOf("")

    suspend fun subscribe(newEmail: String) {
        email = newEmail
        if (!isEmailValid(email = email)) {
            showInvalidEmailPopup = true
            delay(2000)
            showInvalidEmailPopup = false
            return
        } else {
            scope.launch {
                try {
                    val response = subscribeToNewsLetter(Newsletter(email = email))
                    responseMessage = response
                    showSuccessPopup = true
                    delay(2000)
                    showSuccessPopup = false
                } catch (e: Exception) {
                    responseMessage = "Error fetching newsletter response: ${e.message}"
                    showSuccessPopup = true
                    delay(2000)
                    showSuccessPopup = false
                }
            }
        }
    }

    fun dismissPopup() {
        showInvalidEmailPopup = false
        showSuccessPopup = false
    }

}