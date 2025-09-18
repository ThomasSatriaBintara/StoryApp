package com.example.storyapp.register

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.R.layout.activity_register
import com.example.storyapp.API.RetrofitClient
import com.example.storyapp.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(activity_register)

        val logoImageView = findViewById<ImageView>(R.id.logo_profile)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Load logo image
        Glide.with(this)
            .load(R.drawable.ouroboros)
            .apply(
                RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true))
            .override(500, 500)
            .into(logoImageView)

        // Create clockwise rotation animation
        val rotateAnimation = ObjectAnimator.ofFloat(logoImageView, "rotation", 0f, -360f).apply {
            duration = 15000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        // Start rotation animation
        rotateAnimation.start()

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please input name, email, and password.", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show()
            } else {
                val registerRequest = RegisterRequest(name, email, password)

                RetrofitClient.instance.register(registerRequest)
                    .enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.body()?.error == false) {
                                Toast.makeText(this@RegisterActivity, "Register Successful.", Toast.LENGTH_SHORT).show()
                                navigateToLogin()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Registration failed.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(this@RegisterActivity, "Network error.", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    // Override back button press
    override fun onBackPressed() {
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToLogin() {
        // Clear Glide caches
        Glide.get(applicationContext).clearMemory()
        Thread {
            Glide.get(applicationContext).clearDiskCache()
        }.start()

        // Clear preferences and redirect
        getSharedPreferences("user_session", MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}