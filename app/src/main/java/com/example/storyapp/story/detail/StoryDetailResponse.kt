package com.example.storyapp.story.detail

import com.example.storyapp.story.viewmodel.Story

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)