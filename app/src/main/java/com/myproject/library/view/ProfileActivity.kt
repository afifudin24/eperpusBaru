package com.myproject.library.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myproject.library.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Mendapatkan UID pengguna yang sedang login
        val currentUserUid = firebaseAuth.currentUser?.uid

        // Mengambil data siswa dari Firestore berdasarkan UID pengguna
        if (currentUserUid != null) {
            val usersRef = firestore.collection("users")
            usersRef.document(currentUserUid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Data ditemukan, akses data nama siswa
                        val namaSiswa = documentSnapshot.getString("name")
                        val emailSiswa = documentSnapshot.getString("email")
                        val helo = findViewById<TextView>(R.id.helo)
                        val email = findViewById<TextView>(R.id.email)

                        helo.text = "Helo ${namaSiswa}!"
                        email.text = "${emailSiswa}!"
                    } else {
                        // Data tidak ditemukan
                        Log.d("Info", "Data tidak ditemukan untuk UID: $currentUserUid")
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mengambil data dari Firestore
                    Log.e("Error", "Gagal mengambil data: $exception")
                }
        }

    }

}