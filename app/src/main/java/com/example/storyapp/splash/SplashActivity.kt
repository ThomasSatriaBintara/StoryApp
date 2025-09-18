package com.example.storyapp.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.story.StoryActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = preferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            val intent = Intent(this, StoryActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        finish()
    }
}