package com.example.storyapp.register

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)