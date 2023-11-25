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
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myproject.library.R
import com.myproject.library.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        rAnimation()
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

        binding.btnRegister.setOnClickListener {
            val name = binding.registerName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val nohp = binding.edNohpName.text.toString()
            
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun rAnimation() {
        ObjectAnimator.ofFloat(binding.imageRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val button = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val title = ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f).setDuration(100)
        val desc = ObjectAnimator.ofFloat(binding.registerMessage, View.ALPHA, 1f).setDuration(100)
        val txtName = ObjectAnimator.ofFloat(binding.registerNameTxt, View.ALPHA, 1f).setDuration(100)
        val textInputName = ObjectAnimator.ofFloat(binding.registerName, View.ALPHA, 1f).setDuration(100)
        val txtEmail = ObjectAnimator.ofFloat(binding.registerEmailTxt, View.ALPHA, 1f).setDuration(100)
        val textInputEmail = ObjectAnimator.ofFloat(binding.registerEmail, View.ALPHA, 1f).setDuration(100)
        val txtPassword = ObjectAnimator.ofFloat(binding.registerPasswordTxt, View.ALPHA, 1f).setDuration(100)
        val textInputPassword = ObjectAnimator.ofFloat(binding.registerPassword, View.ALPHA, 1f).setDuration(100)
        val textNoHP = ObjectAnimator.ofFloat(binding.nohpTXT3, View.ALPHA, 1f).setDuration(100)
        val textInputNoHP = ObjectAnimator.ofFloat(binding.edNohpName, View.ALPHA, 1f).setDuration(100)
        val textInputNoHPInput = ObjectAnimator.ofFloat(binding.nohpEdtLayout, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, desc,txtName, textInputName, txtEmail, textNoHP, textInputNoHPInput ,textInputNoHP, textInputEmail, txtPassword, textInputPassword, button)
            start()
        }
    }
}