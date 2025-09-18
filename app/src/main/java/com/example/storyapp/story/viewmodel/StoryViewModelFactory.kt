package com.example.storyapp.story.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.API.ApiService

class StoryViewModelFactory(
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences,

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(apiService, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}