package com.example.storyapp.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.API.RetrofitClient
import com.example.storyapp.databinding.ActivityStoryBinding
import com.example.storyapp.story.adapter.StoryAdapter
import com.example.storyapp.story.upload.UploadStoryActivity
import com.example.storyapp.login.LoginActivity
import com.example.storyapp.maps.MapsActivity
import com.example.storyapp.story.detail.StoryDetailActivity
import com.example.storyapp.story.viewmodel.StoryViewModel
import com.example.storyapp.story.viewmodel.StoryViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.listToolBar)

        val apiService = RetrofitClient.instance
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        storyViewModel = ViewModelProvider(
            this,
            StoryViewModelFactory(apiService, sharedPreferences)
        ).get(StoryViewModel::class.java)

        setupRecyclerView()
        setupObservers()
        setupFabAnimation()
        loadStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, StoryDetailActivity::class.java).apply {
                putExtra("story_id", story.id)
            }
            startActivity(intent)
        }
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@StoryActivity)
            adapter = storyAdapter
        }
    }

    private fun setupObservers() {
        // Observe PagingData from ViewModel
        storyViewModel.stories.observe(this, Observer { pagingData ->
            Log.d("StoryActivity", "Received paging data: ${pagingData}")
            storyAdapter.submitData(lifecycle, pagingData)
            binding.rvStories.alpha = 0f
            binding.rvStories.animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        })

        // Observe error messages
        storyViewModel.errorMessage.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupFabAnimation() {
        val fab: FloatingActionButton = binding.btnAddStory

        fab.setOnClickListener {
            fab.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    fab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            val intent = Intent(this, UploadStoryActivity::class.java)
                            launcherAddStory.launch(intent)
                            overridePendingTransition(R.transition.slide_up, R.transition.fade_out)
                        }
                }
        }
    }

    private val launcherAddStory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                refreshStories()
            }
        }

    private fun loadStories() {
        val preferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = preferences.getString("token", null)
        if (token != null) {
            storyViewModel.fetchStories(token, page = 1, size = 15)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                animateRefresh()
                true
            }

            R.id.menu_logout -> {
                logout()
                true
            }

            R.id.menu_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun animateRefresh() {
        binding.rvStories.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                refreshStories()
            }
    }

    private fun refreshStories() {
        val preferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = preferences.getString("token", null)
        if (token != null) {
            storyViewModel.fetchStories(token, page = 1, size = 15)
        } else {
            Toast.makeText(this, "Token tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        binding.root.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                getSharedPreferences("user_session", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.transition.fade_in, R.transition.fade_out)
            }
    }

    private fun handleLogout() {
        // Clear Glide caches
        Glide.get(applicationContext).clearMemory()
        Thread {
            Glide.get(applicationContext).clearDiskCache()
        }.start()

        // Clear preferences and redirect to login
        getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        binding.btnAddStory.apply {
            alpha = 1f
            scaleX = 1f
            scaleY = 1f
        }
    }
}