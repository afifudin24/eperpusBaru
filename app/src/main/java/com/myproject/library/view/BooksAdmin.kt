package com.myproject.library.view

import BookAdapter
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
import com.myproject.library.data.model.Books

class BooksAdmin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books_admin)

        //back
        val back = findViewById<ImageView>(R.id.backMenu)
        back.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val db = FirebaseFirestore.getInstance()
        val bookList = mutableListOf<Books>() // Ganti Book dengan model Anda
        val collectionReference = db.collection("books") // Ganti 'books' dengan nama koleksi Anda

        collectionReference.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val book = document.toObject(Books::class.java)
                    bookList.add(book)
                }
                // Setelah Anda mendapatkan semua data, atur RecyclerView dan adapter di sini
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView) // Ganti dengan ID RecyclerView Anda
                val layoutManager = LinearLayoutManager(this)
                recyclerView.layoutManager = layoutManager
                val adapter = BookAdapter(bookList)

                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                // Handle kegagalan pengambilan data dari Firestore
            }


        val tambah = findViewById<Button>(R.id.tomboltambah)

        tambah.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}