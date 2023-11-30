package com.myproject.library.view

import UsersAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.myproject.library.R
import com.myproject.library.data.model.Users

class PenggunaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengguna)
        val kembali = findViewById<ImageView>(R.id.kembaliMenu)
        kembali.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        val db = FirebaseFirestore.getInstance()
        val userList = mutableListOf<Users>() // Ganti Book dengan model Anda
        val collectionReference = db.collection("users") // Ganti 'books' dengan nama koleksi Anda

        collectionReference.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(Users::class.java)
                    userList.add(user)
                }
                // Setelah Anda mendapatkan semua data, atur RecyclerView dan adapter di sini
                val recyclerView: RecyclerView = findViewById(R.id.rvPengguna) // Ganti dengan ID RecyclerView Anda
                val layoutManager = LinearLayoutManager(this)
                recyclerView.layoutManager = layoutManager
                val adapter = UsersAdapter(userList)

                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                // Handle kegagalan pengambilan data dari Firestore
            }



    }
}