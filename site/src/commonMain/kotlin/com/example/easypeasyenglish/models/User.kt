package com.example.easypeasyenglish.models

// Shared declaration for both platforms
expect class User {
    val id: String
    val username: String
    val password: String
}

expect class UserWithoutPassword {
    val id: String
    val username: String
}
