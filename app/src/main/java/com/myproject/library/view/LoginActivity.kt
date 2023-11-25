package com.myproject.library.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.myproject.library.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        lAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmailTxt.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }

        }

        binding.daftar.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // If sign-in fails, display a message to the user.
                    Toast.makeText(
                        this, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun lAnimation() {
        ObjectAnimator.ofFloat(binding.imageLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val button = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.loginMessage, View.ALPHA, 1f).setDuration(100)
        val txtEmail = ObjectAnimator.ofFloat(binding.loginEmailTxt, View.ALPHA, 1f).setDuration(100)
        val textInputEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).setDuration(100)
        val txtPassword = ObjectAnimator.ofFloat(binding.loginPassword, View.ALPHA, 1f).setDuration(100)
        val textInputPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, desc, txtEmail, textInputEmail, txtPassword, textInputPassword, button)
            start()
        }
    }
}