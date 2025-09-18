package com.example.storyapp.unittesting

import com.example.storyapp.story.viewmodel.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = i.toString(),
                name = "name $i",
                description = "description $i",
                photoUrl = "photo_url_$i",
                createdAt = "2024-01-03T$i:00:00Z",
                lat = -6.200000,
                lon = 106.816666
            )
            items.add(story)
        }
        return items
    }
}