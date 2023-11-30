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
import kotlinx.coroutines.tasks.await

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
       suspend fun register(name: String, email: String, password: String, nohp : String): Boolean {
            return try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                val admin = true

                // Jika registrasi berhasil, tambahkan data pengguna ke Firestore
                if (user != null) {
//                val address = binding.edtAddress.text.toString() // Mengambil alamat dari EditText

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "uid" to user.uid,
                        "nohp" to nohp,
                        "is_admin" to admin
                        // Tambahkan data lainnya sesuai kebutuhan
                    )

                    // Tambahkan dokumen 'user' dengan UID sebagai identifier di koleksi 'users'
                    usersCollection.document(user.uid).set(userData).await()
                    true // Registrasi berhasil
                } else {
                    false // Registrasi gagal
                }
            } catch (e: Exception) {
                // Penanganan kesalahan jika terjadi masalah pada registrasi atau penambahan data ke Firestore
                e.printStackTrace()
                false // Registrasi gagal
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.registerName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val nohp = binding.edNohpName.text.toString()

            lifecycleScope.launch { // Gunakan lifecycleScope untuk menjalankan suspend function
                val registrationResult = register(name, email, password, nohp)
                if (registrationResult) {
                    // Registrasi berhasil, tambahkan logika di sini
                    Toast.makeText(this@RegisterActivity, "Sukses, Anda Telah terdaftar", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    // Registrasi gagal, tambahkan logika di sini
                    Toast.makeText(this@RegisterActivity, "Ada Kesalahan", Toast.LENGTH_SHORT).show()

                }
            }
            
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
        val txtName = ObjectAnimator.ofFloat(binding.registerNameText, View.ALPHA, 1f).setDuration(100)
        val textInputName = ObjectAnimator.ofFloat(binding.nameEdtLayout, View.ALPHA, 1f).setDuration(100)
        val txtEmail = ObjectAnimator.ofFloat(binding.registerEmailTxt, View.ALPHA, 1f).setDuration(100)
        val textInputEmail = ObjectAnimator.ofFloat(binding.emailEdtLayout, View.ALPHA, 1f).setDuration(100)
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