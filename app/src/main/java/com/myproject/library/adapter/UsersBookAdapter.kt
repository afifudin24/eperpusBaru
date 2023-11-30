package com.myproject.library.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myproject.library.R
import com.myproject.library.data.model.Books
import com.myproject.library.view.DetailActivity

class UsersBookAdapter(
    private val bookList: MutableList<Books>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<UsersBookAdapter.UsersBook>() {

    interface OnItemClickListener {
        fun onItemClick(book: Books)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersBook {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_users, parent, false)
        return UsersBook(view)
    }

    override fun onBindViewHolder(holder: UsersBook, position: Int) {
        val book = bookList[position]
        holder.bindView(book, clickListener) // Kirim data buku saat bindView dipanggil
    }

    override fun getItemCount(): Int = bookList.size

    inner class UsersBook(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val kategoriView: TextView = itemView.findViewById(R.id.tv_kategori)
        private val imageurl: ImageView = itemView.findViewById(R.id.iv_item_photo)

        fun bindView(book: Books, clickListener: OnItemClickListener) {
            titleTextView.text = book.judulbuku
            kategoriView.text = book.kategori
            val gambar = book.urlImage
            val storage = FirebaseStorage.getInstance()
            val storageRef: StorageReference = storage.reference.child("images/$gambar")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    Glide.with(itemView.context)
                        .load(downloadUrl)
                        .into(imageurl)
                }
                .addOnFailureListener { exception ->
                    // Handle kesalahan di sini
                }

            itemView.setOnClickListener {
                clickListener.onItemClick(book)
            }
        }
    }
}
