package com.myproject.library.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import com.myproject.library.data.model.Books

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val button = findViewById<Button>(R.id.btn_story)
        val pdfUrl = intent.getStringExtra("PDF_URL")
        button.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.setType("application/pdf")
            intent.setData(Uri.parse(pdfUrl))
            intent.putExtra("PDF_URL", pdfUrl)
            startActivity(intent)
        }

        val receivedIntent = intent
        val receivedBook = receivedIntent.getSerializableExtra("BOOK_ID") as? Books

        if (receivedBook != null) {
            val titleTextView = findViewById<TextView>(R.id.tv_detail_name)
            val authorTextView = findViewById<TextView>(R.id.tv_detail_description)
            val coverImageView = findViewById<ImageView>(R.id.iv_detail_photo)

            titleTextView.text = receivedBook.judulbuku
            authorTextView.text = receivedBook.penulis

            val gambar = receivedBook.urlImage
            val storage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.reference.child("images/$gambar")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    Glide.with(this)
                        .load(downloadUrl)
                        .into(coverImageView)
                }
                .addOnFailureListener { exception ->
                    // Handle kesalahan di sini
                }

        } else {
            Toast.makeText(this, "Data buku tidak tersedia", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}